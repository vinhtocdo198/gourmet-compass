package com.example.gourmetcompass.ui_restaurant_detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.database.FirestoreUtil;
import com.example.gourmetcompass.models.Restaurant;
import com.google.firebase.firestore.FirebaseFirestore;

public class RestaurantReviewFragment extends Fragment {

    private static final String TAG = "RestaurantReviewFragment";
    FirebaseFirestore db;
    String restaurantId;
    // TODO: review model

    public static RestaurantReviewFragment newInstance(String restaurantId) {
        RestaurantReviewFragment fragment = new RestaurantReviewFragment();

        Bundle args = new Bundle();
        args.putSerializable("restaurantId", restaurantId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant_review, container, false);

        db = FirestoreUtil.getInstance().getFirestore();

        // Get the restaurant object from the arguments
        if (getArguments() != null) {
            restaurantId = getArguments().getString("restaurantId");
        }

        // Init views
//        initViews(view);

        // Use the restaurant object to set data in views
//        setViews();

        return view;
    }
}