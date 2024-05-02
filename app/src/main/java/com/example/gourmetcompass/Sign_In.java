package com.example.gourmetcompass;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gourmetcompass.ui_general.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Sign_In  extends AppCompatActivity {
    EditText userName, password;
    Button signInButton, regiterButton;
    FirebaseAuth mAuth;
    TextView forgotPass;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), HomeFragment.class);
            startActivity(intent);
            finish();
        }
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();
        signInButton = findViewById(R.id.sign_in_button);
        regiterButton = findViewById(R.id.register_button);
        userName = findViewById(R.id.user_name);
        password = findViewById(R.id.user_password);
        regiterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Sign-In", "clicked");
                Intent intent = new Intent(Sign_In.this, Sign_Up.class);
                startActivity(intent);
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String un, pass;// read user name, email input, password input and confirm password input.
                un = String.valueOf(userName.getText()).trim();
                pass = String.valueOf(password.getText()).trim();

                if (TextUtils.isEmpty(un)){
                    Toast.makeText(Sign_In.this, "Please fill in your user name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(pass)){
                    Toast.makeText(Sign_In.this, "Please fill in your password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(un, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), HomeFragment.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Sign_In.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                }
        });
    }



}
