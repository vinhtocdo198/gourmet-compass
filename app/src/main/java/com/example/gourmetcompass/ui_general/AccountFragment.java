package com.example.gourmetcompass.ui_general;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.ui_personal.ChangePasswordActivity;
import com.example.gourmetcompass.ui_personal.MyCollectionsActivity;
import com.example.gourmetcompass.ui_personal.MyReviewsActivity;
import com.example.gourmetcompass.ui_personal.PersonalInformationActivity;
import com.example.gourmetcompass.ui_personal.RequestAddRestaurantActivity;
import com.google.firebase.auth.FirebaseAuth;

public class AccountFragment extends Fragment {

    Button personalInfoBtn, myCollectionsBtn, myReviewsBtn, changePasswordBtn, requestBtn, logoutBtn;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Init firebase auth
        mAuth = FirebaseAuth.getInstance();

        // Init views
        initViews(view);

        personalInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open personal information activity
                startActivitySlideIn(PersonalInformationActivity.class);
            }
        });

        myCollectionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open my collections activity
                startActivitySlideIn(MyCollectionsActivity.class);
            }
        });

        myReviewsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open my reviews activity
                startActivitySlideIn(MyReviewsActivity.class);
            }
        });

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open change password activity
                startActivitySlideIn(ChangePasswordActivity.class);
            }
        });

        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open request activity
                startActivitySlideIn(RequestAddRestaurantActivity.class);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log out of the account
                mAuth.signOut();
                replaceFragment(new LogInFragment());
            }
        });

        return view;
    }

    private void initViews(View view) {
        personalInfoBtn = view.findViewById(R.id.personal_info_btn);
        myCollectionsBtn = view.findViewById(R.id.my_collections_btn);
        myReviewsBtn = view.findViewById(R.id.my_reviews_btn);
        changePasswordBtn = view.findViewById(R.id.change_password_btn);
        requestBtn = view.findViewById(R.id.add_edit_res_btn);
        logoutBtn = view.findViewById(R.id.log_out_btn);
    }

    private void startActivitySlideIn(Class<?> activityClass) {
        Intent intent = new Intent(getActivity(), activityClass);
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
}