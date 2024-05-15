package com.example.gourmetcompass.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.gourmetcompass.R;
import com.example.gourmetcompass.models.Reply;
import com.example.gourmetcompass.utils.FirestoreUtil;
import com.example.gourmetcompass.utils.StorageUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ReplyRVAdapter extends RecyclerView.Adapter<ReplyRVAdapter.MyViewHolder> {

    Context context;
    ArrayList<Reply> replies;
    String restaurantId, reviewId;
    FirebaseUser user;
    FirebaseFirestore db;
    StorageReference storageRef;

    public ReplyRVAdapter(Context context, ArrayList<Reply> replies, String restaurantId, String reviewId) {
        this.context = context;
        this.replies = replies;
        this.restaurantId = restaurantId;
        this.reviewId = reviewId;
    }

    @NonNull
    @Override
    public ReplyRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reply_review_res, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyRVAdapter.MyViewHolder holder, int position) {
        Reply reply = replies.get(position);

        // Init firebase services
        db = FirestoreUtil.getInstance().getFirestore();
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = StorageUtil.getInstance().getStorage().getReference();

        // Fetch reply data
        holder.replyContent.setText(reply.getDescription());
        holder.replyTime.setText(getTimePassed(reply.getTimestamp()));
        holder.replierName.setText(reply.getReplierName());
        Glide.with(context)
                .load(reply.getReplierAvaUrl())
                .placeholder(R.drawable.ic_default_avatar)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.replierAvaImg);
    }

    @NonNull
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
        return replies.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView replierAvaImg;
        TextView replierName, replyTime, replyContent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            replierAvaImg = itemView.findViewById(R.id.replier_ava);
            replierName = itemView.findViewById(R.id.replier_name);
            replyTime = itemView.findViewById(R.id.reply_time);
            replyContent = itemView.findViewById(R.id.reply_content);
        }
    }
}
