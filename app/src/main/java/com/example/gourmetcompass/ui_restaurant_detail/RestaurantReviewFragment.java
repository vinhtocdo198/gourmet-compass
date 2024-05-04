package com.example.gourmetcompass.ui_restaurant_detail;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.adapters.MenuRVAdapter;
import com.example.gourmetcompass.adapters.ReviewRVAdapter;
import com.example.gourmetcompass.firebase.FirestoreUtil;
import com.example.gourmetcompass.models.Dish;
import com.example.gourmetcompass.models.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RestaurantReviewFragment extends Fragment {

    private static final String TAG = "RestaurantReviewFragment";
    FirebaseFirestore db;
    String restaurantId;
    ArrayList<Review> reviews;
    RecyclerView recyclerView;
    ReviewRVAdapter adapter;
    ProgressBar progressBar;

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

        progressBar = view.findViewById(R.id.review_progress_bar);
        recyclerView = view.findViewById(R.id.review_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        // Fetch restaurant reviews
        fetchRestaurantReviews();

        return view;
    }

    private void fetchRestaurantReviews() {

        progressBar.setVisibility(View.VISIBLE);

        db.collection("restaurants").document(restaurantId)
                .collection("reviews")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (value != null) {
                            reviews = new ArrayList<>();
                            for (QueryDocumentSnapshot document : value) {
                                Review review = document.toObject(Review.class);
                                review.setId(document.getId());
                                reviews.add(review);
                            }
                            adapter = new ReviewRVAdapter(getContext(), reviews, restaurantId);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });
    }
}