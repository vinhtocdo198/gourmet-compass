package com.example.gourmetcompass.ui_personal;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.gourmetcompass.R;
import com.example.gourmetcompass.firebase.FirestoreUtil;
import com.example.gourmetcompass.firebase.StorageUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

public class PersonalInformationActivity extends AppCompatActivity {

    private static final String TAG = "PersonalInformationActivity";
    ImageButton backBtn;
    Button saveBtn;
    EditText usernameTextField, emailTextField, phoneTextField;
    ImageView userAvatar;
    FirebaseFirestore db;
    StorageReference storageRef;
    String userId;
    Uri avatarUri;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_basic_info);

        // Init firebase services
        db = FirestoreUtil.getInstance().getFirestore();
        storageRef = StorageUtil.getInstance().getStorage().getReference();

        // Get user id from arguments
        userId = getIntent().getStringExtra("userId");

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

        userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheet();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewUserInfo();
                Toast.makeText(PersonalInformationActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveNewUserInfo() {
        String newUsername = usernameTextField.getText().toString();
        String newEmail = emailTextField.getText().toString();
        String newPhone = phoneTextField.getText().toString();

        // Save avatar
        if (avatarUri != null) {
            StorageReference avatarRef = storageRef.child("user_images/" + userId + "/avatar/");
            avatarRef.putFile(avatarUri)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "saveNewUserInfo: Avatar saved");
                        } else {
                            Log.d(TAG, "saveNewUserInfo: Failed to save avatar");
                        }
                    });
        }

        db.collection("users").document(userId)
                .update("username", newUsername,
                        "email", newEmail,
                        "phone", newPhone)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(PersonalInformationActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PersonalInformationActivity.this, "Failed to save changes", Toast.LENGTH_SHORT).show();
                    }
                });
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
                if (ContextCompat.checkSelfPermission(PersonalInformationActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    ActivityCompat.requestPermissions(PersonalInformationActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
                }
                bottomSheet.dismiss();
            }
        });

        choosePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetImage.launch("image/*");
                bottomSheet.dismiss();
            }
        });
    }

    // Set avatar image after choosing from gallery
    ActivityResultLauncher<String> mGetImage = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    userAvatar.setImageURI(result);
                    avatarUri = result;
                }
            });

    // Set avatar image after taking photo
    ActivityResultLauncher<Intent> mTakePicture = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    userAvatar.setImageURI(avatarUri);
                }
            }
    );

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "User avatar");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        avatarUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, avatarUri);
        mTakePicture.launch(cameraIntent);
    }

    private void initViews() {
        backBtn = findViewById(R.id.btn_back_basic_info);
        saveBtn = findViewById(R.id.btn_save_basic_info);
        usernameTextField = findViewById(R.id.username_basic_info);
        emailTextField = findViewById(R.id.email_basic_info);
        phoneTextField = findViewById(R.id.phone_basic_info);
        userAvatar = findViewById(R.id.avatar_basic_info);
        progressBar = findViewById(R.id.progress_bar_basic_info);
    }

    private void getUserInformation() {
        progressBar.setVisibility(View.VISIBLE);

        storageRef.child("user_images/" + userId + "/avatar/")
                .getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        avatarUri = task.getResult();
                        Log.d(TAG, "getUserInformation: " + avatarUri.toString());
                        Glide.with(this)
                                .load(avatarUri)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(userAvatar);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        Log.d(TAG, "getUserInformation: Failed to get avatar");
                    }
                });

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        usernameTextField.setText(documentSnapshot.getString("username"));
                        emailTextField.setText(documentSnapshot.getString("email"));
                        phoneTextField.setText(documentSnapshot.getString("phone"));
                    }
                });
    }
}
