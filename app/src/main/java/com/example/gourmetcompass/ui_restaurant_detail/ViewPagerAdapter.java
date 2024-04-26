package com.example.gourmetcompass.ui_restaurant_detail;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.gourmetcompass.models.Restaurant;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final String[] titles = {"Detail", "Menu", "Gallery", "Review"};
    private final Restaurant restaurant;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, Restaurant restaurant) {
        super(fragmentActivity);
        this.restaurant = restaurant;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 1:
                fragment = RestaurantMenuFragment.newInstance(restaurant);
                break;
            case 2:
                fragment = RestaurantGalleryFragment.newInstance(restaurant);
                break;
            case 3:
                fragment = RestaurantReviewFragment.newInstance(restaurant);
                break;
            default:
                fragment = RestaurantDetailFragment.newInstance(restaurant);
                break;
        }

        return fragment;
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }
}
