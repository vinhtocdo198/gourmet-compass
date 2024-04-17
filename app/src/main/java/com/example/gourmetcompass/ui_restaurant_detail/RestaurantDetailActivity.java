package com.example.gourmetcompass.ui_restaurant_detail;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.gourmetcompass.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class RestaurantDetailActivity extends AppCompatActivity {

    // Tab layout
    ViewPagerAdapter viewPagerAdapter;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    private final String[] titles = {"Detail", "Menu", "Gallery", "Review"};

    // Buttons
    ImageButton plusBtn;
    ImageButton searchBtn;
    ImageButton backBtn;

    // Bottom sheets
    List<BottomSheetDialog> bottomSheets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        // Set tab layout
        viewPager2 = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> tab.setText(titles[position])).attach();


        plusBtn = findViewById(R.id.btn_plus);
        searchBtn = findViewById(R.id.btn_search);
        backBtn = findViewById(R.id.btn_back);

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheet();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToFragment(1);
            }
        });
    }

    private void openBottomSheet() {
        BottomSheetDialog outerBottomSheet = new BottomSheetDialog(RestaurantDetailActivity.this, R.style.BottomSheetTheme);
        View outerSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_restaurant, findViewById(R.id.bottom_sheet_container));
        outerBottomSheet.setContentView(outerSheetView);
        outerBottomSheet.show();
        bottomSheets.add(outerBottomSheet);

        // Bottom sheet buttons
        Button addToCollBtn = outerSheetView.findViewById(R.id.btn_add);
        Button addReviewBtn = outerSheetView.findViewById(R.id.btn_review);

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
                navigateToFragment(3);
                outerBottomSheet.dismiss();

                // Open add review dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(RestaurantDetailActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_review, null);
                builder.setView(dialogView);
                AlertDialog reviewDialog = builder.create();
                showDialog(reviewDialog);

                Button cancelBtn = dialogView.findViewById(R.id.btn_cancel);
                Button submitBtn = dialogView.findViewById(R.id.btn_submit);

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reviewDialog.dismiss();
                    }
                });

                submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: submit review
                    }
                });
            }
        });
    }

    private void navigateToFragment(int index) {
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

    private void showDialog(AlertDialog dialog) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (dialog.getWindow() != null) {
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setAttributes(layoutParams);
            dialog.show();
        }
    }
}