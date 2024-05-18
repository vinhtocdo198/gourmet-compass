package com.example.gourmetcompass;

import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gourmetcompass.databinding.ActivityMainBinding;
import com.example.gourmetcompass.views.general.BrowseFragment;
import com.example.gourmetcompass.views.general.HomeFragment;
import com.example.gourmetcompass.views.general.LogInFragment;
import com.example.gourmetcompass.views.general.NotificationFragment;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

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