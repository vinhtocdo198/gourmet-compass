package com.example.gourmetcompass.ui_general;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.utils.EditTextUtil;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    ImageButton backBtn;
    Button sendBtn;
    EditTextUtil emailTextField;
    LinearLayout forgotPassContainer;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Init firebase auth
        mAuth = FirebaseAuth.getInstance();

        // Init views
        initViews();

        forgotPassContainer.setOnClickListener(v -> emailTextField.clearFocus());

        // Set on click listeners
        backBtn.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.stay_still, R.anim.slide_out);
        });
        sendBtn.setOnClickListener(v -> {
            resetPassword();
            finish();
        });

    }

    private void resetPassword() {
        String email = emailTextField.getText();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        emailTextField.clearFocus();
                        Toast.makeText(ForgotPasswordActivity.this, "Email sent. Please check your inbox!", Toast.LENGTH_SHORT).show();
                    } else {
                        emailTextField.clearFocus();
                        Toast.makeText(ForgotPasswordActivity.this, "Email not found!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initViews() {
        backBtn = findViewById(R.id.forgot_pass_btn_back);
        sendBtn = findViewById(R.id.forgot_pass_btn_send);
        emailTextField = findViewById(R.id.forgot_pass_email);
        emailTextField.setHint("Enter Email");
        forgotPassContainer = findViewById(R.id.forgot_pass_layout);
    }
}