package com.example.gourmetcompass.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.Review;

import java.util.ArrayList;

public class MyReviewsRVAdapter extends RecyclerView.Adapter<MyReviewsRVAdapter.MyViewHolder> {
    Context context;
    ArrayList<Review> reviewList;

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
        holder.restaurantName.setText(reviewList.get(position).getClass().getName());
        //holder.restaurantImage.setImageResource(reviewList.get(position).getClass().getImage());
        holder.reviewContent.setText(reviewList.get(position).getReviewDescription());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView restaurantImage;
        TextView reviewContent, restaurantName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            restaurantImage = itemView.findViewById(R.id.my_reviews_res_img);
            reviewContent = itemView.findViewById(R.id.my_reviews_content);
            restaurantName = itemView.findViewById(R.id.my_reviews_res_name);
        }
    }

}
