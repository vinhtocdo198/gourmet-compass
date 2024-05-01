package com.example.gourmetcompass.ui_personal;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gourmetcompass.R;

public class MyCollectionsActivity extends AppCompatActivity {

    ImageButton backBtn, searchBtn, addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collections);

        backBtn = findViewById(R.id.btn_back_my_collections);
        searchBtn = findViewById(R.id.btn_search_my_collections);
        addBtn = findViewById(R.id.btn_add_my_collections);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Search
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open bottom sheet add collection
            }
        });

    }
}
