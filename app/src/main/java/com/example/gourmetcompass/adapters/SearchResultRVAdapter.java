package com.example.gourmetcompass.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.gourmetcompass.R;
import com.example.gourmetcompass.models.Restaurant;
import com.example.gourmetcompass.utils.StorageUtil;
import com.example.gourmetcompass.views.restaurant_detail.RestaurantDetailActivity;
import com.example.gourmetcompass.utils.FirestoreUtil;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SearchResultRVAdapter extends RecyclerView.Adapter<SearchResultRVAdapter.MyViewHolder> {

    private static final String TAG = "SearchResultRVAdapter";
    Context context;
    ArrayList<Restaurant> restaurantList;
    FirebaseFirestore db;
    StorageReference storageRef;

    public SearchResultRVAdapter(Context context, ArrayList<Restaurant> restaurantList) {
        this.context = context;
        this.restaurantList = restaurantList;
    }

    @NonNull
    @Override
    public SearchResultRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_coll_detail, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultRVAdapter.MyViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);

        // Init firebase services
        db = FirestoreUtil.getInstance().getFirestore();
        storageRef = StorageUtil.getInstance().getStorage().getReference();

        // Init views
        holder.itemName.setText(restaurant.getName());
        holder.itemDesc.setText(restaurant.getDescription());
        holder.btnMore.setVisibility(View.GONE);
        getResThumbnailImg(holder, restaurant);
        setResRatings(holder, restaurant);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), RestaurantDetailActivity.class);
            intent.putExtra("restaurantId", restaurant.getId());
            holder.itemView.getContext().startActivity(intent);
            if (holder.itemView.getContext() instanceof Activity) {
                ((Activity) holder.itemView.getContext()).overridePendingTransition(R.anim.slide_in, R.anim.stay_still);
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
                                .into(holder.itemImg);
                    } else {
                        Log.e(TAG, "Failed to fetch restaurant background image", task.getException());
                    }
                });
    }

    private void setResRatings(@NonNull MyViewHolder holder, Restaurant restaurant) {
        holder.itemRatingCount.setText(String.format(context.getString(R.string.rating_count), restaurant.getRatingCount()));
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.itemRatingCount.getLayoutParams();
        params.setMarginEnd(20);
        holder.itemRatingCount.setLayoutParams(params);
        if (restaurant.getRatings().equals("N/A")) {
            holder.itemRatings.setText("N/A");
        } else {
            holder.itemRatings.setText(String.format(context.getString(R.string.item_ratings), Float.parseFloat(restaurant.getRatings())));
        }
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImg;
        ImageButton btnMore;
        TextView itemName, itemDesc, itemRatings, itemRatingCount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImg = itemView.findViewById(R.id.my_coll_detail_ava);
            itemName = itemView.findViewById(R.id.my_coll_detail_title);
            itemDesc = itemView.findViewById(R.id.my_coll_detail_desc);
            itemRatings = itemView.findViewById(R.id.my_coll_detail_ratings);
            itemRatingCount = itemView.findViewById(R.id.my_coll_detail_rating_count);
            btnMore = itemView.findViewById(R.id.my_coll_detail_btn_more);
        }
    }
}
