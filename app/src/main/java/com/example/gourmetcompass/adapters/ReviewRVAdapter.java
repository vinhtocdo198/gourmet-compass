package com.example.gourmetcompass.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.R;

public class ReviewRVAdapter extends RecyclerView.Adapter<ReviewRVAdapter.MyViewHolder> {

    Context context;
//    ArrayList<Review> reviews; // TODO: Create Review class

    public ReviewRVAdapter(Context context) {
        this.context = context;
//        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewRVAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewRVAdapter.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView reviewerName, reviewContent, reviewTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            reviewerName = itemView.findViewById(R.id.reviewer_name);
            reviewContent = itemView.findViewById(R.id.review_content);
            reviewTime = itemView.findViewById(R.id.review_time);

        }
    }
}
