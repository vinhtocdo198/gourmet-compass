package com.example.gourmetcompass.views.general;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.utils.EditTextUtil;
import com.example.gourmetcompass.utils.FirestoreUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpFragment extends Fragment {

    public final String TAG = "SignUpFragment";
    EditTextUtil usernameTextField, emailTextField, passwordTextField, cfPasswordTextField;
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

        loginBtn.setOnClickListener(view1 -> replaceFragment(new LogInFragment(), null));

        signUpBtn.setOnClickListener(view12 -> signUp());

        return view;
    }

    private void signUp() {
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
            Toast.makeText(getContext(), "Password and confirm password do not match! Please check again!", Toast.LENGTH_SHORT).show();
            SignUpFragment.this.passwordTextField.setText("");
            cfPasswordTextField.setText("");
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Create a new user with a phone number and username
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("username", username);
                                userMap.put("email", email);

                                // Add a new document with a generated ID
                                db.collection("users")
                                        .document(user.getUid())
                                        .set(userMap)
                                        .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot added with ID: " + user.getUid()))
                                        .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));

                                // Pass user data to account fragment
                                Bundle bundle = new Bundle();
                                bundle.putString("userId", user.getUid());
                                replaceFragment(new AccountFragment(), bundle);
                            }
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getActivity(), "This email is already in use. Please use a different email", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.w(TAG, "Failed to create a new account", task.getException());
                            }
                        }
                    });
        }
    }

    private void initViews(View view) {
        usernameTextField = view.findViewById(R.id.username_sign_up);
        usernameTextField.setHint("Enter username");

        emailTextField = view.findViewById(R.id.email_sign_up);
        emailTextField.setHint("Enter email");

        passwordTextField = view.findViewById(R.id.password_sign_up);
        passwordTextField.setInputType("password");
        passwordTextField.setHint("Enter password");

        cfPasswordTextField = view.findViewById(R.id.cf_password_sign_up);
        cfPasswordTextField.setInputType("password");
        cfPasswordTextField.setHint("Confirm password");

        signUpBtn = view.findViewById(R.id.btn_sign_up_mid);
        loginBtn = view.findViewById(R.id.btn_log_in_bot);
    }

    private void replaceFragment(Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, fragment);
        fragmentTransaction.commit();
    }
}