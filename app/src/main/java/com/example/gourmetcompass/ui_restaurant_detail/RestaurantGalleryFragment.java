package com.example.gourmetcompass.ui_restaurant_detail;

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
import com.example.gourmetcompass.firebase.FirestoreUtil;
import com.example.gourmetcompass.firebase.StorageUtil;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class RestaurantGalleryFragment extends Fragment {

    private static final String TAG = "RestaurantGalleryFragment";
    FirebaseFirestore db;
    String restaurantId;
    RecyclerView recyclerView;
    GalleryRVAdapter adapter;
    ArrayList<String> imageUrls;
    FirebaseStorage storage;
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
        storage = StorageUtil.getInstance().getStorage();
        storageRef = storage.getReference();

        // Get the restaurant object from the arguments
        if (getArguments() != null) {
            restaurantId = getArguments().getString("restaurantId");
        }

        progressBar = view.findViewById(R.id.gallery_progress_bar);
        recyclerView = view.findViewById(R.id.gallery_layout);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setHasFixedSize(true);
        imageUrls = new ArrayList<>();

        // Fetch gallery images from Firebase Storage
        fetchGalleryImages();

        return view;
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