package com.example.gourmetcompass.ui_restaurant_detail;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.models.Restaurant;
import com.example.gourmetcompass.models.Review;
import com.example.gourmetcompass.utils.FirestoreUtil;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class RestaurantDetailFragment extends Fragment {

    private static final String TAG = "RestaurantDetailFragment";
    FirebaseFirestore db;
    String restaurantId;
    Restaurant restaurant;
    TextView descContent, addressContent, phoneContent, openHrContent, ratingsTitle;
    TextView rate1, rate2, rate3, rate4, rate5;


    public static RestaurantDetailFragment newInstance(String restaurantId) {
        RestaurantDetailFragment fragment = new RestaurantDetailFragment();

        Bundle bundle = new Bundle();
        bundle.putString("restaurantId", restaurantId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant_detail, container, false);

        db = FirestoreUtil.getInstance().getFirestore();

        // Get the restaurant object from the arguments
        if (getArguments() != null) {
            restaurantId = getArguments().getString("restaurantId");
        }

        // Init views
        initViews(view);

        // Fetch restaurant details
        fetchRestaurantDetail();

        // Get total ratings
        getTotalRatings();

        return view;
    }

    private void setViews() {
        descContent.setText(restaurant.getDescription());
        addressContent.setText(restaurant.getAddress());
        phoneContent.setText(restaurant.getPhoneNo());
        openHrContent.setText(restaurant.getOpeningHours());
        ratingsTitle.setText(getString(R.string.ratings_title, restaurant.getRatings()));
    }

    private void initViews(View view) {
        descContent = view.findViewById(R.id.desc_content);
        addressContent = view.findViewById(R.id.address_content);
        phoneContent = view.findViewById(R.id.phone_content);
        openHrContent = view.findViewById(R.id.open_hr_content);
        ratingsTitle = view.findViewById(R.id.ratings_title);
        rate1 = view.findViewById(R.id.rate_count_1);
        rate2 = view.findViewById(R.id.rate_count_2);
        rate3 = view.findViewById(R.id.rate_count_3);
        rate4 = view.findViewById(R.id.rate_count_4);
        rate5 = view.findViewById(R.id.rate_count_5);
    }

    private void fetchRestaurantDetail() {
        db.collection("restaurants")
                .document(restaurantId)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (value != null && value.exists()) {
                        restaurant = value.toObject(Restaurant.class);
                        setViews();
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                });
    }

    private void getTotalRatings() {
        db.collection("restaurants")
                .document(restaurantId)
                .collection("reviews")
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (value != null) {
                        RestaurantDetailActivity activity = (RestaurantDetailActivity) getActivity();
                        int rate1Count = 0, rate2Count = 0, rate3Count = 0, rate4Count = 0, rate5Count = 0;
                        float averageRatings, totalRatings = 0;
                        int totalReviews = 0;

                        for (QueryDocumentSnapshot document : value) {
                            Review review = document.toObject(Review.class);
                            switch (review.getRatings()) {
                                case "1.0":
                                    rate1Count++;
                                    break;
                                case "2.0":
                                    rate2Count++;
                                    break;
                                case "3.0":
                                    rate3Count++;
                                    break;
                                case "4.0":
                                    rate4Count++;
                                    break;
                                case "5.0":
                                    rate5Count++;
                                    break;
                            }
                            totalRatings += Float.parseFloat(review.getRatings());
                            totalReviews++;
                        }

                        if (activity != null) {
                            if (totalReviews == 0) {
                                db.collection("restaurants")
                                        .document(restaurantId)
                                        .update("ratings", "N/A")
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Ratings successfully updated!");
                                            } else {
                                                Log.w(TAG, "Error updating document", task.getException());
                                            }
                                        });
                            } else {
                                averageRatings = totalRatings / totalReviews;
                                db.collection("restaurants")
                                        .document(restaurantId)
                                        .update("ratings", String.valueOf(averageRatings))
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Ratings successfully updated!");
                                            } else {
                                                Log.w(TAG, "Error updating document", task.getException());
                                            }
                                        });
                            }
                            rate1.setText(String.format(activity.getString(R.string.rating_count), rate1Count));
                            rate2.setText(String.format(activity.getString(R.string.rating_count), rate2Count));
                            rate3.setText(String.format(activity.getString(R.string.rating_count), rate3Count));
                            rate4.setText(String.format(activity.getString(R.string.rating_count), rate4Count));
                            rate5.setText(String.format(activity.getString(R.string.rating_count), rate5Count));
                        }

                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                });
    }
}