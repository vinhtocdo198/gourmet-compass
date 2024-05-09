package com.example.gourmetcompass;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.gourmetcompass.databinding.ActivityMainBinding;
import com.example.gourmetcompass.firebase.FirestoreUtil;
import com.example.gourmetcompass.firebase.StorageUtil;
import com.example.gourmetcompass.models.User;
import com.example.gourmetcompass.ui_general.BrowseFragment;
import com.example.gourmetcompass.ui_general.HomeFragment;
import com.example.gourmetcompass.ui_general.LogInFragment;
import com.example.gourmetcompass.ui_general.MapFragment;
import com.example.gourmetcompass.ui_general.NotificationFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
//    Button mapBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Init splash screen
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment;

            if (item.getItemId() == R.id.home_fragment) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.browse_fragment) {
                selectedFragment = new BrowseFragment();
            } else if (item.getItemId() == R.id.map_fragment) {
                selectedFragment = new MapFragment();
            } else if (item.getItemId() == R.id.noti_fragment) {
                selectedFragment = new NotificationFragment();
            } else if (item.getItemId() == R.id.account_fragment) {
                selectedFragment = new LogInFragment();
            } else {
                selectedFragment = new HomeFragment(); // Default fragment
            }

            replaceFragment(selectedFragment);
            return true;
        });

//        mapBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Start the AnotherActivity when the Button is clicked
//                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void selectBottomNavItem(@IdRes int menuItemId) {
        binding.bottomNavigationView.setSelectedItemId(menuItemId);
    }
}