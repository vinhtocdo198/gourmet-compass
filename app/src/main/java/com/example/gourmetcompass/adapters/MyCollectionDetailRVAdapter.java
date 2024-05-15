package com.example.gourmetcompass.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.utils.FirestoreUtil;
import com.example.gourmetcompass.models.Dish;
import com.example.gourmetcompass.models.Restaurant;
import com.example.gourmetcompass.ui_personal.MyCollectionDetailActivity;
import com.example.gourmetcompass.ui_restaurant_detail.RestaurantDetailActivity;
import com.example.gourmetcompass.utils.BottomSheetUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MyCollectionDetailRVAdapter extends RecyclerView.Adapter<MyCollectionDetailRVAdapter.MyViewHolder> {

    private final String TAG = "MyCollectionDetailRVAdapter";
    Context context;
    ArrayList<Object> itemList;
    FirebaseFirestore db;
    FirebaseUser user;
    String userId, collectionId;

    public MyCollectionDetailRVAdapter(Context context, ArrayList<Object> itemList, String collectionId) {
        this.context = context;
        this.itemList = itemList;
        this.collectionId = collectionId;
    }

    @NonNull
    @Override
    public MyCollectionDetailRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_coll_detail, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCollectionDetailRVAdapter.MyViewHolder holder, int position) {
        Object item = itemList.get(position);

        // Init firebase services
        db = FirestoreUtil.getInstance().getFirestore();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

        if (item instanceof Restaurant) {
            Restaurant restaurant = (Restaurant) item;
            holder.itemName.setText(restaurant.getName());
            holder.itemDesc.setText(restaurant.getDescription());
            setResRatings(holder, (Restaurant) item);
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(holder.itemView.getContext(), RestaurantDetailActivity.class);
                intent.putExtra("restaurantId", restaurant.getId());
                holder.itemView.getContext().startActivity(intent);
                if (holder.itemView.getContext() instanceof Activity) {
                    ((Activity) holder.itemView.getContext()).overridePendingTransition(R.anim.slide_in, R.anim.stay_still);
                }
            });
        } else if (item instanceof Dish) {
            Dish dish = (Dish) item;
            holder.itemName.setText(dish.getName());
            holder.itemDesc.setText(dish.getDescription());
            holder.itemRatings.setText(String.valueOf(dish.getRatings()));
            holder.itemRatingCount.setText(String.format(context.getString(R.string.rating_count), dish.getRatingCount()));
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(holder.itemView.getContext(), RestaurantDetailActivity.class);
                intent.putExtra("restaurantId", dish.getRestaurantId());
                holder.itemView.getContext().startActivity(intent);
                if (holder.itemView.getContext() instanceof Activity) {
                    ((Activity) holder.itemView.getContext()).overridePendingTransition(R.anim.slide_in, R.anim.stay_still);
                }
            });
        }

        holder.btnMore.setOnClickListener(v -> openRemoveBtms(holder, item));
    }

    private void openRemoveBtms(@NonNull MyViewHolder holder, Object item) {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(context, R.style.BottomSheetTheme);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_remove, bottomSheet.findViewById(R.id.btms_remove_coll));
        bottomSheet.setContentView(view);
        BottomSheetUtil.openBottomSheet(bottomSheet);

        Button btnRemove = view.findViewById(R.id.btms_btn_remove_from_coll);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) {
                    return;
                }
                if (item instanceof Restaurant) {
                    db.collection("users")
                            .document(userId)
                            .collection("collections")
                            .document(collectionId)
                            .update("restaurants", FieldValue.arrayRemove(((Restaurant) item).getId()))
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    itemList.remove(pos);
                                    notifyDataSetChanged();
                                    Toast.makeText(context, "Removed from collection", Toast.LENGTH_SHORT).show();
                                    ((MyCollectionDetailActivity) context).setLayout(itemList.size());
                                } else {
                                    Log.e(TAG, "Error getting restaurant from collection", task.getException());
                                }
                            });
                } else if (item instanceof Dish) {
                    db.collection("users")
                            .document(userId)
                            .collection("collections")
                            .document(collectionId)
                            .update("dishes", FieldValue.arrayRemove(((Dish) item).getId()))
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    itemList.remove(pos);
                                    notifyDataSetChanged();
                                    Toast.makeText(context, "Removed from collection", Toast.LENGTH_SHORT).show();
                                    ((MyCollectionDetailActivity) context).setLayout(itemList.size());
                                } else {
                                    Log.e(TAG, "Error getting restaurant from collection", task.getException());
                                }
                            });
                }
                bottomSheet.dismiss();
            }
        });
    }

    private void setResRatings(@NonNull MyViewHolder holder, Restaurant item) {
        db.collection("restaurants")
                .document(item.getId())
                .collection("reviews")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int reviewCount = task.getResult().size();
                        holder.itemRatingCount.setText(String.format(context.getString(R.string.rating_count), reviewCount));
                        if (reviewCount > 0) {
                            holder.itemRatings.setText(String.format(context.getString(R.string.item_ratings), Float.parseFloat(item.getRatings())));
                        } else {
                            holder.itemRatings.setText("N/A");
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImg;
        ImageButton btnMore;
        TextView itemName, itemDesc, itemRatings, itemRatingCount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImg = itemView.findViewById(R.id.my_coll_detail_ava);
            itemName = itemView.findViewById(R.id.my_coll_detail_title);
            itemDesc = itemView.findViewById(R.id.my_coll_detail_desc);
            itemRatings = itemView.findViewById(R.id.my_coll_detail_ratings);
            itemRatingCount = itemView.findViewById(R.id.my_coll_detail_rating_count);
            btnMore = itemView.findViewById(R.id.my_coll_detail_btn_more);
        }
    }
}
