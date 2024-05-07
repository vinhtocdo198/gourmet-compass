package com.example.gourmetcompass.ui_personal;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.firebase.FirestoreUtil;
import com.example.gourmetcompass.ui_restaurant_detail.RestaurantDetailActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;

public class PersonalInformationActivity extends AppCompatActivity {

    ImageButton backBtn;
    Button saveBtn;
    EditText usernameTextField, emailTextField, phoneTextField;
    ImageButton userAvatar;
    FirebaseFirestore db;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_basic_info);

        // Init firebase services
        db = FirestoreUtil.getInstance().getFirestore();

        // Get user id from arguments
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        // Init views
        initViews();

        // Get user information
        getUserInformation();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.stay_still, R.anim.slide_out);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewUserInfo();
                Toast.makeText(PersonalInformationActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.stay_still, R.anim.slide_out);
            }
        });

        userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheet();
            }
        });
    }

    private void saveNewUserInfo() {
        String newUsername = usernameTextField.getText().toString();
        String newEmail = emailTextField.getText().toString();
        String newPhone = phoneTextField.getText().toString();
        // TODO: save avatar

        db.collection("users").document(userId)
                .update("username", newUsername,
                        "email", newEmail,
                        "phone", newPhone);
    }

    private void openBottomSheet() {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(PersonalInformationActivity.this, R.style.BottomSheetTheme);
        View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_user_avatar, findViewById(R.id.btms_user_avatar_container));
        bottomSheet.setContentView(sheetView);
        bottomSheet.show();

        Button takePhotoBtn = sheetView.findViewById(R.id.btn_take_photo);
        Button choosePhotoBtn = sheetView.findViewById(R.id.btn_photo_lib);

        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        choosePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhoto();
            }
        });
    }

    private void choosePhoto() {
        // TODO: Choose a photo with image picker
    }

    private void takePhoto() {
        // TODO: Take a photo
    }

    private void initViews() {
        backBtn = findViewById(R.id.btn_back_basic_info);
        saveBtn = findViewById(R.id.btn_save_basic_info);
        usernameTextField = findViewById(R.id.username_basic_info);
        emailTextField = findViewById(R.id.email_basic_info);
        phoneTextField = findViewById(R.id.phone_basic_info);
        userAvatar = findViewById(R.id.avatar_basic_info);
    }

    private void getUserInformation() {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        usernameTextField.setText(documentSnapshot.getString("username"));
                        emailTextField.setText(documentSnapshot.getString("email"));
                        phoneTextField.setText(documentSnapshot.getString("phone")); // TODO
                    }
                });
    }
}
