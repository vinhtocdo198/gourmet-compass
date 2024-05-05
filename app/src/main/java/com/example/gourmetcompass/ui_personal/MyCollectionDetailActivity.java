package com.example.gourmetcompass.ui_personal;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.R;

public class MyCollectionDetailActivity extends AppCompatActivity {

    private final String TAG = "MyCollectionDetailActivity";
    String collectionId;
    ImageButton backBtn, searchBtn;
    TextView collName;
    RecyclerView myCollListRecyclerView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection_detail);

        collectionId = getIntent().getStringExtra("collectionId");

        // Init views
        initViews();

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

            }
        });
    }

    private void initViews() {
        backBtn = findViewById(R.id.btn_back_res_my_colls);
        searchBtn = findViewById(R.id.btn_search_res_my_colls);
        collName = findViewById(R.id.res_my_colls_coll_title);
        myCollListRecyclerView = findViewById(R.id.res_my_colls_list_recyclerview);
        progressBar = findViewById(R.id.progress_bar_res_my_colls);
    }
}