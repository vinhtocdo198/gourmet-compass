package com.example.gourmetcompass.ui_general;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.firebase.FirestoreUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpFragment extends Fragment {

    public final String TAG = "SignUpFragment";
    EditText usernameTextField, emailTextField, passwordTextField, cfPasswordTextField;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Button loginBtn, signUpBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        // Init firebase services
        mAuth = FirebaseAuth.getInstance();
        db = FirestoreUtil.getInstance().getFirestore();

        // Init views
        initViews(view);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new LogInFragment());
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username, email, password, cfPassword;
                username = String.valueOf(SignUpFragment.this.usernameTextField.getText()).trim();
                email = String.valueOf(SignUpFragment.this.emailTextField.getText()).trim();
                password = String.valueOf(SignUpFragment.this.passwordTextField.getText()).trim();
                cfPassword = String.valueOf(cfPasswordTextField.getText()).trim();

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getContext(), "Please fill in your user name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getContext(), "Please fill in your email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getContext(), "Please fill in your password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(cfPassword)) {
                    Toast.makeText(getContext(), "Please fill in your confirm password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!cfPassword.equals(password)) {
                    Toast.makeText(getContext(), "Password and confirm password don't match! Please check again!", Toast.LENGTH_SHORT).show();
                    SignUpFragment.this.passwordTextField.setText("");
                    cfPasswordTextField.setText("");
                } else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) {
                                            // Create a new user with a phone number and username
                                            Map<String, Object> userMap = new HashMap<>();
//                                            userMap.put("phoneNumber", phoneNumber); // TODO: Add phone number?
                                            userMap.put("username", username);

                                            // Add a new document with a generated ID
                                            db.collection("users")
                                                    .document(user.getUid())
                                                    .set(userMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "DocumentSnapshot added with ID: " + user.getUid());
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error adding document", e);
                                                        }
                                                    });
                                        }

                                    } else {
                                        // If sign up fails, display a message to the user.
                                        Log.w(TAG, "Failed to create a new account", task.getException());
                                        Toast.makeText(getActivity(), "Failed to create a new account", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        return view;
    }

    private void initViews(View view) {
        usernameTextField = view.findViewById(R.id.username_sign_up);
        emailTextField = view.findViewById(R.id.email_sign_up);
        passwordTextField = view.findViewById(R.id.password_sign_up);
        cfPasswordTextField = view.findViewById(R.id.cf_password_sign_up);
        signUpBtn = view.findViewById(R.id.btn_sign_up_mid);
        loginBtn = view.findViewById(R.id.btn_log_in_bot);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, fragment);
        fragmentTransaction.commit();
    }
}