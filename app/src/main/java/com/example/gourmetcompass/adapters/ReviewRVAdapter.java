package com.example.gourmetcompass.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.gourmetcompass.R;
import com.example.gourmetcompass.models.Reply;
import com.example.gourmetcompass.models.Review;
import com.example.gourmetcompass.utils.ButtonUtil;
import com.example.gourmetcompass.utils.FirestoreUtil;
import com.example.gourmetcompass.utils.StorageUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ReviewRVAdapter extends RecyclerView.Adapter<ReviewRVAdapter.MyViewHolder> {

    private static final String TAG = "ReviewRVAdapter";
    Context context;
    ArrayList<Review> reviews;
    ArrayList<Reply> replies;
    ReplyRVAdapter replyRVAdapter;
    String restaurantId;
    FirebaseUser user;
    FirebaseFirestore db;
    StorageReference storageRef;
    Uri reviewerAvaUri;

    public ReviewRVAdapter(Context context, ArrayList<Review> reviews, String restaurantId) {
        this.context = context;
        this.reviews = reviews;
        this.restaurantId = restaurantId;
    }

    @NonNull
    @Override
    public ReviewRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review_res, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewRVAdapter.MyViewHolder holder, int position) {
        Review review = reviews.get(position);

        // Init firebase services
        db = FirestoreUtil.getInstance().getFirestore();
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = StorageUtil.getInstance().getStorage().getReference();

        // Fetch review data
        holder.reviewContent.setText(review.getDescription());
        holder.reviewRatings.setText(String.valueOf((int) Float.parseFloat(review.getRatings())));
        holder.reviewerName.setText(review.getReviewerName());
        holder.reviewTime.setText(getTimePassed(review.getTimestamp()));
        Glide.with(context)
                .load(review.getReviewerAvaUrl())
                .placeholder(R.drawable.ic_default_avatar)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.reviewerAvaImg);
        setReactButtonsStatus(holder, review);
        setReplyButton(holder, review);

        // Set up replies recyclerview
        holder.repliesRV.setLayoutManager(new LinearLayoutManager(context));
        holder.repliesRV.setHasFixedSize(true);
        setReplyData(holder, review);

        // Set up buttons
        holder.replyBtn.setOnClickListener(v -> openReplyDialog(review));
        holder.likeBtn.setOnClickListener(v -> likeReview(holder, review));
        holder.dislikeBtn.setOnClickListener(v -> dislikeReview(holder, review));
        setCollapseReplyButton(holder, review);

    }

    private void setCollapseReplyButton(@NonNull MyViewHolder holder, Review review) {
        db.collection("restaurants")
                .document(restaurantId)
                .collection("reviews")
                .document(review.getId())
                .collection("replies")
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    if (value != null && !value.isEmpty()) {
                        holder.showRepliesBtn.setVisibility(View.VISIBLE);
                    } else {
                        holder.showRepliesBtn.setVisibility(View.GONE);
                    }
                });
        holder.showRepliesBtn.setOnClickListener(v -> {
            if (holder.repliesRV.getVisibility() == View.VISIBLE) {
                holder.repliesRV.setVisibility(View.GONE);
            } else {
                holder.repliesRV.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setReplyData(@NonNull MyViewHolder holder, Review review) {

        db.collection("restaurants")
                .document(restaurantId)
                .collection("reviews")
                .document(review.getId())
                .collection("replies")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (value != null) {
                            replies = new ArrayList<>();
                            for (QueryDocumentSnapshot document : value) {
                                Reply reply = document.toObject(Reply.class);
                                reply.setId(document.getId());
                                replies.add(reply);
                            }
                            replyRVAdapter = new ReplyRVAdapter(context, replies, restaurantId, review.getId());
                            holder.repliesRV.setAdapter(replyRVAdapter);
                            replyRVAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void dislikeReview(@NonNull MyViewHolder holder, Review review) {
        ArrayList<String> likedUserIds = review.getLikedUserIds();
        ArrayList<String> dislikedUserIds = review.getDislikedUserIds();
        if (dislikedUserIds.contains(user.getUid())) {
            dislikedUserIds.remove(user.getUid());
            holder.dislikeBtn.setBackgroundResource(R.drawable.btn_react);
        } else {
            dislikedUserIds.add(user.getUid());
            holder.dislikeBtn.setBackgroundResource(R.drawable.btn_react_pressed);

            if (likedUserIds.contains(user.getUid())) {
                likedUserIds.remove(user.getUid());
                holder.likeBtn.setBackgroundResource(R.drawable.btn_react);
            }
        }

        // Update Firestore
        db.collection("restaurants").document(restaurantId)
                .collection("reviews").document(review.getId())
                .update("likedUserIds", likedUserIds, "dislikedUserIds", dislikedUserIds)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        holder.likeBtn.setText(String.format(context.getString(R.string.react_count), review.getLikedUserIds().size()));
                        holder.dislikeBtn.setText(String.format(context.getString(R.string.react_count), review.getDislikedUserIds().size()));
                    } else {
                        Log.d(TAG, "Error updating document", task.getException());
                    }
                });
    }

    private void likeReview(@NonNull MyViewHolder holder, Review review) {
        ArrayList<String> likedUserIds = review.getLikedUserIds();
        ArrayList<String> dislikedUserIds = review.getDislikedUserIds();
        if (likedUserIds.contains(user.getUid())) {
            likedUserIds.remove(user.getUid());
            holder.likeBtn.setBackgroundResource(R.drawable.btn_react);
        } else {
            likedUserIds.add(user.getUid());
            holder.likeBtn.setBackgroundResource(R.drawable.btn_react_pressed);

            if (dislikedUserIds.contains(user.getUid())) {
                dislikedUserIds.remove(user.getUid());
                holder.dislikeBtn.setBackgroundResource(R.drawable.btn_react);
            }
        }

        // Update Firestore
        db.collection("restaurants").document(restaurantId)
                .collection("reviews").document(review.getId())
                .update("likedUserIds", likedUserIds, "dislikedUserIds", dislikedUserIds)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        holder.likeBtn.setText(String.format(context.getString(R.string.react_count), likedUserIds.size()));
                        holder.dislikeBtn.setText(String.format(context.getString(R.string.react_count), dislikedUserIds.size()));
                    } else {
                        Log.d(TAG, "Error updating document", task.getException());
                    }
                });
    }

    @SuppressWarnings("unchecked")
    private void setReactButtonsStatus(@NonNull MyViewHolder holder, Review review) {
        db.collection("restaurants")
                .document(restaurantId)
                .collection("reviews")
                .document(review.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            ArrayList<String> likedUserIds = (ArrayList<String>) document.get("likedUserIds");
                            ArrayList<String> dislikedUserIds = (ArrayList<String>) document.get("dislikedUserIds");

                            holder.likeBtn.setText(String.format(context.getString(R.string.react_count), likedUserIds != null ? likedUserIds.size() : 0));
                            holder.dislikeBtn.setText(String.format(context.getString(R.string.react_count), dislikedUserIds != null ? dislikedUserIds.size() : 0));

                            if (likedUserIds != null && likedUserIds.contains(user.getUid())) {
                                holder.likeBtn.setBackgroundResource(R.drawable.btn_react_pressed);
                            }

                            if (dislikedUserIds != null && dislikedUserIds.contains(user.getUid())) {
                                holder.dislikeBtn.setBackgroundResource(R.drawable.btn_react_pressed);
                            }
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "Get failed with ", task.getException());
                    }
                });
    }

    private void setReplyButton(@NonNull MyViewHolder holder, Review review) {
        db.collection("restaurants")
                .document(restaurantId)
                .collection("reviews")
                .document(review.getId())
                .collection("replies")
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    if (value != null) {
                        holder.replyBtn.setText(String.format(context.getString(R.string.react_count), value.size()));
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

    private void openReplyDialog(Review review) {
        // Create a dialog for replying to the review
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_reply_review);

        // Set the dialog width and height
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            layoutParams.width = (int) (metrics.widthPixels * 0.9); // 90% of screen width
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        }
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_dialog));

        // Init dialog views
        TextView reviewerName = dialog.findViewById(R.id.reply_review_dialog_title);
        EditText replyTextField = dialog.findViewById(R.id.reply_review_dialog_content);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel_reply);
        Button btnSubmit = dialog.findViewById(R.id.btn_submit_reply);
        reviewerName.setText(context.getString(R.string.reply_review, review.getReviewerName()));

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSubmit.setOnClickListener(v -> {
            String replyContent = replyTextField.getText().toString();
            if (replyContent.isEmpty()) {
                Toast.makeText(context, "Reply cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            replyToReview(review.getId(), replyContent);

            dialog.dismiss();
        });

        dialog.show();
    }

    private void replyToReview(String reviewId, String replyContent) {

        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String username = document.getString("username");
                            String userAvaUrl = document.getString("avaUrl");
                            // Create a reply object
                            Map<String, Object> reply = new HashMap<>();
                            reply.put("timestamp", System.currentTimeMillis());
                            reply.put("description", replyContent);
                            reply.put("replierName", username);
                            reply.put("replierAvaUrl", userAvaUrl);

                            // Add the reply to Firestore
                            db.collection("restaurants")
                                    .document(restaurantId)
                                    .collection("reviews")
                                    .document(reviewId)
                                    .collection("replies")
                                    .add(reply)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(context, "Reply submitted", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, "Failed to submit reply", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "Get failed with ", task.getException());
                    }
                });
    }

    public void updateData(ArrayList<Review> newReviews) {
        this.reviews = newReviews;
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        RecyclerView repliesRV;
        TextView reviewerName, reviewContent, reviewTime, reviewRatings;
        ButtonUtil likeBtn, dislikeBtn, replyBtn;
        Button showRepliesBtn;
        ImageView reviewerAvaImg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            reviewerName = itemView.findViewById(R.id.reviewer_name);
            reviewContent = itemView.findViewById(R.id.review_content);
            reviewTime = itemView.findViewById(R.id.review_time);
            reviewRatings = itemView.findViewById(R.id.review_ratings);
            likeBtn = itemView.findViewById(R.id.like_btn);
            dislikeBtn = itemView.findViewById(R.id.dislike_btn);
            replyBtn = itemView.findViewById(R.id.reply_btn);
            showRepliesBtn = itemView.findViewById(R.id.show_replies_btn);
            repliesRV = itemView.findViewById(R.id.reply_recyclerview);
            reviewerAvaImg = itemView.findViewById(R.id.reviewer_ava);
        }
    }
}
