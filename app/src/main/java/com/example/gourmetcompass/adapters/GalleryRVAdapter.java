package com.example.gourmetcompass.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gourmetcompass.R;

import java.util.ArrayList;

public class GalleryRVAdapter extends RecyclerView.Adapter<GalleryRVAdapter.MyViewHolder> {

    Context context;
    ArrayList<String> imageUrls;

    public GalleryRVAdapter(Context context, ArrayList<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public GalleryRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gallery, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryRVAdapter.MyViewHolder holder, int position) {
        Glide.with(context).load(imageUrls.get(position)).into(holder.img);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.gallery_img);
        }
    }
}
