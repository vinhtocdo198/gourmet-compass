package com.example.gourmetcompass.models;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.ui_restaurant_detail.RestaurantDetailActivity;

import java.util.ArrayList;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.MyViewHolder> {

    Context context;
    ArrayList<Restaurant> restaurantArrayList;

    public HomeRecyclerViewAdapter(Context context, ArrayList<Restaurant> restaurantArrayList) {
        this.context = context;
        this.restaurantArrayList = restaurantArrayList;
    }


    @NonNull
    @Override
    public HomeRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_res_home, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeRecyclerViewAdapter.MyViewHolder holder, int position) {
        Restaurant restaurant = restaurantArrayList.get(position);
        holder.name.setText(restaurant.name);
        holder.imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to RestaurantDetailActivity
                Intent intent = new Intent(context, RestaurantDetailActivity.class);
                intent.putExtra("restaurantId", restaurantArrayList.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageButton imageBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.res_name_home);
            imageBtn = itemView.findViewById(R.id.res_img_home);
            // TODO: Add image here
        }
    }
}
