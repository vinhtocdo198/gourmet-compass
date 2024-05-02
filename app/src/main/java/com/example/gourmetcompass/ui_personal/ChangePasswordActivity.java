package com.example.gourmetcompass.ui_personal;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gourmetcompass.R;

public class ChangePasswordActivity extends AppCompatActivity {

    ImageButton backBtn;
    Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        backBtn = findViewById(R.id.btn_back_change_password);
        saveBtn = findViewById(R.id.btn_save_change_password);

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
                // TODO: Save new password

                Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.stay_still, R.anim.slide_out);
            }
        });

    }
}
