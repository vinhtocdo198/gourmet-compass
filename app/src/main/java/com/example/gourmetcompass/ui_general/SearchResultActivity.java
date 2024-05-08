package com.example.gourmetcompass.ui_general;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.gourmetcompass.R;

public class SearchResultActivity extends AppCompatActivity {

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

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.stay_still, R.anim.slide_out);
            }
        });

        // Set search text result and quantity
        searchTextResult.setText(searchQuery);
        resultQuantity.setText(String.format(getApplicationContext().getString(R.string.n_results), 300)); // TODO: change 300

//        setupFilterBtn(highestRatedBtn);

    }

    private void initViews() {
        searchTextResult = findViewById(R.id.search_text_result);
        resultQuantity = findViewById(R.id.result_quantity);
        highestRatedBtn = findViewById(R.id.highest_rated_filter);
        mostReviewedBtn = findViewById(R.id.most_reviewed_filter);
        nearestBtn = findViewById(R.id.nearest_filter);
        backBtn = findViewById(R.id.btn_back_res_search_list);
//        progressBar = findViewById(R.id.res_search_list_progress_bar);
    }

    private void setupFilterBtn(Button button) {
        // Initially, the button is not clicked
        button.setTag(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isClicked = (boolean) button.getTag();
                if (isClicked) {
                    // If the button was previously clicked, reset its background and update its state
                    button.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_star_filter, null));
                    button.setTag(false);
                } else {
                    // If the button was not previously clicked, change its background and update its state
                    button.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_star_filter_full, null));
                    button.setTag(true);
                }
            }
        });
    }

}