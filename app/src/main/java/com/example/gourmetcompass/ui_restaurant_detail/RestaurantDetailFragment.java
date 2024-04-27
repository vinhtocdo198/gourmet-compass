package com.example.gourmetcompass.ui_restaurant_detail;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.database.FirestoreUtil;
import com.example.gourmetcompass.models.Restaurant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

        return view;
    }

    private void setViews() {
        descContent.setText(restaurant.getDescription());
        addressContent.setText(restaurant.getAddress());
        phoneContent.setText(restaurant.getPhoneNo());
        openHrContent.setText(restaurant.getOpeningHours());
        ratingsTitle.setText(getString(R.string.ratings_title, restaurant.getRatings()));
        // TODO: Set the ratings count for each star
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
        db.collection("restaurants").document(restaurantId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                restaurant = documentSnapshot.toObject(Restaurant.class);
                                setViews();
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}