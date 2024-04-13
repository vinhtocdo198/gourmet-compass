package com.example.gourmetcompass.ui_restaurant_detail;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final String[] titles = {"Detail", "Menu", "Gallery", "Review"};

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new RestaurantDetailFragment();
            case 1:
                return new RestaurantMenuFragment();
            case 2:
                return new RestaurantGalleryFragment();
            case 3:
                return new RestaurantReviewFragment();
        }
        return new RestaurantDetailFragment();
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }
}
