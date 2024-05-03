package com.example.gourmetcompass.ui_personal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gourmetcompass.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class MyCollectionsActivity extends AppCompatActivity {

    ImageButton backBtn, searchBtn, addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collections);

        // Init views
        backBtn = findViewById(R.id.btn_back_my_collections);
        searchBtn = findViewById(R.id.btn_search_my_collections);
        addBtn = findViewById(R.id.btn_add_my_collections);

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

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open bottom sheet add collection
                BottomSheetDialog bottomSheet = new BottomSheetDialog(MyCollectionsActivity.this, R.style.BottomSheetTheme);
                View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_my_colls, findViewById(R.id.btms_res_container));
                bottomSheet.setContentView(sheetView);
                bottomSheet.show();

                // Bottom sheet button
                Button doneBtn = bottomSheet.findViewById(R.id.btn_done_my_colls);
                if (doneBtn != null) {
                    doneBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO: add to collection

                            bottomSheet.dismiss();
                            Toast.makeText(MyCollectionsActivity.this, "Added to collection", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }
}
