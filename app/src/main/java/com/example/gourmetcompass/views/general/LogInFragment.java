package com.example.gourmetcompass.views.general;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.utils.EditTextUtil;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LogInFragment extends Fragment {
    private static final String GoogleTag = "GoogleActivity";
    public final String TAG = "LogInFragment";
    private static final int RC_SIGN_IN = 9001;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    GoogleSignInClient mGoogleSignInClient;
    SignInClient mSignInClient;
    EditTextUtil emailTextField, passwordTextField;
    Button logInBtn, signUpBtn;
    Button googleBtn;
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

        forgotPass.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ForgotPasswordActivity.class);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.stay_still);
            }
        });

        signUpBtn.setOnClickListener(v -> replaceFragment(new SignUpFragment(), null));

        // Google sign up
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        mGoogleSignInClient = GoogleSignIn.getClient(this.requireActivity(), gso);

        logInBtn.setOnClickListener(v -> logIn());

        return view;
    }

    private void logIn() {
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
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Pass user data to account fragment
                            Bundle bundle = new Bundle();
                            bundle.putString("userId", user.getUid());
                            replaceFragment(new AccountFragment(), bundle);
                        }
                        Toast.makeText(getActivity(), "Logged in successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(getActivity(), "Incorrect credentials. Please check again", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Pass user data to account fragment
            Bundle bundle = new Bundle();
            bundle.putString("userId", currentUser.getUid());
            replaceFragment(new AccountFragment(), bundle);
        }
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
        forgotPass = view.findViewById(R.id.forgot_pass);
        emailTextField = view.findViewById(R.id.email_log_in);
        emailTextField.setHint("Enter email");
        passwordTextField = view.findViewById(R.id.password_log_in);
        passwordTextField.setInputType("password");
        passwordTextField.setHint("Enter password");

        logInBtn = view.findViewById(R.id.btn_log_in_mid);
        signUpBtn = view.findViewById(R.id.btn_sign_up_bot);
        googleBtn = view.findViewById(R.id.google_btn);
    }

    private void replaceFragment(Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle Google Sign-In result
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign-In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account == null) {
                    Log.w(GoogleTag, "No account selected");
                    return;
                }
                Toast.makeText(getContext(), "Google sign in successful", Toast.LENGTH_SHORT).show();
                Log.d(GoogleTag, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign-In failed, update UI accordingly
                Toast.makeText(getContext(), "Google sign in failed", Toast.LENGTH_SHORT).show();
                Log.w(GoogleTag, "signInResult:failed code=" + e.getStatusCode());
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(GoogleTag, "signInWithCredential:success");
                            Toast.makeText(getActivity(), "Authentication success.", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(GoogleTag, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}