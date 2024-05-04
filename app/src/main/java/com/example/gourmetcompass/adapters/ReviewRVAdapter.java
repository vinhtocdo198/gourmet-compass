package com.example.gourmetcompass.adapters;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ReviewRVAdapter extends RecyclerView.Adapter<ReviewRVAdapter.MyViewHolder> {

    private static final String TAG = "ReviewRVAdapter";
    Context context;
    ArrayList<Review> reviews;
    String restaurantId;
    FirebaseUser user;
    FirebaseFirestore db;
    ArrayList<String> likedUserIds, dislikedUserIds;

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

        // Init firebase services
        db = FirestoreUtil.getInstance().getFirestore();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Check if the current user has liked or disliked this review
        db.collection("restaurants").document(restaurantId).
                collection("reviews").document(review.getId()).
                get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                likedUserIds = (ArrayList<String>) document.get("likedUserIds");
                                dislikedUserIds = (ArrayList<String>) document.get("dislikedUserIds");

                                holder.likeButton.setText(String.format(context.getString(R.string.react_count), likedUserIds.size()));
                                holder.dislikeButton.setText(String.format(context.getString(R.string.react_count), dislikedUserIds.size()));

                                if (likedUserIds != null && likedUserIds.contains(user.getUid())) {
                                    // The current user has liked this review
                                    holder.likeButton.setBackgroundResource(R.drawable.btn_react_pressed); // Replace with your liked button color
                                    review.setLiked(true);
                                }

                                if (dislikedUserIds != null && dislikedUserIds.contains(user.getUid())) {
                                    // The current user has disliked this review
                                    holder.dislikeButton.setBackgroundResource(R.drawable.btn_react_pressed); // Replace with your disliked button color
                                    review.setDisliked(true);
                                }
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "Get failed with ", task.getException());
                        }
                    }
                });

        holder.reviewContent.setText(review.getDescription());
        holder.reviewRatings.setText(String.valueOf((int) Float.parseFloat(review.getRatings())));
        holder.replyButton.setText(String.format(context.getString(R.string.react_count), 0));

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
                    likedUserIds.remove(user.getUid());
                    holder.likeButton.setBackgroundResource(R.drawable.btn_review); // Replace with your default button color
                    review.setLiked(false);
                } else {
                    // If the review is not liked, increment the like count and change the button color
                    likedUserIds.add(user.getUid());
                    holder.likeButton.setBackgroundResource(R.drawable.btn_react_pressed); // Replace with your liked button color
                    review.setLiked(true);

                    // If the review is disliked, decrement the dislike count and change the dislike button color
                    if (review.isDisliked()) {
                        dislikedUserIds.remove(user.getUid());
                        holder.dislikeButton.setBackgroundResource(R.drawable.btn_review); // Default button state
                        review.setDisliked(false);
                    }
                }
                holder.likeButton.setText(String.format(context.getString(R.string.react_count), likedUserIds.size()));
                holder.dislikeButton.setText(String.format(context.getString(R.string.react_count), dislikedUserIds.size()));

                // Update Firestore
                db.collection("restaurants").document(restaurantId).collection("reviews").document(review.getId())
                        .update("likedUserIds", likedUserIds, "dislikedUserIds", dislikedUserIds);
            }
        });

        holder.dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (review.isDisliked()) {
                    // If the review is already disliked, decrement the dislike count and change the button color
                    dislikedUserIds.remove(user.getUid());
                    holder.dislikeButton.setBackgroundResource(R.drawable.btn_review); // Default button state
                    review.setDisliked(false);
                } else {
                    // If the review is not disliked, increment the dislike count and change the button color
                    dislikedUserIds.add(user.getUid());
                    holder.dislikeButton.setBackgroundResource(R.drawable.btn_react_pressed); // Disliked button state
                    review.setDisliked(true);

                    // If the review is liked, decrement the like count and change the like button color
                    if (review.isLiked()) {
                        likedUserIds.remove(user.getUid());
                        holder.likeButton.setBackgroundResource(R.drawable.btn_review); // Default button state
                        review.setLiked(false);
                    }
                }
                holder.likeButton.setText(String.format(context.getString(R.string.react_count), likedUserIds.size()));
                holder.dislikeButton.setText(String.format(context.getString(R.string.react_count), dislikedUserIds.size()));

                // Update Firestore
                db.collection("restaurants").document(restaurantId).collection("reviews").document(review.getId())
                        .update("likedUserIds", likedUserIds, "dislikedUserIds", dislikedUserIds);
            }
        });

        // Fetch review data
        getReviewData(holder, review);

    }

    private void getReviewData(@NonNull MyViewHolder holder, Review review) {
        if (user != null) {
            db.collection("restaurants").document(restaurantId)
                    .collection("reviews").document(review.getId())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Get the current username
                                    String reviewerId = document.getString("reviewerId");
                                    getReviewerUsername(reviewerId, holder);

                                    // Calculate the time passed since the review was posted
                                    Long timestamp = document.getLong("timestamp");
                                    if (timestamp != null) {
                                        holder.reviewTime.setText(getTimePassed(timestamp));
                                    } else {
                                        // Handle the case where timestamp is null
                                        holder.reviewTime.setText("N/A");
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
    }

    private void getReviewerUsername(String reviewerId, @NonNull MyViewHolder holder) {
        db.collection("users").document(reviewerId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String username = document.getString("username");
                                holder.reviewerName.setText(username);
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "Get failed with ", task.getException());
                        }
                    }
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
                dialog.dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Handle the submit action
                // For example, you can get the text from the EditText and use it to reply to the review

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
