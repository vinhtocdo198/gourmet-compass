package com.example.gourmetcompass.views.restaurant_detail;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.adapters.GalleryRVAdapter;
import com.example.gourmetcompass.utils.FirestoreUtil;
import com.example.gourmetcompass.utils.StorageUtil;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class RestaurantGalleryFragment extends Fragment {

    private static final String TAG = "RestaurantGalleryFragment";
    FirebaseFirestore db;
    String restaurantId;
    RecyclerView recyclerView;
    GalleryRVAdapter adapter;
    ArrayList<String> imageUrls;
    StorageReference storageRef;
    ProgressBar progressBar;

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

        db = FirestoreUtil.getInstance().getFirestore();
        storageRef = StorageUtil.getInstance().getStorage().getReference();

        // Get the restaurant object from the arguments
        if (getArguments() != null) {
            restaurantId = getArguments().getString("restaurantId");
        }

        // Init views
        initViews(view);

        // Fetch gallery images from Firebase Storage
        fetchGalleryImages();

        return view;
    }

    private void initViews(View view) {
        progressBar = view.findViewById(R.id.gallery_progress_bar);
        recyclerView = view.findViewById(R.id.gallery_layout);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setHasFixedSize(true);
        imageUrls = new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchGalleryImages() {

        progressBar.setVisibility(View.VISIBLE);

        // Navigate to gallery folder
        StorageReference restaurantFolderRef = storageRef.child("res_images").child(restaurantId).child("gallery");

        // List all the files in the folder
        restaurantFolderRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference fileRef : listResult.getItems()) {
                        // For each file, get the download URL
                        fileRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    // Add the URL to the list
                                    imageUrls.add(uri.toString());
                                    adapter = new GalleryRVAdapter(getContext(), imageUrls);
                                    recyclerView.setAdapter(adapter);

                                    // Update the RecyclerView
                                    adapter.notifyDataSetChanged();
                                });
                    }
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error listing files", e));
    }

}