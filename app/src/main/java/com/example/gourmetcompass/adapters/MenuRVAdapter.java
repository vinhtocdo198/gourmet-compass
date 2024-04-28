package com.example.gourmetcompass.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.models.Dish;

import java.util.ArrayList;

public class MenuRVAdapter extends RecyclerView.Adapter<MenuRVAdapter.MyViewHolder> {

    Context context;
    ArrayList<Dish> menu;

    public MenuRVAdapter(Context context, ArrayList<Dish> menu) {
        this.context = context;
        this.menu = menu;
    }

    @NonNull
    @Override
    public MenuRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuRVAdapter.MyViewHolder holder, int position) {
        Dish dish = menu.get(position);
        holder.dishName.setText(dish.getName());
        holder.dishDesc.setText(dish.getDescription());
        holder.dishRatings.setText(dish.getRatings());
        holder.dishRatingCount.setText(String.format(context.getString(R.string.rating_count), dish.getRatingCount()));
        holder.dishImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "More info about " + dish.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return menu.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView dishName, dishDesc, dishRatingCount, dishRatings;
        ImageView dishImg;
        ImageButton dishImgBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            dishName = itemView.findViewById(R.id.dish_title);
            dishDesc = itemView.findViewById(R.id.dish_desc);
            dishRatings = itemView.findViewById(R.id.dish_ratings);
            dishRatingCount = itemView.findViewById(R.id.dish_rating_count);
            dishImg = itemView.findViewById(R.id.dish_img);
            dishImgBtn = itemView.findViewById(R.id.dish_btn_more);
            // TODO: Add image here
        }
    }
}
