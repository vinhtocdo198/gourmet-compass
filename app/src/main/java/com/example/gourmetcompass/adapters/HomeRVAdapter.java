package com.example.gourmetcompass.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.gourmetcompass.MainActivity;
import com.example.gourmetcompass.R;
import com.example.gourmetcompass.models.Restaurant;
import com.example.gourmetcompass.views.restaurant_detail.RestaurantDetailActivity;
import com.example.gourmetcompass.utils.StorageUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class HomeRVAdapter extends RecyclerView.Adapter<HomeRVAdapter.MyViewHolder> {

    private static final String TAG = "HomeRVAdapter";
    Context context;
    ArrayList<Restaurant> restaurantList;
    FirebaseUser user;
    StorageReference storageRef;

    public HomeRVAdapter(Context context, ArrayList<Restaurant> restaurantList) {
        this.context = context;
        this.restaurantList = restaurantList;
    }

    @NonNull
    @Override
    public HomeRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_res_home, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeRVAdapter.MyViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);

        // Init firebase services
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = StorageUtil.getInstance().getStorage().getReference();

        holder.resName.setText(restaurant.getName());
        getResThumbnailImg(holder, restaurant);
        holder.resImgThumbnail.setOnClickListener(v -> {
            // Navigate to RestaurantDetailActivity
            if (user != null) {
                Intent intent = new Intent(context, RestaurantDetailActivity.class);
                intent.putExtra("restaurantId", restaurant.getId());
                context.startActivity(intent);
                if (context instanceof MainActivity) {
                    ((MainActivity) context).overridePendingTransition(R.anim.slide_in, R.anim.stay_still);
                }
            } else {
                // Navigate to LogInFragment if user is not logged in
                if (context instanceof MainActivity) {
                    ((MainActivity) context).selectBottomNavItem(R.id.account_fragment);
                }
                Toast.makeText(context, "Log in to see our restaurants", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getResThumbnailImg(@NonNull MyViewHolder holder, Restaurant restaurant) {
        storageRef.child("res_images/" + restaurant.getId() + "/app_bar/app_bar.jpg")
                .getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri resBgUrl = task.getResult();
                        Glide.with(context)
                                .load(resBgUrl)
                                .placeholder(R.drawable.bg_shimmer)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .centerCrop()
                                .into(holder.resImgThumbnail);
                    } else {
                        Log.e(TAG, "Failed to fetch restaurant background image", task.getException());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView resName;
        ImageView resImgThumbnail;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            resName = itemView.findViewById(R.id.res_name_home);
            resImgThumbnail = itemView.findViewById(R.id.res_img_home);
        }
    }
}
