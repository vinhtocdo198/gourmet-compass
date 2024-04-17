package com.example.gourmetcompass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gourmetcompass.ui_restaurant_detail.RestaurantDetailActivity;

public class MainActivity extends AppCompatActivity {

    Button detailBtn;
    Button mapBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a new Button
        detailBtn = findViewById(R.id.detail_btn);
        mapBtn = findViewById(R.id.map_btn);

        detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the AnotherActivity when the Button is clicked
                Intent intent = new Intent(MainActivity.this, RestaurantDetailActivity.class);
                startActivity(intent);
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the AnotherActivity when the Button is clicked
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }

}