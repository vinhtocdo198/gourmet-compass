package com.example.gourmetcompass.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.models.UserCollection;
import com.example.gourmetcompass.ui_personal.MyCollectionDetailActivity;

import java.util.ArrayList;

public class MyCollectionsRVAdapter extends RecyclerView.Adapter<MyCollectionsRVAdapter.MyViewHolder> {

    private final String TAG = "MyCollectionsRVAdapter";
    Context context;
    ArrayList<UserCollection> collList;

    public MyCollectionsRVAdapter(Context context, ArrayList<UserCollection> collList) {
        this.context = context;
        this.collList = collList;
    }

    @NonNull
    @Override
    public MyCollectionsRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_coll_my_colls, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCollectionsRVAdapter.MyViewHolder holder, int position) {
        UserCollection collection = collList.get(position);

        holder.collNameBtn.setText(collList.get(position).getName());
        holder.collNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyCollectionDetailActivity.class);
                intent.putExtra("collectionId", collection.getId());
                context.startActivity(intent);
                if (context instanceof Activity) {
                    ((Activity) context).overridePendingTransition(R.anim.slide_in, R.anim.stay_still);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return collList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        Button collNameBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            collNameBtn = itemView.findViewById(R.id.btn_my_coll_name);
        }
    }

}
