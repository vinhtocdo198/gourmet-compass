package com.example.gourmetcompass.ui_general;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.ui_personal.ChangePasswordActivity;
import com.example.gourmetcompass.ui_personal.MyCollectionsActivity;
import com.example.gourmetcompass.ui_personal.MyReviewsActivity;
import com.example.gourmetcompass.ui_personal.PersonalInformationActivity;
import com.example.gourmetcompass.ui_personal.RequestAddRestaurantActivity;

public class AccountFragment extends Fragment {

    Button personalInfoBtn, myCollectionsBtn, myReviewsBtn, changePasswordBtn, requestBtn, logoutBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Init views
        initViews(view);

        personalInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open personal information activity
                Intent intent = new Intent(getActivity(), PersonalInformationActivity.class);
                startActivity(intent);
            }
        });

        myCollectionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open my collections activity
                Intent intent = new Intent(getActivity(), MyCollectionsActivity.class);
                startActivity(intent);
            }
        });

        myReviewsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open my reviews activity
                Intent intent = new Intent(getActivity(), MyReviewsActivity.class);
                startActivity(intent);
            }
        });

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open change password activity
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open request activity
                Intent intent = new Intent(getActivity(), RequestAddRestaurantActivity.class);
                startActivity(intent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open login activity
                // Intent intent = new Intent(getActivity(), LoginActivity.class);
                // startActivity(intent);
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
}