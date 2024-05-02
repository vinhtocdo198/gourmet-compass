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
import com.example.gourmetcompass.models.Restaurant;

import java.util.ArrayList;

public class RestaurantMyCollsListRVAdapter extends RecyclerView.Adapter<RestaurantMyCollsListRVAdapter.MyViewHolder> {
    Context context;
    ArrayList<Restaurant> restaurantList;

    public RestaurantMyCollsListRVAdapter(Context context, ArrayList<Restaurant> restaurantList) {
        this.context = context;
        this.restaurantList = restaurantList;
    }

    @NonNull
    @Override
    public RestaurantMyCollsListRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_res_my_colls, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantMyCollsListRVAdapter.MyViewHolder holder, int position) {
        holder.restaurantName.setText(restaurantList.get(position).getName());
        holder.restaurantDescription.setText(restaurantList.get(position).getDescription());
        //holder.restaurantImage.setImageResource(restaurantList.get(position).get());
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView restaurantImage;
        TextView restaurantName, restaurantDescription;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            restaurantImage = itemView.findViewById(R.id.res_my_colls_res_img);
            restaurantName = itemView.findViewById(R.id.res_my_colls_res_title);
            restaurantDescription = itemView.findViewById(R.id.res_my_colls_res_desc);
        }
    }
}
