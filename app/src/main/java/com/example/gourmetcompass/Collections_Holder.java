package com.example.gourmetcompass;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class Collections_Holder extends RecyclerView.ViewHolder {
    TextView collectionName;

    public Collections_Holder(@NonNull View itemView) {
        super(itemView);
        collectionName = itemView.findViewById(R.id.CollectionName);
    }
}
