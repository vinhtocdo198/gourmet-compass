package com.example.gourmetcompass.ui_restaurant_detail;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.adapters.ReviewRVAdapter;
import com.example.gourmetcompass.models.Review;
import com.example.gourmetcompass.utils.FirestoreUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    FirebaseUser user;
    String restaurantId;
    ArrayList<Review> reviews;
    RecyclerView recyclerView;
    ReviewRVAdapter adapter;
    ProgressBar progressBar;
    RelativeLayout reviewLayout;
    LinearLayout reviewEmptyLayout;

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

        // Init firebase services
        db = FirestoreUtil.getInstance().getFirestore();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Get the restaurant object from the arguments
        if (getArguments() != null) {
            restaurantId = getArguments().getString("restaurantId");
        }

        // Init views
        initViews(view);

        // Fetch restaurant reviews
        fetchRestaurantReviews();

        return view;
    }

    private void initViews(View view) {
        reviewLayout = view.findViewById(R.id.review_fragment_layout);
        reviewEmptyLayout = view.findViewById(R.id.review_empty_layout);
        progressBar = view.findViewById(R.id.review_progress_bar);
        recyclerView = view.findViewById(R.id.review_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
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
                            RestaurantDetailActivity activity = (RestaurantDetailActivity) getActivity();
                            reviews = new ArrayList<>();
                            for (QueryDocumentSnapshot document : value) {
                                Review review = document.toObject(Review.class);
                                review.setId(document.getId());
                                reviews.add(review);
                            }

                            // Sort the reviews so that current user's reviews will be on top
                            reviews.sort((review1, review2) -> {
                                if (review1.getReviewerId().equals(user.getUid()) && !review2.getReviewerId().equals(user.getUid())) {
                                    return -1;
                                } else if (!review1.getReviewerId().equals(user.getUid()) && review2.getReviewerId().equals(user.getUid())) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            });

                            if (activity != null) {
                                if (adapter == null) {
                                    adapter = new ReviewRVAdapter(activity, reviews, restaurantId);
                                    recyclerView.setAdapter(adapter);
                                } else {
                                    adapter.updateData(reviews);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            setLayout(reviews.size());
                            progressBar.setVisibility(View.GONE);
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });
    }

    private void setLayout(int size) {
        if (size == 0) {
            reviewLayout.setVisibility(View.GONE);
            reviewEmptyLayout.setVisibility(View.VISIBLE);
        } else {
            reviewLayout.setVisibility(View.VISIBLE);
            reviewEmptyLayout.setVisibility(View.GONE);
        }
    }
}