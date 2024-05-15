package com.example.gourmetcompass.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gourmetcompass.R;
import com.example.gourmetcompass.utils.FirestoreUtil;
import com.example.gourmetcompass.models.Review;
import com.example.gourmetcompass.ui_restaurant_detail.RestaurantDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MyReviewsRVAdapter extends RecyclerView.Adapter<MyReviewsRVAdapter.MyViewHolder> {
    Context context;
    ArrayList<Review> reviewList;
    FirebaseFirestore db;
    FirebaseUser user;

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

        setResName(holder, review.getRestaurantId());
        holder.reviewContent.setText(review.getDescription());
        holder.reviewTime.setText(getTimePassed(review.getTimestamp()));
        Glide.with(context)
                .load("")
                .placeholder(R.drawable.bg_shimmer)
                .into(holder.resImg);

        holder.view.setOnClickListener(v -> {
            Intent intent = new Intent(holder.view.getContext(), RestaurantDetailActivity.class);
            intent.putExtra("restaurantId", review.getRestaurantId());
            holder.view.getContext().startActivity(intent);
            if (holder.view.getContext() instanceof Activity) {
                ((Activity) holder.view.getContext()).overridePendingTransition(R.anim.slide_in, R.anim.stay_still);
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

        View view;
        ImageView resImg;
        TextView reviewContent, reviewTime, resName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            resImg = itemView.findViewById(R.id.my_reviews_res_img);
            reviewContent = itemView.findViewById(R.id.my_reviews_content);
            reviewTime = itemView.findViewById(R.id.my_reviews_time);
            resName = itemView.findViewById(R.id.my_reviews_res_name);
        }
    }

}
