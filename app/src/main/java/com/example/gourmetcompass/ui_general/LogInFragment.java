package com.example.gourmetcompass.ui_general;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gourmetcompass.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInFragment extends Fragment {
    public final String TAG = "LogInFragment";
    EditText emailTextField, passwordTextField;
    Button logInBtn, signUpBtn;
    FirebaseAuth mAuth;
    TextView forgotPass;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log_in, container, false);

        // Init firebase auth
        mAuth = FirebaseAuth.getInstance();

        // Init views
        initViews(view);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new SignUpFragment(), null);
            }
        });

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password;
                email = String.valueOf(LogInFragment.this.emailTextField.getText()).trim();
                password = String.valueOf(LogInFragment.this.passwordTextField.getText()).trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getActivity(), "Please fill in your email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getActivity(), "Please fill in your password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        // Pass user data to account fragment
                                        Bundle bundle = new Bundle();
                                        bundle.putString("userId", user.getUid());
                                        replaceFragment(new AccountFragment(), bundle);
                                    }
                                    Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_SHORT).show();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(getActivity(), "Email or password incorrect. Please check again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // If user is already logged in, show account fragment
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Pass user data to account fragment
            Bundle bundle = new Bundle();
            bundle.putString("userId", currentUser.getUid());
            replaceFragment(new AccountFragment(), bundle);
        }
    }

    private void initViews(View view) {
        emailTextField = view.findViewById(R.id.email_log_in);
        passwordTextField = view.findViewById(R.id.password_log_in);
        logInBtn = view.findViewById(R.id.btn_log_in_mid);
        signUpBtn = view.findViewById(R.id.btn_sign_up_bot);
    }

    private void replaceFragment(Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, fragment);
        fragmentTransaction.commit();
    }
}