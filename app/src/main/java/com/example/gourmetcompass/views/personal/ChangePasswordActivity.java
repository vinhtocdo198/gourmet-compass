package com.example.gourmetcompass.views.personal;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.utils.EditTextUtil;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    ImageButton backBtn;
    Button saveBtn;
    EditTextUtil currPassTextField, newPassTextField, cfPassTextField;
    LinearLayout changePassLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Init views
        initViews();
        changePassLayout.setOnClickListener(v -> clearAllFocus());

        backBtn.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.stay_still, R.anim.slide_out);
        });

        saveBtn.setOnClickListener(v -> {
            changePassword();
            clearAllFocus();
        });

    }

    private void changePassword() {
        String currPass = currPassTextField.getText();
        String newPass = newPassTextField.getText();
        String cfPass = cfPassTextField.getText();

        if (currPass.isEmpty() || newPass.isEmpty() || cfPass.isEmpty()) {
            Toast.makeText(ChangePasswordActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currPass.equals(newPass)) {
            Toast.makeText(ChangePasswordActivity.this, "New password must be different from current password", Toast.LENGTH_SHORT).show();
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
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // If the current password is correct, change the password
                                user.updatePassword(newPass)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                                                finish();
                                                overridePendingTransition(R.anim.stay_still, R.anim.slide_out);
                                            } else {
                                                Toast.makeText(ChangePasswordActivity.this, "Failed to change password", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(ChangePasswordActivity.this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    private void initViews() {
        changePassLayout = findViewById(R.id.change_password_layout);
        backBtn = findViewById(R.id.btn_back_change_password);
        saveBtn = findViewById(R.id.btn_save_change_password);

        currPassTextField = findViewById(R.id.curr_pass_change_pass);
        currPassTextField.setInputType("password");
        currPassTextField.setHint("Enter current password");

        newPassTextField = findViewById(R.id.new_pass_change_pass);
        newPassTextField.setInputType("password");
        newPassTextField.setHint("Enter new password");

        cfPassTextField = findViewById(R.id.cf_new_pass_change_pass);
        cfPassTextField.setInputType("password");
        cfPassTextField.setHint("Confirm new password");
    }

    private void clearAllFocus() {
        currPassTextField.clearFocus();
        newPassTextField.clearFocus();
        cfPassTextField.clearFocus();
    }
}
