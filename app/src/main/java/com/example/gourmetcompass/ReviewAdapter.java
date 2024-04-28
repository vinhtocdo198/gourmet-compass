package com.example.gourmetcompass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<Review_Holder> {
    Context context;
    List<Review> reviewList;

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public Review_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Review_Holder(LayoutInflater.from(context).inflate(R.layout.single_review, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Review_Holder holder, int position) {
        holder.restaurantName.setText(reviewList.get(position).getClass().getName());
        //holder.restaurantImage.setImageResource(reviewList.get(position).getClass().getImage());
        holder.reviewDetail.setText(reviewList.get(position).getReviewDescription());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }
}
