package com.example.gourmetcompass.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.gourmetcompass.MainActivity;
import com.example.gourmetcompass.R;
import com.example.gourmetcompass.models.RestaurantCategory;
import com.example.gourmetcompass.ui_general.SearchResultActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class CategoryRVAdapter extends RecyclerView.Adapter<CategoryRVAdapter.MyViewHolder> {

    Context context;
    ArrayList<RestaurantCategory> categoryList;
    FirebaseUser user;

    public CategoryRVAdapter(Context context, ArrayList<RestaurantCategory> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_res_category, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryRVAdapter.MyViewHolder holder, int position) {
        RestaurantCategory category = categoryList.get(position);

        // Init firebase services
        user = FirebaseAuth.getInstance().getCurrentUser();

        holder.categoryName.setText(category.getName());
        Glide.with(context)
                .load(category.getImageUrl())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.categoryImg);

        holder.categoryImg.setOnClickListener(v -> {
            if (user != null) {
                Intent intent = new Intent(context, SearchResultActivity.class);
                intent.putExtra("searchQuery", category.getName());
                context.startActivity(intent);
                if (context instanceof MainActivity) {
                    ((MainActivity) context).overridePendingTransition(R.anim.slide_in, R.anim.stay_still);
                }
            } else {
                if (context instanceof MainActivity) {
                    ((MainActivity) context).selectBottomNavItem(R.id.account_fragment);
                }
                Toast.makeText(context, "Log in to see our restaurants", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView categoryName;
        ImageView categoryImg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryImg = itemView.findViewById(R.id.res_category_img);
            categoryName = itemView.findViewById(R.id.res_category_name);
        }
    }
}
