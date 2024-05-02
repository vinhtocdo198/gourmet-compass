package com.example.gourmetcompass.ui_personal;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gourmetcompass.R;

public class MyReviewsActivity extends AppCompatActivity {

    ImageButton backBtn, searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reviews);

        backBtn = findViewById(R.id.btn_back_my_reviews);
        searchBtn = findViewById(R.id.btn_search_my_reviews);

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
}
