package com.example.gourmetcompass;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Notification_Holder extends RecyclerView.ViewHolder{

    public ImageView icon;
    public TextView description;
    public TextView notification_time;
    public Notification_Holder(@NonNull View itemView) {
        super(itemView);
        icon = itemView.findViewById(R.id.noti_icon);
        description = itemView.findViewById(R.id.noti_description);
        notification_time = itemView.findViewById(R.id.noti_time);
    }
}
