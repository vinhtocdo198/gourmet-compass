package com.example.gourmetcompass.views.personal;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.gourmetcompass.models.User;
import com.example.gourmetcompass.utils.BottomSheetUtil;
import com.example.gourmetcompass.utils.EditTextUtil;
import com.example.gourmetcompass.utils.FirestoreUtil;
import com.example.gourmetcompass.utils.StorageUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

public class PersonalInformationActivity extends AppCompatActivity {

    private static final String TAG = "PersonalInformationActivity";
    ImageButton backBtn;
    Button saveBtn;
    EditTextUtil usernameTextField, emailTextField, phoneTextField;
    ImageView userAvatar;
    FirebaseFirestore db;
    StorageReference storageRef;
    String userId;
    Uri avatarUri;
    LinearLayout basicInfoContainer;

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
        basicInfoContainer.setOnClickListener(v -> clearAllFocus());

        // Get user information
        getUserInformation();

        backBtn.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.stay_still, R.anim.slide_out);
        });

        userAvatar.setOnClickListener(v -> openBottomSheet());

        saveBtn.setOnClickListener(v -> {
            checkNewUserInfo();
            clearAllFocus();
        });
    }

    private void checkNewUserInfo() {
        String newUsername = usernameTextField.getText();
        String newPhone = phoneTextField.getText();

        // Check user's information
        if (newUsername.length() < 6 || newUsername.length() > 36) {
            Toast.makeText(PersonalInformationActivity.this, "Username's length must be between 6 and 36 letters", Toast.LENGTH_SHORT).show();
            return;
        }
        if (newPhone.length() < 10 || newPhone.length() > 11) {
            Toast.makeText(PersonalInformationActivity.this, "Invalid phone number format", Toast.LENGTH_SHORT).show();
            return;
        }

        saveNewUserInfo(newUsername, newPhone);
    }

    private void saveNewUserInfo(String newUsername, String newPhone) {
        if (avatarUri != null) {
            StorageReference avatarRef = storageRef.child("user_images").child(userId).child("avatar");
            avatarRef.putFile(avatarUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    avatarRef.getDownloadUrl()
                            .addOnCompleteListener(uriTask -> {
                                if (uriTask.isSuccessful()) {
                                    String avaUri = uriTask.getResult().toString();
                                    saveNewAvatarAndInfo(newUsername, newPhone, avaUri);
                                } else {
                                    Log.d(TAG, "saveNewUserInfo: Failed to get avatar uri");
                                }
                            });
                } else {
                    Log.d(TAG, "saveNewUserInfo: Failed to save avatar");
                }
            });
        } else {
            saveOnlyNewInfo(newUsername, newPhone);
        }
    }

    private void saveOnlyNewInfo(String newUsername, String newPhone) {
        db.collection("users").document(userId)
                .update("username", newUsername,
                        "phone", newPhone)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(PersonalInformationActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PersonalInformationActivity.this, "Failed to save changes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveNewAvatarAndInfo(String newUsername, String newPhone, String avaUri) {
        db.collection("users").document(userId)
                .update("username", newUsername,
                        "phone", newPhone,
                        "avaUrl", avaUri)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(PersonalInformationActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PersonalInformationActivity.this, "Failed to save changes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearAllFocus() {
        usernameTextField.clearFocus();
        phoneTextField.clearFocus();
    }

    private void openBottomSheet() {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(PersonalInformationActivity.this, R.style.BottomSheetTheme);
        View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_user_avatar, findViewById(R.id.btms_user_avatar_container));
        bottomSheet.setContentView(sheetView);
        BottomSheetUtil.openBottomSheet(bottomSheet);

        Button takePhotoBtn = sheetView.findViewById(R.id.btn_take_photo);
        Button choosePhotoBtn = sheetView.findViewById(R.id.btn_photo_lib);

        takePhotoBtn.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(PersonalInformationActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                ActivityCompat.requestPermissions(PersonalInformationActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
            }
            bottomSheet.dismiss();
        });

        choosePhotoBtn.setOnClickListener(v -> {
            mGetImage.launch("image/*");
            bottomSheet.dismiss();
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
        basicInfoContainer = findViewById(R.id.basic_info_layout);
        backBtn = findViewById(R.id.btn_back_basic_info);
        saveBtn = findViewById(R.id.btn_save_basic_info);
        userAvatar = findViewById(R.id.avatar_basic_info);

        // Text fields
        emailTextField = findViewById(R.id.email_basic_info);
        emailTextField.setEnabled(false);
        usernameTextField = findViewById(R.id.username_basic_info);
        usernameTextField.setHint("Enter username");
        phoneTextField = findViewById(R.id.phone_basic_info);
        phoneTextField.setHint("Enter phone number");
        phoneTextField.setInputType("number");
    }

    private void getUserInformation() {

        storageRef.child("user_images/" + userId + "/avatar/")
                .getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!isFinishing()) {
                            Glide.with(PersonalInformationActivity.this)
                                    .load(task.getResult())
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(userAvatar);
                        }
                    } else {
                        Log.d(TAG, "getUserInformation: Failed to get avatar");
                    }
                });

        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        User user = task.getResult().toObject(User.class);
                        if (user != null) {
                            emailTextField.setText(user.getEmail());
                            usernameTextField.setText(user.getUsername());
                            phoneTextField.setText(user.getPhone());
                            Log.d(TAG, "getUserInformation: " + user.getAvaUrl());
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });
    }
}
