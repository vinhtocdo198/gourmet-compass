package com.example.gourmetcompass.ui_restaurant_detail;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.adapters.BottomSheetCollectionsRVAdapter;
import com.example.gourmetcompass.adapters.ViewPagerAdapter;
import com.example.gourmetcompass.firebase.FirestoreUtil;
import com.example.gourmetcompass.models.Restaurant;
import com.example.gourmetcompass.models.MyCollection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RestaurantDetailActivity extends AppCompatActivity {

    private static final String TAG = "RestaurantDetailActivity";
    private final String[] titles = {"Detail", "Menu", "Gallery", "Review"};
    FirebaseFirestore db;
    FirebaseUser user;
    ViewPagerAdapter viewPagerAdapter;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ImageButton plusBtn, searchBtn, backBtn;
    Restaurant restaurant;
    TextView resName;
    ArrayList<BottomSheetDialog> bottomSheets;
    ArrayList<MyCollection> collList;
    String restaurantId, reviewerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        // Init firebase services
        db = FirestoreUtil.getInstance().getFirestore();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            reviewerId = user.getUid(); // Get current user id
        }

        // Get restaurant id from intent
        restaurantId = getIntent().getStringExtra("restaurantId");

        // Init views
        initViews();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.stay_still, R.anim.slide_out);
            }
        });

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheet();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(1);
            }
        });

        // Fetch details of the restaurant
        getRestaurantDetail(restaurantId);

    }

    private void initViews() {
        resName = findViewById(R.id.res_name_detail);
        plusBtn = findViewById(R.id.btn_plus);
        searchBtn = findViewById(R.id.btn_search);
        backBtn = findViewById(R.id.btn_back);
        viewPager2 = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        bottomSheets = new ArrayList<>();
        collList = new ArrayList<>(); // TODO: might change back
    }

    private void openBottomSheet() {
        BottomSheetDialog outerBottomSheet = new BottomSheetDialog(RestaurantDetailActivity.this, R.style.BottomSheetTheme);
        View outerSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_restaurant, findViewById(R.id.btms_res_container));
        outerBottomSheet.setContentView(outerSheetView);
        outerBottomSheet.show();
        bottomSheets.add(outerBottomSheet);

        // Bottom sheet buttons
        Button addToCollBtn = outerSheetView.findViewById(R.id.btn_add_btms_res);
        Button addReviewBtn = outerSheetView.findViewById(R.id.btn_add_review_btms_res);

        addToCollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: if a collection exists
                // Open inner bottom sheet
                BottomSheetDialog existCollBottomSheet = new BottomSheetDialog(RestaurantDetailActivity.this, R.style.BottomSheetTheme);
                View existCollSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_exist_coll, findViewById(R.id.bottom_sheet_exist_coll_container));
                existCollBottomSheet.setContentView(existCollSheetView);
                existCollBottomSheet.show();
                bottomSheets.add(existCollBottomSheet);

                Button addNewCollBtn = existCollSheetView.findViewById(R.id.btms_exist_btn_add_new);
                Button existDoneBtn = existCollSheetView.findViewById(R.id.btms_exist_btn_done);

                // New collection button in the second bottom sheet
                addNewCollBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BottomSheetDialog newCollBottomSheet = new BottomSheetDialog(RestaurantDetailActivity.this, R.style.BottomSheetTheme);
                        View newCollSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_new_coll, findViewById(R.id.bottom_sheet_new_coll_container));
                        newCollBottomSheet.setContentView(newCollSheetView);
                        newCollBottomSheet.show();
                        bottomSheets.add(newCollBottomSheet);

                        Button newDoneBtn = newCollSheetView.findViewById(R.id.btms_new_btn_done);
                        EditText textField = newCollBottomSheet.findViewById(R.id.btms_new_text_field);

                        newDoneBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (textField != null && textField.getText().toString().isEmpty()) {
                                    Toast.makeText(RestaurantDetailActivity.this, "Collection must have a name", Toast.LENGTH_SHORT).show();
                                } else {
                                    dismissAllBottomSheets();
                                    // TODO: add to coll
                                    Toast.makeText(RestaurantDetailActivity.this, "Added to collection", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                // Recycler view of user collections and done button to add to collections
                showUserCollList(existCollSheetView);
                existDoneBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addToExistingResColl();
                        Toast.makeText(RestaurantDetailActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();
                        dismissAllBottomSheets();
                    }
                });
            }
        });

        addReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(3);
                outerBottomSheet.dismiss();
                openAddReviewDialog();
            }
        });
    }

    private void addToExistingResColl() {
        for (MyCollection coll : collList) {
            if (coll.isChecked() && !coll.getRestaurants().contains(restaurantId)) {
                coll.getRestaurants().add(restaurantId);
                updateUserCollRes(coll);
            } else if (!coll.isChecked()) {
                coll.getRestaurants().remove(restaurantId);
                updateUserCollRes(coll);
            }
        }
    }

    private void updateUserCollRes(MyCollection coll) {
        db.collection("users")
                .document(user.getUid())
                .collection("collections")
                .document(coll.getId())
                .update("restaurants", coll.getRestaurants())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Changes saved successfully");
                    } else {
                        Log.e(TAG, "Failed to add restaurant to collection", task.getException());
                    }
                });
    }

    private void showUserCollList(View existCollSheetView) {
        RecyclerView recyclerView = existCollSheetView.findViewById(R.id.btms_exist_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(RestaurantDetailActivity.this));
        BottomSheetCollectionsRVAdapter adapter = new BottomSheetCollectionsRVAdapter(RestaurantDetailActivity.this, collList, restaurantId);
        recyclerView.setAdapter(adapter);
        fetchCollectionList(adapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchCollectionList(BottomSheetCollectionsRVAdapter adapter) {
        db.collection("users")
                .document(user.getUid())
                .collection("collections")
                .whereEqualTo("type", "restaurant")
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

    private void openAddReviewDialog() {
        // Create a dialog for adding a review
        Dialog reviewDialog = new Dialog(RestaurantDetailActivity.this);
        reviewDialog.setContentView(R.layout.dialog_add_review);

        // Set the dialog width and height
        Window window = reviewDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            layoutParams.width = (int) (metrics.widthPixels * 0.9); // 90% of screen width
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        }
        reviewDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Init views
        Button cancelBtn = reviewDialog.findViewById(R.id.btn_cancel_add_review);
        Button submitBtn = reviewDialog.findViewById(R.id.btn_submit_add_review);
        Button uploadImg = reviewDialog.findViewById(R.id.btn_upload_add_review);
        EditText reviewTextField = reviewDialog.findViewById(R.id.dialog_text_add_review);
        RatingBar ratingBar = reviewDialog.findViewById(R.id.rating_bar_add_review);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewDialog.dismiss();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reviewContent = reviewTextField.getText().toString();
                String ratings = String.valueOf(ratingBar.getRating());
                if (reviewContent.isEmpty()) {
                    Toast.makeText(RestaurantDetailActivity.this, "Review cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                submitReview(reviewContent, ratings);

                reviewDialog.dismiss();
            }
        });

        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Handle the upload image action
            }
        });

        reviewDialog.show();
    }

    private void submitReview(String reviewContent, String ratings) {
        // Create a new review object
        Map<String, Object> review = new HashMap<>();
        review.put("timestamp", System.currentTimeMillis());
        review.put("description", reviewContent);
        review.put("ratings", ratings);
        review.put("reviewerId", reviewerId);
        review.put("likedUserIds", new ArrayList<String>());
        review.put("dislikedUserIds", new ArrayList<String>());

        // Add to db
        db.collection("restaurants").document(restaurantId).
                collection("reviews")
                .add(review)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RestaurantDetailActivity.this, "Review added", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RestaurantDetailActivity.this, "Failed to add review", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void switchFragment(int index) {
        TabLayout.Tab tab = tabLayout.getTabAt(index);
        if (tab != null) {
            tab.select();
        }
    }

    private void dismissAllBottomSheets() {
        for (BottomSheetDialog bottomSheet : bottomSheets) {
            if (bottomSheet != null && bottomSheet.isShowing()) {
                bottomSheet.dismiss();
            }
        }
        bottomSheets.clear();
    }

    private void getRestaurantDetail(String restaurantId) {
        db.collection("restaurants").document(restaurantId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                restaurant = documentSnapshot.toObject(Restaurant.class);

                                if (restaurant != null) {
                                    resName.setText(restaurant.getName());
                                }
                                viewPagerAdapter = new ViewPagerAdapter(RestaurantDetailActivity.this, restaurantId);
                                viewPager2.setAdapter(viewPagerAdapter);
                                new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> tab.setText(titles[position])).attach();

                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}