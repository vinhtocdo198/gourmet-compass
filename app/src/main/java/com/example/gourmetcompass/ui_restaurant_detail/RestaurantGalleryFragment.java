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


public class RestaurantGalleryFragment extends Fragment {

    private static final String TAG = "RestaurantGalleryFragment";
    FirebaseFirestore db;
    String restaurantId;
    // TODO: gallery model

    public static RestaurantGalleryFragment newInstance(String restaurantId) {
        RestaurantGalleryFragment fragment = new RestaurantGalleryFragment();

        Bundle bundle = new Bundle();
        bundle.putString("restaurantId", restaurantId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant_gallery, container, false);

        db = FirestoreUtil.getInstance();

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