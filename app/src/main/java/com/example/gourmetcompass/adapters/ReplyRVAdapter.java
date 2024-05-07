package com.example.gourmetcompass.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.firebase.FirestoreUtil;
import com.example.gourmetcompass.models.Reply;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ReplyRVAdapter extends RecyclerView.Adapter<ReplyRVAdapter.MyViewHolder> {

    private static final String TAG = "ReplyRVAdapter";
    Context context;
    ArrayList<Reply> replies;
    String restaurantId, reviewId;
    FirebaseUser user;
    FirebaseFirestore db;

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

        db.collection("restaurants").document(restaurantId)
                .collection("reviews").document(reviewId)
                .collection("replies").document(reply.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String replierId = document.getString("replierId");
                                String description = document.getString("description");

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
                    }
                });
    }

    private void setReplierUsername(String replierId, @NonNull ReplyRVAdapter.MyViewHolder holder) {
        db.collection("users").document(replierId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
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