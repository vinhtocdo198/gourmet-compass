package com.example.gourmetcompass.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.utils.FirestoreUtil;
import com.example.gourmetcompass.models.Dish;
import com.example.gourmetcompass.models.MyCollection;
import com.example.gourmetcompass.utils.BottomSheetUtil;
import com.example.gourmetcompass.utils.EditTextUtil;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MenuRVAdapter extends RecyclerView.Adapter<MenuRVAdapter.MyViewHolder> {

    private static final String TAG = "MenuRVAdapter";
    Context context;
    ArrayList<Dish> menu;
    ArrayList<BottomSheetDialog> bottomSheets;
    FirebaseFirestore db;
    FirebaseUser user;
    ArrayList<MyCollection> collList;
    String restaurantId;

    public MenuRVAdapter(Context context, ArrayList<Dish> menu, String restaurantId) {
        this.context = context;
        this.menu = menu;
        this.restaurantId = restaurantId;
    }

    @NonNull
    @Override
    public MenuRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuRVAdapter.MyViewHolder holder, int position) {
        Dish dish = menu.get(position);

        // Init firebase services
        db = FirestoreUtil.getInstance().getFirestore();
        user = FirebaseAuth.getInstance().getCurrentUser();

        bottomSheets = new ArrayList<>();
        collList = new ArrayList<>();
        holder.dishName.setText(dish.getName());
        holder.dishDesc.setText(dish.getDescription());
        setDishRatings(holder, dish);
        holder.dishImgBtn.setOnClickListener(v -> openBottomSheet(holder, dish));
    }

    private void setDishRatings(@NonNull MyViewHolder holder, Dish dish) {
        db.collection("restaurants")
                .document(restaurantId)
                .collection("dishes")
                .document(dish.getId())
                .collection("ratings")
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Failed to fetch ratings", e);
                        return;
                    }
                    if (value != null) {
                        int size = value.size();
                        float totalRating = 0;
                        for (QueryDocumentSnapshot doc : value) {
                            totalRating += Float.parseFloat(doc.getString("rate"));
                        }
                        if (size == 0) {
                            holder.dishRatings.setText("N/A");
                        } else {
                            float avgRating = totalRating / size;
                            holder.dishRatings.setText(String.valueOf(avgRating));
                        }
                        holder.dishRatingCount.setText(String.format(context.getString(R.string.rating_count), size));
                    }
                });
    }

    private void openBottomSheet(@NonNull MyViewHolder holder, Dish dish) {
        BottomSheetDialog outerBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetTheme);
        View outerSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dish, holder.itemView.findViewById(R.id.btms_dish_container));
        outerBottomSheet.setContentView(outerSheetView);
        BottomSheetUtil.openBottomSheet(outerBottomSheet);
        bottomSheets.add(outerBottomSheet);

        Button addToCollBtn = outerSheetView.findViewById(R.id.btn_add_btms_dish);
        Button addReviewBtn = outerSheetView.findViewById(R.id.btn_rate_btms_dish);

        addToCollBtn.setOnClickListener(v -> db.collection("users")
                .document(user.getUid())
                .collection("collections")
                .whereEqualTo("type", "dish")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            openNewCollBtms(holder, dish);
                        } else {
                            openExistingCollBtms(holder, dish);
                        }
                    } else {
                        Log.e(TAG, "Failed to fetch collections", task.getException());
                    }
                }));

        addReviewBtn.setOnClickListener(v -> {
            BottomSheetDialog rateBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetTheme);
            View reviewSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_rate_dish, holder.itemView.findViewById(R.id.btms_rate_dish_container));
            rateBottomSheet.setContentView(reviewSheetView);
            BottomSheetUtil.openBottomSheet(rateBottomSheet);
            bottomSheets.add(rateBottomSheet);

            Button cancelBtn = reviewSheetView.findViewById(R.id.btn_cancel_rate_dish);
            Button submitBtn = reviewSheetView.findViewById(R.id.btn_submit_rate_dish);
            RatingBar ratingBar = reviewSheetView.findViewById(R.id.rating_bar_dish);
            setExistingRating(dish, ratingBar);

            cancelBtn.setOnClickListener(v1 -> rateBottomSheet.dismiss());
            submitBtn.setOnClickListener(v2 -> {
                String ratings = String.valueOf(ratingBar.getRating());
                submitRating(dish.getId(), ratings);
                dismissAllBottomSheets();
            });
        });
    }

    private void setExistingRating(Dish dish, RatingBar ratingBar) {
        db.collection("restaurants")
                .document(restaurantId)
                .collection("dishes")
                .document(dish.getId())
                .collection("ratings")
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // The user has already rated the dish, so set the rating of the RatingBar
                            String rating = task.getResult().getDocuments().get(0).getString("rate");
                            ratingBar.setRating(Float.parseFloat(rating));
                        }
                    } else {
                        Log.e(TAG, "Failed to fetch user rating", task.getException());
                    }
                });
    }

    private void submitRating(String dishId, String ratings) {
        // Check if the user has already rated the dish
        db.collection("restaurants")
                .document(restaurantId)
                .collection("dishes")
                .document(dishId)
                .collection("ratings")
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            // The user hasn't rated the dish yet, so create a new rating
                            addNewDishRating(dishId, ratings);
                        } else {
                            // The user has already rated the dish, so update the existing rating
                            updateDishRating(dishId, ratings, task);
                        }
                    } else {
                        Log.e(TAG, "Failed to check if user has already rated the dish", task.getException());
                    }
                });
    }

    private void updateDishRating(String dishId, String ratings, Task<QuerySnapshot> task) {
        String ratingId = task.getResult().getDocuments().get(0).getId();

        db.collection("restaurants")
                .document(restaurantId)
                .collection("dishes")
                .document(dishId)
                .collection("ratings")
                .document(ratingId)
                .update("rate", ratings)
                .addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        Toast.makeText(context, "Rating updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Failed to update rating", updateTask.getException());
                    }
                });
    }

    private void addNewDishRating(String dishId, String ratings) {
        Map<String, Object> newRating = new HashMap<>();
        newRating.put("userId", user.getUid());
        newRating.put("rate", ratings);

        db.collection("restaurants")
                .document(restaurantId)
                .collection("dishes")
                .document(dishId)
                .collection("ratings")
                .add(newRating)
                .addOnCompleteListener(addTask -> {
                    if (addTask.isSuccessful()) {
                        Toast.makeText(context, "Rating added", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Failed to submit rating", addTask.getException());
                    }
                });
    }

    private void openExistingCollBtms(@NonNull MyViewHolder holder, Dish dish) {
        // Open inner bottom sheet
        BottomSheetDialog existCollBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetTheme);
        View existCollSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_exist_coll, holder.itemView.findViewById(R.id.bottom_sheet_exist_coll_container));
        existCollBottomSheet.setContentView(existCollSheetView);
        BottomSheetUtil.openBottomSheet(existCollBottomSheet);
        bottomSheets.add(existCollBottomSheet);

        Button addNewCollBtn = existCollSheetView.findViewById(R.id.btms_exist_btn_add_new);
        Button existDoneBtn = existCollSheetView.findViewById(R.id.btms_exist_btn_done);

        // New collection button in the second bottom sheet
        addNewCollBtn.setOnClickListener(v -> openNewCollBtms(holder, dish));

        // Recycler view of user collections and done button to add to collections
        showUserCollList(existCollSheetView, dish);
        existDoneBtn.setOnClickListener(v -> {
            addToExistingDishColl(dish);
            Toast.makeText(context, "Changes saved", Toast.LENGTH_SHORT).show();
            dismissAllBottomSheets();
        });
    }

    private void updateUserCollDish(MyCollection coll) {
        db.collection("users")
                .document(user.getUid())
                .collection("collections")
                .document(coll.getId())
                .update("dishes", coll.getDishes())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Changes saved successfully");
                    } else {
                        Log.e(TAG, "Failed to add restaurant to collection", task.getException());
                    }
                });
    }

    private void addToExistingDishColl(Dish dish) {
        for (MyCollection coll : collList) {
            if (coll.isChecked() && !coll.getDishes().contains(dish.getId())) {
                coll.getDishes().add(dish.getId());
                updateUserCollDish(coll);
            } else if (!coll.isChecked()) {
                coll.getDishes().remove(dish.getId());
                updateUserCollDish(coll);
            }
        }
    }

    private void openNewCollBtms(@NonNull MyViewHolder holder, Dish dish) {
        BottomSheetDialog newCollBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetTheme);
        View newCollSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_new_coll, holder.itemView.findViewById(R.id.bottom_sheet_new_coll_container));
        newCollBottomSheet.setContentView(newCollSheetView);
        BottomSheetUtil.openBottomSheet(newCollBottomSheet);
        bottomSheets.add(newCollBottomSheet);

        Button newDoneBtn = newCollSheetView.findViewById(R.id.btms_new_btn_done);
        EditTextUtil nameTextField = newCollSheetView.findViewById(R.id.btms_new_text_field);
        nameTextField.setHeight(150);
        nameTextField.setHint("Enter collection name");
        setDefaultNewCollName(nameTextField);

        // Add the restaurant to the newly created collection and all checked collections
        newDoneBtn.setOnClickListener(v -> {
            String collName = nameTextField.getText();
            if (collName.isEmpty()) {
                Toast.makeText(context, "Collection must have a name", Toast.LENGTH_SHORT).show();
            } else {
                addToNewDishColl(collName, dish);
                addToExistingDishColl(dish);
                dismissAllBottomSheets();
                Toast.makeText(context, "Added to collection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToNewDishColl(String collName, Dish dish) {
        Map<String, Object> newColl = new HashMap<>();
        newColl.put("type", "dish");
        newColl.put("name", collName);
        ArrayList<String> dishList = new ArrayList<>();
        dishList.add(dish.getId());
        newColl.put("dishes", dishList);
        db.collection("users")
                .document(user.getUid())
                .collection("collections")
                .add(newColl)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Changes saved successfully");
                    } else {
                        Log.e(TAG, "Failed to add collection", task.getException());
                    }
                });
    }

    private void showUserCollList(View existCollSheetView, Dish dish) {
        RecyclerView recyclerView = existCollSheetView.findViewById(R.id.btms_exist_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        BottomSheetCollectionsRVAdapter adapter = new BottomSheetCollectionsRVAdapter(context, collList, dish.getId(), "dish");
        recyclerView.setAdapter(adapter);
        fetchCollectionList(adapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchCollectionList(BottomSheetCollectionsRVAdapter adapter) {
        db.collection("users")
                .document(user.getUid())
                .collection("collections")
                .whereEqualTo("type", "dish")
                .orderBy("name")
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error fetching collections", e);
                        return;
                    }
                    collList.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            MyCollection collection = doc.toObject(MyCollection.class);
                            collection.setId(doc.getId());
                            collList.add(collection);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void setDefaultNewCollName(EditTextUtil textField) {
        db.collection("users")
                .document(user.getUid())
                .collection("collections")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && textField != null) {
                        textField.setText(context.getString(R.string.my_collection, task.getResult().size() + 1));
                    } else {
                        Log.e(TAG, "Failed to fetch collections", task.getException());
                    }
                });
    }

    private void dismissAllBottomSheets() {
        for (BottomSheetDialog bottomSheet : bottomSheets) {
            if (bottomSheet != null && bottomSheet.isShowing()) {
                bottomSheet.dismiss();
            }
        }
        bottomSheets.clear();
    }

    @Override
    public int getItemCount() {
        return menu.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView dishName, dishDesc, dishRatingCount, dishRatings;
        ImageView dishImg;
        ImageButton dishImgBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            dishName = itemView.findViewById(R.id.dish_title);
            dishDesc = itemView.findViewById(R.id.dish_desc);
            dishRatings = itemView.findViewById(R.id.dish_ratings);
            dishRatingCount = itemView.findViewById(R.id.dish_rating_count);
            dishImg = itemView.findViewById(R.id.dish_img);
            dishImgBtn = itemView.findViewById(R.id.dish_btn_more);
            // TODO: Add image here
        }
    }
}
