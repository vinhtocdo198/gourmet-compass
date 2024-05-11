package com.example.gourmetcompass.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.models.Restaurant;
import com.example.gourmetcompass.ui_restaurant_detail.RestaurantDetailActivity;
import com.example.gourmetcompass.utils.FirestoreUtil;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SearchResultRVAdapter extends RecyclerView.Adapter<SearchResultRVAdapter.MyViewHolder> {

    Context context;
    ArrayList<Restaurant> restaurantList;
    FirebaseFirestore db;

    public SearchResultRVAdapter(Context context, ArrayList<Restaurant> restaurantList) {
        this.context = context;
        this.restaurantList = restaurantList;
    }

    @NonNull
    @Override
    public SearchResultRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_coll_detail, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultRVAdapter.MyViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);

        // Init firebase services
        db = FirestoreUtil.getInstance().getFirestore();

        holder.itemName.setText(restaurant.getName());
        holder.itemDesc.setText(restaurant.getDescription());
        holder.itemRatings.setText(String.valueOf(restaurant.getRatings()));
        holder.btnMore.setVisibility(View.GONE);
        setResRatings(holder, restaurant.getId());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), RestaurantDetailActivity.class);
            intent.putExtra("restaurantId", restaurant.getId());
            holder.itemView.getContext().startActivity(intent);
            if (holder.itemView.getContext() instanceof Activity) {
                ((Activity) holder.itemView.getContext()).overridePendingTransition(R.anim.slide_in, R.anim.stay_still);
            }
        });
    }

    private void setResRatings(@NonNull SearchResultRVAdapter.MyViewHolder holder, String restaurantId) {
        db.collection("restaurants")
                .document(restaurantId)
                .collection("reviews")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int reviewCount = task.getResult().size();
                        if (reviewCount > 0) {
                            holder.itemRatingCount.setText(String.format(context.getString(R.string.rating_count), reviewCount));
                        } else {
                            holder.itemRatingCount.setText("(N/A)");
                        }
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.itemRatingCount.getLayoutParams();
                        params.setMarginEnd(20);
                        holder.itemRatingCount.setLayoutParams(params);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImg;
        ImageButton btnMore;
        TextView itemName, itemDesc, itemRatings, itemRatingCount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImg = itemView.findViewById(R.id.my_coll_detail_ava);
            itemName = itemView.findViewById(R.id.my_coll_detail_title);
            itemDesc = itemView.findViewById(R.id.my_coll_detail_desc);
            itemRatings = itemView.findViewById(R.id.my_coll_detail_ratings);
            itemRatingCount = itemView.findViewById(R.id.my_coll_detail_rating_count);
            btnMore = itemView.findViewById(R.id.my_coll_detail_btn_more);
        }
    }
}
