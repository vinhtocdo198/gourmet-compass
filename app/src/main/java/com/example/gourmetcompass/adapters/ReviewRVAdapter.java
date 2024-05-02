package com.example.gourmetcompass.adapters;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.firebase.FirestoreUtil;
import com.example.gourmetcompass.models.Review;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ReviewRVAdapter extends RecyclerView.Adapter<ReviewRVAdapter.MyViewHolder> {

    Context context;
    ArrayList<Review> reviews;
    String restaurantId;

    public ReviewRVAdapter(Context context, ArrayList<Review> reviews, String restaurantId) {
        this.context = context;
        this.reviews = reviews;
        this.restaurantId = restaurantId;
    }

    @NonNull
    @Override
    public ReviewRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review_res, parent, false);
        return new ReviewRVAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewRVAdapter.MyViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.reviewerName.setText(R.string.name);
        holder.reviewContent.setText(review.getDescription());
        holder.reviewTime.setText(R.string._1h);
        holder.reviewRatings.setText(review.getRatings());
        holder.likeButton.setText(String.format(context.getString(R.string.react_count), review.getLikeCount()));
        holder.dislikeButton.setText(String.format(context.getString(R.string.react_count), review.getDislikeCount()));
        holder.replyButton.setText(String.format(context.getString(R.string.react_count), review.getReplyCount()));

        FirebaseFirestore db = FirestoreUtil.getInstance().getFirestore();

        holder.replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replyReview();
            }
        });

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (review.isLiked()) {
                    // If the review is already liked, decrement the like count and change the button color
                    review.setLikeCount(review.getLikeCount() - 1);
                    holder.likeButton.setBackgroundResource(R.drawable.btn_review); // Replace with your default button color
                    review.setLiked(false);
                } else {
                    // If the review is not liked, increment the like count and change the button color
                    review.setLikeCount(review.getLikeCount() + 1);
                    holder.likeButton.setBackgroundResource(R.drawable.btn_react_pressed); // Replace with your liked button color
                    review.setLiked(true);

                    // If the review is disliked, decrement the dislike count and change the dislike button color
                    if (review.isDisliked()) {
                        review.setDislikeCount(review.getDislikeCount() - 1);
                        holder.dislikeButton.setBackgroundResource(R.drawable.btn_review); // Default button state
                        review.setDisliked(false);
                    }
                }
                holder.likeButton.setText(String.format(context.getString(R.string.react_count), review.getLikeCount()));
                holder.dislikeButton.setText(String.format(context.getString(R.string.react_count), review.getDislikeCount()));

                // Update Firestore
                db.collection("restaurants").document(restaurantId).collection("reviews").document(review.getId())
                        .update("likeCount", review.getLikeCount(), "dislikeCount", review.getDislikeCount());
            }
        });

        holder.dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (review.isDisliked()) {
                    // If the review is already disliked, decrement the dislike count and change the button color
                    review.setDislikeCount(review.getDislikeCount() - 1);
                    holder.dislikeButton.setBackgroundResource(R.drawable.btn_review); // Default button state
                    review.setDisliked(false);
                } else {
                    // If the review is not disliked, increment the dislike count and change the button color
                    review.setDislikeCount(review.getDislikeCount() + 1);
                    holder.dislikeButton.setBackgroundResource(R.drawable.btn_react_pressed); // Disliked button state
                    review.setDisliked(true);

                    // If the review is liked, decrement the like count and change the like button color
                    if (review.isLiked()) {
                        review.setLikeCount(review.getLikeCount() - 1);
                        holder.likeButton.setBackgroundResource(R.drawable.btn_review); // Default button state
                        review.setLiked(false);
                    }
                }
                holder.likeButton.setText(String.format(context.getString(R.string.react_count), review.getLikeCount()));
                holder.dislikeButton.setText(String.format(context.getString(R.string.react_count), review.getDislikeCount()));

                // Update Firestore
                db.collection("restaurants").document(restaurantId).collection("reviews").document(review.getId())
                        .update("likeCount", review.getLikeCount(), "dislikeCount", review.getDislikeCount());
            }
        });

        // TODO: fix for each user like and dislike
    }

    private void replyReview() {
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
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dialog_bg));

        Button btnCancel = dialog.findViewById(R.id.btn_cancel_reply);
        Button btnSubmit = dialog.findViewById(R.id.btn_submit_reply);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog when the cancel button is clicked
                dialog.dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Handle the submit action
                // For example, you can get the text from the EditText and use it to reply to the review

                // Dismiss the dialog when the submit action is done
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView reviewerName, reviewContent, reviewTime, reviewRatings;
        Button likeButton, dislikeButton, replyButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            reviewerName = itemView.findViewById(R.id.reviewer_name);
            reviewContent = itemView.findViewById(R.id.review_content);
            reviewTime = itemView.findViewById(R.id.review_time);
            reviewRatings = itemView.findViewById(R.id.review_ratings);
            likeButton = itemView.findViewById(R.id.like_btn);
            dislikeButton = itemView.findViewById(R.id.dislike_btn);
            replyButton = itemView.findViewById(R.id.reply_btn);
            // TODO: add image view for reviewer profile picture

        }
    }
}
