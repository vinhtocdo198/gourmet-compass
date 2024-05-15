package com.example.gourmetcompass.ui_general;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.gourmetcompass.R;
import com.example.gourmetcompass.utils.FirestoreUtil;
import com.example.gourmetcompass.utils.StorageUtil;
import com.example.gourmetcompass.models.User;
import com.example.gourmetcompass.ui_personal.ChangePasswordActivity;
import com.example.gourmetcompass.ui_personal.MyCollectionsActivity;
import com.example.gourmetcompass.ui_personal.MyReviewsActivity;
import com.example.gourmetcompass.ui_personal.PersonalInformationActivity;
import com.example.gourmetcompass.ui_personal.RequestAddRestaurantActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

public class AccountFragment extends Fragment {

    private static final String TAG = "AccountFragment";
    Button personalInfoBtn, myCollectionsBtn, myReviewsBtn, changePasswordBtn, requestBtn, logoutBtn;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    StorageReference storageRef;
    String userId, avatarUrl;
    User user;
    TextView usernameAppBar;
    ImageView userAvatarAppBar;
    Uri avatarUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Init firebase services
        mAuth = FirebaseAuth.getInstance();
        db = FirestoreUtil.getInstance().getFirestore();
        storageRef = StorageUtil.getInstance().getStorage().getReference();

        // Get user id from arguments
        Bundle bundle = getArguments();
        if (bundle != null) {
            userId = bundle.getString("userId");
        }

        // Init views
        initViews(view);

        // Get user data
        getUserData();

        // Navigation to personal screens
        personalInfoBtn.setOnClickListener(v -> {
            // Open personal information activity
            startActivitySlideIn(PersonalInformationActivity.class);
        });

        myCollectionsBtn.setOnClickListener(v -> {
            // Open my collections activity
            startActivitySlideIn(MyCollectionsActivity.class);
        });

        myReviewsBtn.setOnClickListener(v -> {
            // Open my reviews activity
            startActivitySlideIn(MyReviewsActivity.class);
        });

        changePasswordBtn.setOnClickListener(v -> {
            // Open change password activity
            startActivitySlideIn(ChangePasswordActivity.class);
        });

        requestBtn.setOnClickListener(v -> {
            // Open request activity
            startActivitySlideIn(RequestAddRestaurantActivity.class);
        });

        logoutBtn.setOnClickListener(v -> {
            // Log out of the account
            Toast.makeText(getActivity(), "Logged out", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            replaceFragment(new LogInFragment());
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserData(); // Update user data after saving changes
    }

    private void initViews(View view) {
        usernameAppBar = view.findViewById(R.id.username_account);
        userAvatarAppBar = view.findViewById(R.id.user_avatar);
        personalInfoBtn = view.findViewById(R.id.personal_info_btn);
        myCollectionsBtn = view.findViewById(R.id.my_collections_btn);
        myReviewsBtn = view.findViewById(R.id.my_reviews_btn);
        changePasswordBtn = view.findViewById(R.id.change_password_btn);
        requestBtn = view.findViewById(R.id.add_edit_res_btn);
        requestBtn.setVisibility(View.GONE);
        logoutBtn = view.findViewById(R.id.log_out_btn);
    }

    private void startActivitySlideIn(Class<?> activityClass) {
        Intent intent = new Intent(getActivity(), activityClass);
        intent.putExtra("userId", userId);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().overridePendingTransition(R.anim.slide_in, R.anim.stay_still);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void getUserData() {

        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        User user = task.getResult().toObject(User.class);
                        if (user != null && getActivity() != null) {
                            usernameAppBar.setText(user.getUsername());
                            Glide.with(getActivity())
                                    .load(user.getAvaUrl())
                                    .placeholder(R.drawable.ic_default_avatar)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(userAvatarAppBar);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });

    }

}