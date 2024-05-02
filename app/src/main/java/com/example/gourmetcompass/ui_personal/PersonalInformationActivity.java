package com.example.gourmetcompass.ui_personal;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gourmetcompass.R;

public class PersonalInformationActivity extends AppCompatActivity {

    ImageButton backBtn;
    Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_basic_info);

        backBtn = findViewById(R.id.btn_back_basic_info);
        saveBtn = findViewById(R.id.btn_save_basic_info);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.stay_still, R.anim.slide_out);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Save the changes

                Toast.makeText(PersonalInformationActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.stay_still, R.anim.slide_out);
            }
        });
    }
}
