package com.example.gourmetcompass.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.MainActivity;
import com.example.gourmetcompass.R;
import com.example.gourmetcompass.models.Restaurant;
import com.example.gourmetcompass.ui_restaurant_detail.RestaurantDetailActivity;

import java.util.ArrayList;

public class HomeRVAdapter extends RecyclerView.Adapter<HomeRVAdapter.MyViewHolder> {

    Context context;
    ArrayList<Restaurant> restaurantList;

    public HomeRVAdapter(Context context, ArrayList<Restaurant> restaurantList) {
        this.context = context;
        this.restaurantList = restaurantList;
    }

    @NonNull
    @Override
    public HomeRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_res_home, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeRVAdapter.MyViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);
        holder.resName.setText(restaurant.getName());
        holder.resImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to RestaurantDetailActivity
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(context, RestaurantDetailActivity.class);
                    intent.putExtra("restaurantId", restaurantList.get(pos).getId());
                    context.startActivity(intent);
                    if (context instanceof MainActivity) {
                        ((MainActivity) context).overridePendingTransition(R.anim.slide_in, R.anim.stay_still);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView resName;
        ImageButton resImgBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            resName = itemView.findViewById(R.id.res_name_home);
            resImgBtn = itemView.findViewById(R.id.res_img_home);
            // TODO: Add image here
        }
    }
}
