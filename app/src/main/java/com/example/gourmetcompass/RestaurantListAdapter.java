package com.example.gourmetcompass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.models.Restaurant;

import java.util.List;

public class RestaurantListAdapter extends RecyclerView.Adapter<Restaurant_Holder> {
    Context context;
    List<Restaurant> restaurantList;

    public RestaurantListAdapter(Context context, List<Restaurant> restaurantList) {
        this.context = context;
        this.restaurantList = restaurantList;
    }

    @NonNull
    @Override
    public Restaurant_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Restaurant_Holder((LayoutInflater.from(context).inflate(R.layout.single_restaurant_view,parent,false)));
    }

    @Override
    public void onBindViewHolder(@NonNull Restaurant_Holder holder, int position) {
        holder.restaurantName.setText(restaurantList.get(position).getName());
        holder.restaurantDescription.setText(restaurantList.get(position).getDescription());
        //holder.restaurantImage.setImageResource(restaurantList.get(position).get());
        //holder.ratingBar.setRating(restaurantList.get(position).getRating());
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }
}
