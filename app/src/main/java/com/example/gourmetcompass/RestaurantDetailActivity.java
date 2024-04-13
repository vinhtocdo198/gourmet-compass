package com.example.gourmetcompass;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RestaurantDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        // TextViews
        TextView addressContent = findViewById(R.id.addressContent);

        // Buttons
        Button detailBtn = findViewById(R.id.detailBtn);
        Button menuBtn = findViewById(R.id.menuBtn);
        Button galleryBtn = findViewById(R.id.galleryBtn);
        Button reviewBtn = findViewById(R.id.reviewBtn);

        // Underline address
        addressContent.setPaintFlags(addressContent.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Set the button as selected
        detailBtn.setSelected(true);

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuBtn.setSelected(true);
                detailBtn.setSelected(false);
                galleryBtn.setSelected(false);
                reviewBtn.setSelected(false);
                // Handle click action for menuBtn
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuBtn.setSelected(false);
                detailBtn.setSelected(false);
                galleryBtn.setSelected(true);
                reviewBtn.setSelected(false);
                // Handle click action for galleryBtn
            }
        });

        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuBtn.setSelected(false);
                detailBtn.setSelected(false);
                galleryBtn.setSelected(false);
                reviewBtn.setSelected(true);
                // Handle click action for reviewBtn
            }
        });
    }

}