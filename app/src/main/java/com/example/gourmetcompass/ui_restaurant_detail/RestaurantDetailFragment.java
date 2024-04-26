package com.example.gourmetcompass.ui_restaurant_detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.models.Restaurant;

public class RestaurantDetailFragment extends Fragment {

    private static final String ARG_RESTAURANT = "restaurant";
    Restaurant restaurant;
    TextView descContent, addressContent, phoneContent, openHrContent, ratingsTitle;
    TextView rate1, rate2, rate3, rate4, rate5;

    public static RestaurantDetailFragment newInstance(Restaurant restaurant) {
        RestaurantDetailFragment fragment = new RestaurantDetailFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_RESTAURANT, restaurant);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant_detail, container, false);

        // Get the restaurant object from the arguments
        if (getArguments() != null) {
            restaurant = (Restaurant) getArguments().getSerializable(ARG_RESTAURANT);
        }

        // Init views
        initViews(view);

        // Use the restaurant object to set data in views
        setViews();

        return view;
    }

    private void setViews() {
        if (restaurant != null) {
            descContent.setText(restaurant.getDescription());
            addressContent.setText(restaurant.getAddress());
            phoneContent.setText(restaurant.getPhoneNo());
            openHrContent.setText(restaurant.getOpeningHours());
            ratingsTitle.setText(getString(R.string.ratings_title, restaurant.getRatings()));
            // TODO: Set the ratings count for each star
        }
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
}