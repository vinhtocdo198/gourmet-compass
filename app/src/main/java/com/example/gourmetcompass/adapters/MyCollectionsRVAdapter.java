package com.example.gourmetcompass.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.SingleCollection;

import java.util.ArrayList;

public class MyCollectionsRVAdapter extends RecyclerView.Adapter<MyCollectionsRVAdapter.MyViewHolder> {

    Context context;
    ArrayList<SingleCollection> collectionList; // TODO: get user collections from database

    public MyCollectionsRVAdapter(Context context, ArrayList<SingleCollection> collectionList) {
        this.context = context;
        this.collectionList = collectionList;
    }

    @NonNull
    @Override
    public MyCollectionsRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_coll_my_colls, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCollectionsRVAdapter.MyViewHolder holder, int position) {
        holder.collectionNameBtn.setText(collectionList.get(position).getSingleCollectionName());
        holder.collectionNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to collection detail activity

            }
        });
    }

    @Override
    public int getItemCount() {
        return collectionList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        Button collectionNameBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            collectionNameBtn = itemView.findViewById(R.id.my_coll_btn);
        }
    }

}
