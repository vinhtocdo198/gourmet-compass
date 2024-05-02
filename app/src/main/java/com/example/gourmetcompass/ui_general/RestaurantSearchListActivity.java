package com.example.gourmetcompass.ui_general;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gourmetcompass.R;

public class RestaurantSearchListActivity extends AppCompatActivity {

    String searchQuery;
    TextView searchTextResult, resultQuantity;
    Button highestRatedBtn, mostReviewedBtn, nearestBtn;
    ImageButton backBtn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_search_list);

        // Get search query from intent
        searchQuery = getIntent().getStringExtra("searchQuery");

        // Init views
        initViews();

        // Set search text result and quantity
        searchTextResult.setText(searchQuery);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.stay_still, R.anim.slide_out);
            }
        });
    }

    private void initViews() {
        searchTextResult = findViewById(R.id.search_text_result);
        resultQuantity = findViewById(R.id.result_quantity);
        highestRatedBtn = findViewById(R.id.highest_rated_filter);
        mostReviewedBtn = findViewById(R.id.most_reviewed_filter);
        nearestBtn = findViewById(R.id.nearest_filter);
        backBtn = findViewById(R.id.btn_back_res_search_list);
        progressBar = findViewById(R.id.res_search_list_progress_bar);
    }

}