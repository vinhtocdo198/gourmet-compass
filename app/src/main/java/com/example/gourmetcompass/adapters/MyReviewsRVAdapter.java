package com.example.gourmetcompass.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.gourmetcompass.R;
import com.example.gourmetcompass.utils.FirestoreUtil;
import com.example.gourmetcompass.models.Review;
import com.example.gourmetcompass.views.restaurant_detail.RestaurantDetailActivity;
import com.example.gourmetcompass.utils.StorageUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MyReviewsRVAdapter extends RecyclerView.Adapter<MyReviewsRVAdapter.MyViewHolder> {

    private static final String TAG = "MyReviewsRVAdapter";
    Context context;
    ArrayList<Review> reviewList;
    FirebaseFirestore db;
    FirebaseUser user;
    StorageReference storageRef;

    public MyReviewsRVAdapter(Context context, ArrayList<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public MyReviewsRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review_my_reviews, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyReviewsRVAdapter.MyViewHolder holder, int position) {
        Review review = reviewList.get(position);

        // Init firebase services
        db = FirestoreUtil.getInstance().getFirestore();
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = StorageUtil.getInstance().getStorage().getReference();

        setResName(holder, review.getRestaurantId());
        holder.reviewContent.setText(review.getDescription());
        holder.reviewTime.setText(getTimePassed(review.getTimestamp()));
        getResThumbnailImg(holder, review);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), RestaurantDetailActivity.class);
            intent.putExtra("restaurantId", review.getRestaurantId());
            holder.itemView.getContext().startActivity(intent);
            if (holder.itemView.getContext() instanceof Activity) {
                ((Activity) holder.itemView.getContext()).overridePendingTransition(R.anim.slide_in, R.anim.stay_still);
            }
        });
    }

    private void getResThumbnailImg(@NonNull MyViewHolder holder, Review review) {
        storageRef.child("res_images/" + review.getRestaurantId() + "/app_bar/app_bar.jpg")
                .getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri resBgUrl = task.getResult();
                        Glide.with(context)
                                .load(resBgUrl)
                                .placeholder(R.drawable.bg_shimmer)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .centerCrop()
                                .into(holder.resImg);
                    } else {
                        Log.e(TAG, "Failed to fetch restaurant background image", task.getException());
                    }
                });
    }

    @NonNull
    private String getTimePassed(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - timestamp;

        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeDifference);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifference);
        long hours = TimeUnit.MILLISECONDS.toHours(timeDifference);
        long days = TimeUnit.MILLISECONDS.toDays(timeDifference);

        if (seconds < 60) {
            return seconds + "s";
        } else if (minutes < 60) {
            return minutes + "m";
        } else if (hours < 24) {
            return hours + "h";
        } else {
            return days + "d";
        }
    }

    private void setResName(@NonNull MyViewHolder holder, String restaurantId) {
        db.collection("restaurants")
                .document(restaurantId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        holder.resName.setText(documentSnapshot.getString("name"));
                    }
                });
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView resImg;
        TextView reviewContent, reviewTime, resName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            resImg = itemView.findViewById(R.id.my_reviews_res_img);
            reviewContent = itemView.findViewById(R.id.my_reviews_content);
            reviewTime = itemView.findViewById(R.id.my_reviews_time);
            resName = itemView.findViewById(R.id.my_reviews_res_name);
        }
    }

}
