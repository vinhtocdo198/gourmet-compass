package com.example.gourmetcompass.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gourmetcompass.R;
import com.example.gourmetcompass.firebase.FirestoreUtil;
import com.example.gourmetcompass.firebase.StorageUtil;
import com.example.gourmetcompass.models.Reply;
import com.example.gourmetcompass.models.Review;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ReplyRVAdapter extends RecyclerView.Adapter<ReplyRVAdapter.MyViewHolder> {

    private static final String TAG = "ReplyRVAdapter";
    Context context;
    ArrayList<Reply> replies;
    String restaurantId, reviewId;
    FirebaseUser user;
    FirebaseFirestore db;
    StorageReference storageRef;
    Uri replierAvaUri;

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
        setReplyData(holder, reply);
    }

    private void setReplyData(@NonNull MyViewHolder holder, Reply reply) {
        db.collection("restaurants").document(restaurantId)
                .collection("reviews").document(reviewId)
                .collection("replies").document(reply.getId())
//                .addSnapshotListener((value, e) -> {
//                    if (e != null) {
//                        Log.w(TAG, "Listen failed.", e);
//                        return;
//                    }
//
//                    if (value != null && value.exists()) {
//                        String replierId = value.getString("replierId");
//                        String description = value.getString("description");
//
//                        // Get the replier username and avatar
//                        setReplierAvatar(replierId, holder);
//                        setReplierUsername(replierId, holder);
//                        holder.replyContent.setText(description);
//
//                        // Calculate the time passed since the review was posted
//                        Long timestamp = value.getLong("timestamp");
//                        if (timestamp != null) {
//                            holder.replyTime.setText(getTimePassed(timestamp));
//                        } else {
//                            // Handle the case where timestamp is null
//                            holder.replyTime.setText("N/A");
//                        }
//                    } else {
//                        Log.d(TAG, "No such document");
//                    }
//                });
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String replierId = document.getString("replierId");
                            String description = document.getString("description");

                            // Get the replier username and avatar
                            setReplierAvatar(replierId, holder);
                            setReplierUsername(replierId, holder);
                            holder.replyContent.setText(description);

                            // Calculate the time passed since the review was posted
                            Long timestamp = document.getLong("timestamp");
                            if (timestamp != null) {
                                holder.replyTime.setText(getTimePassed(timestamp));
                            } else {
                                // Handle the case where timestamp is null
                                holder.replyTime.setText("N/A");
                            }
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "Get failed with ", task.getException());
                    }
                });
    }

    private void setReplierAvatar(String replierId, MyViewHolder holder) {
        storageRef.child("user_images/" + replierId + "/avatar/")
                .getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        replierAvaUri = task.getResult();
                        Glide.with(context)
                                .load(replierAvaUri)
                                .into(holder.replierAvaImg);
                    } else {
                        Log.d(TAG, "getUserInformation: Failed to get avatar");
                    }
                });
    }

    private void setReplierUsername(String replierId, @NonNull ReplyRVAdapter.MyViewHolder holder) {
        db.collection("users").document(replierId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String replierName = document.getString("username");
                            holder.replierName.setText(replierName);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "Get failed with ", task.getException());
                    }
                });
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

    public void updateData(ArrayList<Reply> newReplies) {
        this.replies = newReplies;
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
