package com.example.gourmetcompass.ui_restaurant_detail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.adapters.BottomSheetCollectionsRVAdapter;
import com.example.gourmetcompass.adapters.ViewPagerAdapter;
import com.example.gourmetcompass.utils.FirestoreUtil;
import com.example.gourmetcompass.models.MyCollection;
import com.example.gourmetcompass.models.Restaurant;
import com.example.gourmetcompass.utils.BottomSheetUtil;
import com.example.gourmetcompass.utils.EditTextUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    LinearLayout appBarLayout;
    ImageView resImgBg;
    EditTextUtil searchBarUtil;
    ConstraintLayout resDetailContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        // Init firebase services
        db = FirestoreUtil.getInstance().getFirestore();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            reviewerId = user.getUid();
        }

        // Get restaurant id from intent
        restaurantId = getIntent().getStringExtra("restaurantId");

        // Init views
        initViews();

        backBtn.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.stay_still, R.anim.slide_out);
        });

        plusBtn.setOnClickListener(v -> openBottomSheet());

        searchBtn.setOnClickListener(v -> setUpSearchBar());
        hideKeyboardAndLoseFocusTabLayout();

        // Fetch details of the restaurant
        getRestaurantDetail(restaurantId);

    }

    private void setUpSearchBar() {
        switchFragment(1);
        searchBarUtil.setVisibility(View.VISIBLE);
        appBarLayout.setVisibility(View.GONE);

        searchBarUtil.setHint("Search menu");
        searchBarUtil.setSearchBarBackground();
        searchBarUtil.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchBarUtil.getTextField(), InputMethodManager.SHOW_IMPLICIT);
        searchBarUtil.getTextField().setOnFocusChangeListener((v2, hasFocus) -> {
            if (hasFocus) {
                searchBarUtil.setIconEnd(R.drawable.ic_clear_text);
                searchBarUtil.getIconEnd().setOnClickListener(v12 -> searchBarUtil.getTextField().setText(""));
            } else {
                searchBarUtil.setText("");
                searchBarUtil.setVisibility(View.GONE);
                appBarLayout.setVisibility(View.VISIBLE);
            }
        });
        searchBarUtil.getTextField().setOnEditorActionListener(this::showSearchResults);
        hideKeyboardAndLoseFocus(resDetailContainer);
    }

    private void hideKeyboardAndLoseFocusTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                searchBarUtil.setVisibility(View.GONE);
                appBarLayout.setVisibility(View.VISIBLE);
                searchBarUtil.getTextField().clearFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(searchBarUtil.getTextField().getWindowToken(), 0);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void hideKeyboardAndLoseFocus(View container) {
        container.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.performClick();
            }
            searchBarUtil.getTextField().clearFocus();
            InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(searchBarUtil.getTextField().getWindowToken(), 0);
            return false;
        });
    }

    private boolean showSearchResults(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE
                || actionId == EditorInfo.IME_ACTION_SEARCH
                || (event != null
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

            String query = v.getText().toString();
            // Pass the search query to the menu fragment
            RestaurantMenuFragment restaurantMenuFragment = viewPagerAdapter.getRestaurantMenuFragment();
            restaurantMenuFragment.updateSearchQuery(query);

            return true;
        }
        return false;
    }

    private void initViews() {
        resName = findViewById(R.id.res_name_detail);
        plusBtn = findViewById(R.id.btn_plus);
        searchBtn = findViewById(R.id.btn_search);
        backBtn = findViewById(R.id.btn_back);
        viewPager2 = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        appBarLayout = findViewById(R.id.app_bar_layout);
        resImgBg = findViewById(R.id.res_img_bg);
        resDetailContainer = findViewById(R.id.res_detail_container);
        bottomSheets = new ArrayList<>();
        collList = new ArrayList<>();
        searchBarUtil = findViewById(R.id.search_bar_menu);
        searchBarUtil.setVisibility(View.GONE);
    }

    private void openBottomSheet() {
        BottomSheetDialog outerBottomSheet = new BottomSheetDialog(RestaurantDetailActivity.this, R.style.BottomSheetTheme);
        View outerSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_restaurant, findViewById(R.id.btms_res_container));
        outerBottomSheet.setContentView(outerSheetView);
        BottomSheetUtil.openBottomSheet(outerBottomSheet);
        bottomSheets.add(outerBottomSheet);

        // Bottom sheet buttons
        Button addToCollBtn = outerSheetView.findViewById(R.id.btn_add_btms_res);
        Button addReviewBtn = outerSheetView.findViewById(R.id.btn_add_review_btms_res);

        addToCollBtn.setOnClickListener(v -> db.collection("users")
                .document(user.getUid())
                .collection("collections")
                .whereEqualTo("type", "restaurant")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            openNewCollBtms();
                        } else {
                            openExistingCollBtms();
                        }
                    } else {
                        Log.e(TAG, "Failed to fetch collections", task.getException());
                    }
                }));

        addReviewBtn.setOnClickListener(v -> {
            switchFragment(3);
            outerBottomSheet.dismiss();
            openAddReviewDialog();
        });
    }

    private void openExistingCollBtms() {
        // Open inner bottom sheet
        BottomSheetDialog existCollBottomSheet = new BottomSheetDialog(RestaurantDetailActivity.this, R.style.BottomSheetTheme);
        View existCollSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_exist_coll, findViewById(R.id.bottom_sheet_exist_coll_container));
        existCollBottomSheet.setContentView(existCollSheetView);
        BottomSheetUtil.openBottomSheet(existCollBottomSheet);
        bottomSheets.add(existCollBottomSheet);

        Button addNewCollBtn = existCollSheetView.findViewById(R.id.btms_exist_btn_add_new);
        Button existDoneBtn = existCollSheetView.findViewById(R.id.btms_exist_btn_done);

        // New collection button in the second bottom sheet
        addNewCollBtn.setOnClickListener(v -> openNewCollBtms());

        // Recycler view of user collections and done button to add to collections
        showUserCollList(existCollSheetView);
        existDoneBtn.setOnClickListener(v -> {
            addToExistingResColl();
            Toast.makeText(RestaurantDetailActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();
            dismissAllBottomSheets();
        });
    }

    private void openNewCollBtms() {
        BottomSheetDialog newCollBottomSheet = new BottomSheetDialog(RestaurantDetailActivity.this, R.style.BottomSheetTheme);
        View newCollSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_new_coll, findViewById(R.id.bottom_sheet_new_coll_container));
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
                Toast.makeText(RestaurantDetailActivity.this, "Collection must have a name", Toast.LENGTH_SHORT).show();
            } else {
                addToNewResColl(collName);
                addToExistingResColl();
                dismissAllBottomSheets();
                Toast.makeText(RestaurantDetailActivity.this, "Added to collection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToNewResColl(String collName) {
        Map<String, Object> newColl = new HashMap<>();
        newColl.put("type", "restaurant");
        newColl.put("name", collName);
        ArrayList<String> resList = new ArrayList<>();
        resList.add(restaurantId);
        newColl.put("restaurants", resList);
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

    private void setDefaultNewCollName(EditTextUtil textField) {
        db.collection("users")
                .document(user.getUid())
                .collection("collections")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && textField != null) {
                        textField.setText(getString(R.string.my_collection, task.getResult().size() + 1));
                    } else {
                        Log.e(TAG, "Failed to fetch collections", task.getException());
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
        BottomSheetCollectionsRVAdapter adapter =
                new BottomSheetCollectionsRVAdapter(RestaurantDetailActivity.this, collList, restaurantId, "restaurant");
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
        EditText reviewTextField = reviewDialog.findViewById(R.id.dialog_text_add_review);
        RatingBar ratingBar = reviewDialog.findViewById(R.id.rating_bar_add_review);

        cancelBtn.setOnClickListener(v -> reviewDialog.dismiss());

        submitBtn.setOnClickListener(v -> {
            String reviewContent = reviewTextField.getText().toString();
            String ratings = String.valueOf(ratingBar.getRating());
            if (reviewContent.isEmpty()) {
                Toast.makeText(RestaurantDetailActivity.this, "Review cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            submitReview(reviewContent, ratings);
            reviewDialog.dismiss();
        });

        reviewDialog.show();
    }

    private void submitReview(String reviewContent, String ratings) {

        db.collection("users")
                .document(reviewerId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            String reviewerName = documentSnapshot.getString("username");
                            String reviewerAvaUrl = documentSnapshot.getString("avaUrl");

                            // Create a new review object
                            Map<String, Object> review = new HashMap<>();
                            review.put("timestamp", System.currentTimeMillis());
                            review.put("description", reviewContent);
                            review.put("ratings", ratings);
                            review.put("reviewerName", reviewerName);
                            review.put("reviewerAvaUrl", reviewerAvaUrl);
                            review.put("likedUserIds", new ArrayList<String>());
                            review.put("dislikedUserIds", new ArrayList<String>());
                            review.put("restaurantId", restaurantId);

                            // Add review to db
                            db.collection("restaurants")
                                    .document(restaurantId)
                                    .collection("reviews")
                                    .add(review)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {

                                            // Add to user's review list
                                            String reviewId = task1.getResult().getId();
                                            db.collection("users")
                                                    .document(reviewerId)
                                                    .collection("reviews")
                                                    .document(reviewId)
                                                    .set(review)
                                                    .addOnCompleteListener(task2 -> {
                                                        if (task2.isSuccessful()) {
                                                            Log.d(TAG, "Review reference added to user successfully");
                                                        } else {
                                                            Log.e(TAG, "Failed to add review reference to user", task2.getException());
                                                        }
                                                    });

                                            Toast.makeText(RestaurantDetailActivity.this, "Review added", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(RestaurantDetailActivity.this, "Failed to add review", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.e(TAG, "Error getting documents", task.getException());
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
                .addOnCompleteListener(task -> {
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
                });
    }
}