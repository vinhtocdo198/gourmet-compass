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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ReviewRVAdapter extends RecyclerView.Adapter<ReviewRVAdapter.MyViewHolder> {

    private static final String TAG = "ReviewRVAdapter";
    Context context;
    ArrayList<Review> reviews;
    String restaurantId, reviewerId;
    FirebaseUser user;
    FirebaseFirestore db;

    public ReviewRVAdapter(Context context, ArrayList<Review> reviews, String restaurantId) {
        this.context = context;
        this.reviews = reviews;
        this.restaurantId = restaurantId;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review_res, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Review review = reviews.get(position);
//        likedUserIds = new ArrayList<>(review.getLikedUserIds());
//        dislikedUserIds = new ArrayList<>(review.getDislikedUserIds());
        holder.reviewContent.setText(review.getDescription());
        holder.reviewRatings.setText(String.valueOf((int) Float.parseFloat(review.getRatings())));

        // Init firebase services
        db = FirestoreUtil.getInstance().getFirestore();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Fetch review data
        setReviewData(holder, review);
        setReactButtonsStatus(holder, review);
        setReplyButton(holder, review);

        holder.replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReplyDialog(review.getId());
            }
        });

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeReview(holder, review);
            }
        });

        holder.dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dislikeReview(holder, review);
            }
        });

    }

    private void dislikeReview(@NonNull MyViewHolder holder, Review review) {
        ArrayList<String> likedUserIds = review.getLikedUserIds();
        ArrayList<String> dislikedUserIds = review.getDislikedUserIds();
        if (dislikedUserIds.contains(user.getUid())) {
            dislikedUserIds.remove(user.getUid());
            holder.dislikeButton.setBackgroundResource(R.drawable.btn_review); // Default button state
        } else {
            dislikedUserIds.add(user.getUid());
            holder.dislikeButton.setBackgroundResource(R.drawable.btn_react_pressed); // Disliked button state

            if (likedUserIds.contains(user.getUid())) {
                likedUserIds.remove(user.getUid());
                holder.likeButton.setBackgroundResource(R.drawable.btn_review); // Default button state
            }
        }

        // Update Firestore
        db.collection("restaurants").document(restaurantId)
                .collection("reviews").document(review.getId())
                .update("likedUserIds", likedUserIds, "dislikedUserIds", dislikedUserIds)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            holder.likeButton.setText(String.format(context.getString(R.string.react_count), review.getLikedUserIds().size()));
                            holder.dislikeButton.setText(String.format(context.getString(R.string.react_count), review.getDislikedUserIds().size()));
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                        } else {
                            Log.d(TAG, "Error updating document", task.getException());
                        }
                    }
                });
    }

    private void likeReview(@NonNull MyViewHolder holder, Review review) {
        ArrayList<String> likedUserIds = review.getLikedUserIds();
        ArrayList<String> dislikedUserIds = review.getDislikedUserIds();
        if (likedUserIds.contains(user.getUid())) {
            likedUserIds.remove(user.getUid());
            holder.likeButton.setBackgroundResource(R.drawable.btn_review);
        } else if (!likedUserIds.contains(user.getUid())) {
            likedUserIds.add(user.getUid());
            holder.likeButton.setBackgroundResource(R.drawable.btn_react_pressed);

            if (dislikedUserIds.contains(user.getUid())) {
                dislikedUserIds.remove(user.getUid());
                holder.dislikeButton.setBackgroundResource(R.drawable.btn_review);
            }
        }

        // Update Firestore
        db.collection("restaurants").document(restaurantId)
                .collection("reviews").document(review.getId())
                .update("likedUserIds", likedUserIds, "dislikedUserIds", dislikedUserIds)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            holder.likeButton.setText(String.format(context.getString(R.string.react_count), likedUserIds.size()));
                            holder.dislikeButton.setText(String.format(context.getString(R.string.react_count), dislikedUserIds.size()));
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                        } else {
                            Log.d(TAG, "Error updating document", task.getException());
                        }
                    }
                });
    }

    private void setReactButtonsStatus(@NonNull MyViewHolder holder, Review review) {
        db.collection("restaurants").document(restaurantId).
                collection("reviews").document(review.getId()).
                get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                ArrayList<String> likedUserIds = (ArrayList<String>) document.get("likedUserIds");
                                ArrayList<String> dislikedUserIds = (ArrayList<String>) document.get("dislikedUserIds");

                                holder.likeButton.setText(String.format(context.getString(R.string.react_count), likedUserIds != null ? likedUserIds.size() : 0));
                                holder.dislikeButton.setText(String.format(context.getString(R.string.react_count), dislikedUserIds != null ? dislikedUserIds.size() : 0));

                                if (likedUserIds != null && likedUserIds.contains(user.getUid())) {
                                    holder.likeButton.setBackgroundResource(R.drawable.btn_react_pressed);
                                }

                                if (dislikedUserIds != null && dislikedUserIds.contains(user.getUid())) {
                                    holder.dislikeButton.setBackgroundResource(R.drawable.btn_react_pressed);
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

    private void setReplyButton(@NonNull MyViewHolder holder, Review review) {
        db.collection("restaurants").document(restaurantId)
                .collection("reviews").document(review.getId())
                .collection("replies")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int replyCount = task.getResult().size();
                            holder.replyButton.setText(String.format(context.getString(R.string.react_count), replyCount));
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void setReviewData(@NonNull MyViewHolder holder, Review review) {
        db.collection("restaurants").document(restaurantId)
                .collection("reviews").document(review.getId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Get the current username
                                reviewerId = document.getString("reviewerId");
                                setReviewerUsernameReview(reviewerId, holder);

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

    private void setReviewerUsernameReview(String reviewerId, @NonNull MyViewHolder holder) {
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

    private void openReplyDialog(String reviewId) {
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

        // Init dialog views
        TextView reviewerName = dialog.findViewById(R.id.reply_review_dialog_title);
        EditText replyTextField = dialog.findViewById(R.id.reply_review_dialog_content);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel_reply);
        Button btnSubmit = dialog.findViewById(R.id.btn_submit_reply);
        setReviewerUsernameReplyDialog(reviewerName);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String replyContent = replyTextField.getText().toString();
                if (replyContent.isEmpty()) {
                    Toast.makeText(context, "Reply cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                replyToReview(reviewId, replyContent);

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void replyToReview(String reviewId, String replyContent) {
        // Create a reply object
        Map<String, Object> reply = new HashMap<>();
        reply.put("timestamp", System.currentTimeMillis());
        reply.put("description", replyContent);
        reply.put("replierId", user.getUid());

        // Add the reply to Firestore
        db.collection("restaurants").document(restaurantId)
                .collection("reviews").document(reviewId)
                .collection("replies").add(reply)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Reply submitted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to submit reply", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setReviewerUsernameReplyDialog(TextView reviewerName) {
        db.collection("users").document(reviewerId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String username = document.getString("username");
                                reviewerName.setText(context.getString(R.string.reply_review, username));
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "Get failed with ", task.getException());
                        }
                    }
                });
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
