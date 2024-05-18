package com.example.gourmetcompass.views.personal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.MainActivity;
import com.example.gourmetcompass.R;
import com.example.gourmetcompass.adapters.MyReviewsRVAdapter;
import com.example.gourmetcompass.utils.FirestoreUtil;
import com.example.gourmetcompass.models.Review;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MyReviewsActivity extends AppCompatActivity {

    private static final String TAG = "MyReviewsActivity";
    ImageButton backBtn;
    Button addBtn;
    FirebaseFirestore db;
    FirebaseUser user;
    RecyclerView recyclerView;
    MyReviewsRVAdapter adapter;
    ProgressBar progressBar;
    ArrayList<Review> reviewList;
    LinearLayout myReviewsEmptyLayout;
    RelativeLayout myReviewsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reviews);

        // Init firebase services
        db = FirestoreUtil.getInstance().getFirestore();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Init views
        initViews();

        // Fetch review data
        fetchReviewData();

        backBtn.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.stay_still, R.anim.slide_out);
        });

        addBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MyReviewsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private void initViews() {
        backBtn = findViewById(R.id.btn_back_my_reviews);
        addBtn = findViewById(R.id.my_reviews_btn_add);
        recyclerView = findViewById(R.id.my_reviews_recyclerview);
        progressBar = findViewById(R.id.my_reviews_progress_bar);
        myReviewsEmptyLayout = findViewById(R.id.my_reviews_empty_layout);
        myReviewsLayout = findViewById(R.id.my_reviews_layout);
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
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error);
                        return;
                    }

                    reviewList.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            Review review = document.toObject(Review.class);
                            reviewList.add(review);
                        }
                    }
                    setLayout(reviewList.size());
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                });
    }

    public void setLayout(int size) {
        if (size == 0) {
            myReviewsLayout.setVisibility(View.GONE);
            myReviewsEmptyLayout.setVisibility(View.VISIBLE);
        } else {
            myReviewsLayout.setVisibility(View.VISIBLE);
            myReviewsEmptyLayout.setVisibility(View.GONE);
        }
    }
}
