package com.example.gourmetcompass;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Sign_Up extends AppCompatActivity {

    EditText userName, email, password, confirmPassword;
    FirebaseAuth mAuth;

    Button login_button;
    Button signUpButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        login_button = findViewById(R.id.log_in_button);
        signUpButton = findViewById(R.id.sign_up_button);
        userName = findViewById(R.id.user_name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.user_password);
        confirmPassword = findViewById(R.id.confirm_password);


        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Sign_Up.this, Sign_In.class);
                startActivity(intent);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String un, e, p, cp;// read user name, email input, password input and confirm password input.
                un = String.valueOf(userName.getText()).trim();
                e = String.valueOf(email.getText()).trim();
                p = String.valueOf(password.getText()).trim();
                cp = String.valueOf(confirmPassword.getText()).trim();

                if (TextUtils.isEmpty(un)){
                    Toast.makeText(Sign_Up.this, "Please fill in your user name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(e)){
                    Toast.makeText(Sign_Up.this, "Please fill in your email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(p)){
                    Toast.makeText(Sign_Up.this, "Please fill in your password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(cp)){
                    Toast.makeText(Sign_Up.this, "Please fill in your confirm password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!cp.equals(p)){
                    Toast.makeText(Sign_Up.this, "The password and confirm password don't match! Please check again!", Toast.LENGTH_SHORT).show();
                    password.setText("");
                    confirmPassword.setText("");
                    return;
                }else {
                    mAuth.createUserWithEmailAndPassword(un, p)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Sign_Up.this, "Account created.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(Sign_Up.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}