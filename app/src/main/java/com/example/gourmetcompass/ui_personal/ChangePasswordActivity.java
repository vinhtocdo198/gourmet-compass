package com.example.gourmetcompass.ui_personal;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gourmetcompass.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    ImageButton backBtn;
    Button saveBtn;
    EditText currPassTextField, newPassTextField, cfPassTextField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Init views
        initViews();

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
                changePassword();
            }
        });

    }

    private void changePassword() {
        String currPass = currPassTextField.getText().toString();
        String newPass = newPassTextField.getText().toString();
        String cfPass = cfPassTextField.getText().toString();

        if (currPass.isEmpty() || newPass.isEmpty() || cfPass.isEmpty()) {
            Toast.makeText(ChangePasswordActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(cfPass)) {
            Toast.makeText(ChangePasswordActivity.this, "New password and confirmed password do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPass.length() < 6) {
            Toast.makeText(ChangePasswordActivity.this, "New password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            if (email != null) {

                // Check if the current password is correct
                AuthCredential credential = EmailAuthProvider.getCredential(email, currPass);
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // If the current password is correct, change the password
                                    user.updatePassword(newPass)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                        overridePendingTransition(R.anim.stay_still, R.anim.slide_out);
                                                    } else {
                                                        Toast.makeText(ChangePasswordActivity.this, "Failed to change password", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(ChangePasswordActivity.this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }

    private void initViews() {
        backBtn = findViewById(R.id.btn_back_change_password);
        saveBtn = findViewById(R.id.btn_save_change_password);
        currPassTextField = findViewById(R.id.curr_pass_change_pass);
        newPassTextField = findViewById(R.id.new_pass_change_pass);
        cfPassTextField = findViewById(R.id.cf_new_pass_change_pass);
    }
}
