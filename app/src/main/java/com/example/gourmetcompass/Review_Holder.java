package com.example.gourmetcompass;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Review_Holder extends RecyclerView.ViewHolder {
    ImageView restaurantImage;
    TextView reviewDetail, restaurantName;
    public Review_Holder(@NonNull View itemView) {
        super(itemView);
        restaurantImage = itemView.findViewById(R.id.restaurant_img);
        reviewDetail = itemView.findViewById(R.id.review_details);
        restaurantName = itemView.findViewById(R.id.restaurant_name);

    }
}
