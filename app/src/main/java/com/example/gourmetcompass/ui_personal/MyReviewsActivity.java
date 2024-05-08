package com.example.gourmetcompass.ui_personal;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.adapters.MyReviewsRVAdapter;
import com.example.gourmetcompass.firebase.FirestoreUtil;
import com.example.gourmetcompass.models.Review;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MyReviewsActivity extends AppCompatActivity {

    private static final String TAG = "MyReviewsActivity";
    ImageButton backBtn, searchBtn;
    FirebaseFirestore db;
    FirebaseUser user;
    RecyclerView recyclerView;
    MyReviewsRVAdapter adapter;
    ProgressBar progressBar;
    ArrayList<Review> reviewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reviews);

        // Init firebase services
        db = FirestoreUtil.getInstance().getFirestore();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Init views
        initViews();

        fetchReviewData();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.stay_still, R.anim.slide_out);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Search
            }
        });

    }

    private void initViews() {
        backBtn = findViewById(R.id.btn_back_my_reviews);
        searchBtn = findViewById(R.id.btn_search_my_reviews);
        recyclerView = findViewById(R.id.my_reviews_recyclerview);
        progressBar = findViewById(R.id.my_reviews_progress_bar);
        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyReviewsActivity.this));
        reviewList = new ArrayList<>();
        adapter = new MyReviewsRVAdapter(MyReviewsActivity.this, reviewList);
        recyclerView.setAdapter(adapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchReviewData() {
        db.collection("users").document(user.getUid())
                .collection("reviews")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Review review = document.toObject(Review.class);
                            reviewList.add(review);
                        }
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }
}
