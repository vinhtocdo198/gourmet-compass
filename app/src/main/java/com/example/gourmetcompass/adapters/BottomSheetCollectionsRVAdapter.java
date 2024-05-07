package com.example.gourmetcompass.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.models.MyCollection;

import java.util.ArrayList;

public class BottomSheetCollectionsRVAdapter extends RecyclerView.Adapter<BottomSheetCollectionsRVAdapter.MyViewHolder> {

    private static final String TAG = "BottomSheetCollectionsRVAdapter";
    Context context;
    ArrayList<MyCollection> collList;
    String restaurantId;

    public BottomSheetCollectionsRVAdapter(Context context, ArrayList<MyCollection> collList, String restaurantId) {
        this.context = context;
        this.collList = collList;
        this.restaurantId = restaurantId;
    }

    @NonNull
    @Override
    public BottomSheetCollectionsRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_coll_btms, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BottomSheetCollectionsRVAdapter.MyViewHolder holder, int position) {
        MyCollection userColl = collList.get(position);

        holder.collName.setText(collList.get(position).getName());

        // If the restaurant already exists in the collection, set checked to true
        holder.checkBox.setChecked(userColl.getRestaurants().contains(restaurantId));
        userColl.setChecked(holder.checkBox.isChecked());

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userColl.setChecked(holder.checkBox.isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return collList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView collName;
        CheckBox checkBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            collName = itemView.findViewById(R.id.btms_exist_coll_name);
            checkBox = itemView.findViewById(R.id.btms_exist_checkbox);
        }
    }
}
