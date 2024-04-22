package com.example.gourmetcompass;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.gourmetcompass.databinding.ActivityMainBinding;
import com.example.gourmetcompass.general_ui.AccountFragment;
import com.example.gourmetcompass.general_ui.BrowseFragment;
import com.example.gourmetcompass.general_ui.HomeFragment;
import com.example.gourmetcompass.general_ui.MapFragment;
import com.example.gourmetcompass.general_ui.NotificationFragment;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
//    Button detailBtn;
//    Button mapBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.browse:
                    replaceFragment(new BrowseFragment());
                    break;
                case R.id.map:
                    replaceFragment(new MapFragment());
                    break;
                case R.id.notification:
                    replaceFragment(new NotificationFragment());
                    break;
                case R.id.account:
                    replaceFragment(new AccountFragment());
                    break;
            }

            return true;
        });

        // Create a new Button

//        detailBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Start the AnotherActivity when the Button is clicked
//                Intent intent = new Intent(MainActivity.this, RestaurantDetailActivity.class);
//                startActivity(intent);
//            }
//        });

//        mapBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Start the AnotherActivity when the Button is clicked
//                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}