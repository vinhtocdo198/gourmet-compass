package com.example.gourmetcompass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CollectionListAdapter extends RecyclerView.Adapter<Collections_Holder> {

    Context context;
    List<SingleCollection> collectionList;

    public CollectionListAdapter(Context context, List<SingleCollection> collectionList) {
        this.context = context;
        this.collectionList = collectionList;
    }

    @NonNull
    @Override
    public Collections_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Collections_Holder(LayoutInflater.from(context).inflate(R.layout.single_collection_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Collections_Holder holder, int position) {
        holder.collectionName.setText(collectionList.get(position).getSingleCollectionName());
    }

    @Override
    public int getItemCount() {
        return collectionList.size();
    }
}
