package com.example.gourmetcompass.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.MainActivity;
import com.example.gourmetcompass.models.Notification;
import com.example.gourmetcompass.R;
import com.example.gourmetcompass.ui_restaurant_detail.RestaurantDetailActivity;
import com.example.gourmetcompass.utils.FirestoreUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class NotiRVAdapter extends RecyclerView.Adapter<NotiRVAdapter.MyViewHolder> {

    private static final String TAG = "NotiRVAdapter";
    Context context;
    List<Notification> notiList;
    FirebaseFirestore db;
    FirebaseUser user;

    public NotiRVAdapter(Context context, List<Notification> notiList) {
        this.context = context;
        this.notiList = notiList;
    }

    @NonNull
    @Override
    public NotiRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_noti, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotiRVAdapter.MyViewHolder holder, int position) {
        Notification noti = notiList.get(position);

        // Init firebase services
        db = FirestoreUtil.getInstance().getFirestore();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (noti.isChecked()) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.noti_read));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.app_bg));
        }

        switch (noti.getType()) {
            case "like":
                holder.notiTitle.setText(context.getString(R.string.liked_review));
                holder.notiIcon.setImageResource(R.drawable.ic_like);
                break;
            case "dislike":
                holder.notiTitle.setText(context.getString(R.string.disliked_review));
                holder.notiIcon.setImageResource(R.drawable.ic_dislike);
                break;
            case "reply":
                holder.notiTitle.setText(context.getString(R.string.replied_review));
                holder.notiIcon.setImageResource(R.drawable.ic_rep);
                break;
        }
        holder.notiDesc.setText(noti.getReviewDesc());
        holder.notiTime.setText(getTimePassed(noti.getTimestamp()));

        // Navigate to corresponding restaurant when a noti is clicked
        holder.itemView.setOnClickListener(v -> {
            // Check if the review still exists
            db.collection("reviews")
                    .document(noti.getReviewId())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Intent intent = new Intent(context, RestaurantDetailActivity.class);
                                intent.putExtra("restaurantId", noti.getRestaurantId());
                                context.startActivity(intent);
                                if (context instanceof MainActivity) {
                                    ((MainActivity) context).overridePendingTransition(R.anim.slide_in, R.anim.stay_still);
                                }

                                // Mark the notification as read
                                if (!noti.isChecked()) {
                                    db.collection("users")
                                            .document(user.getUid())
                                            .collection("notifications")
                                            .document(noti.getId())
                                            .update("checked", true)
                                            .addOnCompleteListener(readTask -> {
                                                if (readTask.isSuccessful()) {
                                                    Log.d(TAG, "onBindViewHolder: Notification marked as read");
                                                }
                                            });
                                }
                            } else {
                                // If the review does not exist, show a Toast message
                                Toast.makeText(context, "Oops, your review has been deleted!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    });
        });
    }

    private String getTimePassed(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - timestamp;

        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeDifference);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifference);
        long hours = TimeUnit.MILLISECONDS.toHours(timeDifference);
        long days = TimeUnit.MILLISECONDS.toDays(timeDifference);

        if (seconds < 60) {
            return seconds + "s";
        } else if (minutes < 60) {
            return minutes + "m";
        } else if (hours < 24) {
            return hours + "h";
        } else {
            return days + "d";
        }
    }

    @Override
    public int getItemCount() {
        return notiList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView notiIcon;
        public TextView notiTitle, notiDesc, notiTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            notiIcon = itemView.findViewById(R.id.noti_icon);
            notiTitle = itemView.findViewById(R.id.noti_title);
            notiDesc = itemView.findViewById(R.id.noti_desc);
            notiTime = itemView.findViewById(R.id.noti_time);
        }
    }
}
