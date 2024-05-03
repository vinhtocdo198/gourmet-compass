package com.example.gourmetcompass.ui_restaurant_detail;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.adapters.ViewPagerAdapter;
import com.example.gourmetcompass.firebase.FirestoreUtil;
import com.example.gourmetcompass.models.Restaurant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RestaurantDetailActivity extends AppCompatActivity {

    private static final String TAG = "RestaurantDetailActivity";
    private final String[] titles = {"Detail", "Menu", "Gallery", "Review"};
    FirebaseFirestore db;
    ViewPagerAdapter viewPagerAdapter;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ImageButton plusBtn, searchBtn, backBtn;
    Restaurant restaurant;
    TextView resName;
    List<BottomSheetDialog> bottomSheets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        // Init db instance
        db = FirestoreUtil.getInstance().getFirestore();

        // Init views
        initViews();
        bottomSheets = new ArrayList<>();

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

        // Get restaurant id from intent
        String restaurantId = getIntent().getStringExtra("restaurantId");

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

                View itemColl = existCollSheetView.findViewById(R.id.item_collection);
                TextView textView = itemColl.findViewById(R.id.coll_name);
                CheckBox checkBox = itemColl.findViewById(R.id.checkbox);

                textView.setText(R.string.favorites_restaurants);
                checkBox.setChecked(true);
                existCollBottomSheet.setContentView(existCollSheetView);
                existCollBottomSheet.show();
                bottomSheets.add(existCollBottomSheet);

                Button addNewCollBtn = existCollSheetView.findViewById(R.id.btn_add_new);
                Button existDoneBtn = existCollSheetView.findViewById(R.id.btn_exist_done);

                addNewCollBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BottomSheetDialog newCollBottomSheet = new BottomSheetDialog(RestaurantDetailActivity.this, R.style.BottomSheetTheme);
                        View newCollSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_new_coll, findViewById(R.id.bottom_sheet_new_coll_container));
                        newCollBottomSheet.setContentView(newCollSheetView);
                        newCollBottomSheet.show();
                        bottomSheets.add(newCollBottomSheet);


                        Button newDoneBtn = newCollSheetView.findViewById(R.id.btn_new_done);
                        EditText textField = newCollBottomSheet.findViewById(R.id.text_field);

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

                existDoneBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: error if no collection was chosen
                        dismissAllBottomSheets();
                        Toast.makeText(RestaurantDetailActivity.this, "Added to collection", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        addReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(3);
                outerBottomSheet.dismiss();

                addAReview();
            }
        });
    }

    private void addAReview() {
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

        Button cancelBtn = reviewDialog.findViewById(R.id.btn_cancel_add_review);
        Button submitBtn = reviewDialog.findViewById(R.id.btn_submit_add_review);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog when the cancel button is clicked
                reviewDialog.dismiss();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Handle the submit action
                // For example, you can get the text from the EditText and use it to add a review

                // Dismiss the dialog when the submit action is done
                reviewDialog.dismiss();
            }
        });

        reviewDialog.show();
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