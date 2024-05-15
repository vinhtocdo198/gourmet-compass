package com.example.gourmetcompass.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.Notification;
import com.example.gourmetcompass.Notification_Holder;
import com.example.gourmetcompass.R;

import java.util.List;

public class Notification_Adapter extends RecyclerView.Adapter<Notification_Holder> {

    Context context;
    List<Notification> notifications;

    public Notification_Adapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }
    @NonNull
    @Override
    public Notification_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Notification_Holder(LayoutInflater.from(context).inflate(R.layout.single_noti_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Notification_Holder holder, int position) {
        holder.icon.setImageResource(notifications.get(position).getIcon());
        holder.description.setText(notifications.get(position).getDescription());
        holder.notification_time.setText(notifications.get(position).getTimestamp());
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }
}
