package com.example.gourmetcompass.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.gourmetcompass.ui_restaurant_detail.RestaurantDetailFragment;
import com.example.gourmetcompass.ui_restaurant_detail.RestaurantGalleryFragment;
import com.example.gourmetcompass.ui_restaurant_detail.RestaurantMenuFragment;
import com.example.gourmetcompass.ui_restaurant_detail.RestaurantReviewFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final String[] titles = {"Detail", "Menu", "Gallery", "Review"};
    private final String restaurantId;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, String restaurantId) {
        super(fragmentActivity);
        this.restaurantId = restaurantId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 1:
                fragment = RestaurantMenuFragment.newInstance(restaurantId);
                break;
            case 2:
                fragment = RestaurantGalleryFragment.newInstance(restaurantId);
                break;
            case 3:
                fragment = RestaurantReviewFragment.newInstance(restaurantId);
                break;
            default:
                fragment = RestaurantDetailFragment.newInstance(restaurantId);
                break;
        }

        return fragment;
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }
}