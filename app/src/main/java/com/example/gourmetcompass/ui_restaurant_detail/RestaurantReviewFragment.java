package com.example.gourmetcompass.ui_restaurant_detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.models.Restaurant;

public class RestaurantReviewFragment extends Fragment {

    private static final String ARG_RESTAURANT = "restaurant";
    Restaurant restaurant;

    public static RestaurantReviewFragment newInstance(Restaurant restaurant) {
        RestaurantReviewFragment fragment = new RestaurantReviewFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_RESTAURANT, restaurant);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant_review, container, false);

        // Get the restaurant object from the arguments
        if (getArguments() != null) {
            restaurant = (Restaurant) getArguments().getSerializable(ARG_RESTAURANT);
        }

        // Init views
//        initViews(view);

        // Use the restaurant object to set data in views
//        setViews();

        return view;
    }
}