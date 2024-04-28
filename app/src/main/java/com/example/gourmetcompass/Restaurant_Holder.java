package com.example.gourmetcompass;

import android.media.Rating;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

public class Restaurant_Holder extends RecyclerView.ViewHolder {

    ImageView restaurantImage;
    TextView restaurantName;
    TextView restaurantDescription;
    RatingBar ratingBar;
    public Restaurant_Holder(@NonNull View itemView) {
        super(itemView);
        restaurantImage = itemView.findViewById(R.id.restaurant_img);
        restaurantName = itemView.findViewById(R.id.restaurant_name);
        restaurantDescription = itemView.findViewById(R.id.restaurant_description);
        ratingBar = itemView.findViewById(R.id.rating_bar);


    }
}
