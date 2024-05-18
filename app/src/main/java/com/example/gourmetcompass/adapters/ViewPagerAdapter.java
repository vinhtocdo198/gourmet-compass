package com.example.gourmetcompass.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.gourmetcompass.views.restaurant_detail.RestaurantDetailFragment;
import com.example.gourmetcompass.views.restaurant_detail.RestaurantGalleryFragment;
import com.example.gourmetcompass.views.restaurant_detail.RestaurantMenuFragment;
import com.example.gourmetcompass.views.restaurant_detail.RestaurantReviewFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final String[] titles = {"Detail", "Menu", "Gallery", "Review"};
    private final String restaurantId;
    private RestaurantMenuFragment restaurantMenuFragment;

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
                if (restaurantMenuFragment == null) {
                    restaurantMenuFragment = RestaurantMenuFragment.newInstance(restaurantId);
                }
                fragment = restaurantMenuFragment;
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

    public RestaurantMenuFragment getRestaurantMenuFragment() {
        if (restaurantMenuFragment == null) {
            restaurantMenuFragment = RestaurantMenuFragment.newInstance(restaurantId);
        }
        return restaurantMenuFragment;
    }
}