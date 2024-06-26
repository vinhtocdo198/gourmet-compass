package com.example.gourmetcompass.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.gourmetcompass.R;
import com.example.gourmetcompass.models.Reply;
import com.example.gourmetcompass.utils.BottomSheetUtil;
import com.example.gourmetcompass.utils.FirestoreUtil;
import com.example.gourmetcompass.utils.StorageUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ReplyRVAdapter extends RecyclerView.Adapter<ReplyRVAdapter.MyViewHolder> {

    private static final String TAG = "ReplyRVAdapter";
    Context context;
    ArrayList<Reply> replies;
    String restaurantId, reviewId, reviewerName;
    FirebaseUser user;
    FirebaseFirestore db;
    StorageReference storageRef;

    public ReplyRVAdapter(Context context, ArrayList<Reply> replies, String restaurantId, String reviewId, String reviewerName) {
        this.context = context;
        this.replies = replies;
        this.restaurantId = restaurantId;
        this.reviewId = reviewId;
        this.reviewerName = reviewerName;
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
        updateReplierInfo(reply);
        setUserAvatar(holder, reply);

        // Open edit reply bottom sheet
        holder.itemView.setOnLongClickListener(v -> {
            openEditReplyBottomSheet(holder, reply);
            return false;
        });
    }

    private void setUserAvatar(@NonNull MyViewHolder holder, Reply reply) {
        Glide.with(context)
                .load(reply.getReplierAvaUrl())
                .placeholder(R.drawable.ic_default_avatar)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.replierAvaImg);
    }

    private void updateReplierInfo(Reply reply) {
        db.collection("users")
                .document(reply.getReplierId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String replierAvaUrl = document.getString("avaUrl");
                            String username = document.getString("username");
                            db.collection("restaurants")
                                    .document(restaurantId)
                                    .collection("reviews")
                                    .document(reviewId)
                                    .collection("replies")
                                    .document(reply.getId())
                                    .update("replierName", username, "replierAvaUrl", replierAvaUrl);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "Get failed with ", task.getException());
                    }
                });
    }

    private void openEditReplyBottomSheet(@NonNull MyViewHolder holder, Reply reply) {
        if (user.getUid().equals(reply.getReplierId())) {
            BottomSheetDialog editReplySheet = new BottomSheetDialog(context, R.style.BottomSheetTheme);
            View sheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_edit_reply, holder.itemView.findViewById(R.id.btms_edit_reply_container));
            editReplySheet.setContentView(sheetView);
            BottomSheetUtil.openBottomSheet(editReplySheet);

            Button editBtn = sheetView.findViewById(R.id.btn_edit_btms_edit_reply);
            Button deleteBtn = sheetView.findViewById(R.id.btn_delete_btms_edit_reply);

            editBtn.setOnClickListener(v -> openEditReplyDialog(reply, editReplySheet));

            deleteBtn.setOnClickListener(v -> deleteReply(reply, editReplySheet));
        }
    }

    private void deleteReply(Reply reply, BottomSheetDialog editReplySheet) {
        db.collection("restaurants")
                .document(restaurantId)
                .collection("reviews")
                .document(reviewId)
                .collection("replies")
                .document(reply.getId())
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Reply deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to delete reply", Toast.LENGTH_SHORT).show();
                    }
                });

        db.collection("restaurants")
                .document(restaurantId)
                .collection("reviews")
                .document(reviewId)
                .update("replyCount", FieldValue.increment(-1));

        editReplySheet.dismiss();
    }

    private void openEditReplyDialog(Reply reply, BottomSheetDialog editReplySheet) {
        Dialog replyDialog = new Dialog(context);
        replyDialog.setContentView(R.layout.dialog_reply_review);

        // Set the dialog width and height
        Window window = replyDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            layoutParams.width = (int) (metrics.widthPixels * 0.9); // 90% of screen width
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        }
        replyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Init views
        TextView replyDialogTitle = replyDialog.findViewById(R.id.reply_review_dialog_title);
        Button cancelBtn = replyDialog.findViewById(R.id.btn_cancel_reply);
        Button submitBtn = replyDialog.findViewById(R.id.btn_submit_reply);
        EditText replyTextField = replyDialog.findViewById(R.id.reply_review_dialog_content);
        replyDialogTitle.setText(String.format(context.getString(R.string.replying_to) + reviewerName));

        // Sett the current reply data
        replyTextField.setText(reply.getDescription());

        cancelBtn.setOnClickListener(v1 -> replyDialog.dismiss());

        submitBtn.setOnClickListener(v2 -> {
            String replyContent = replyTextField.getText().toString();
            if (replyContent.isEmpty()) {
                Toast.makeText(context, "Reply cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            submitEditReply(replyContent, reply.getId());
            replyDialog.dismiss();
        });

        replyDialog.show();
        editReplySheet.dismiss();
    }

    private void submitEditReply(String replyContent, String replyId) {

        Map<String, Object> updatedReply = new HashMap<>();
        updatedReply.put("description", replyContent);

        db.collection("restaurants")
                .document(restaurantId)
                .collection("reviews")
                .document(reviewId)
                .collection("replies")
                .document(replyId)
                .update(updatedReply)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Reply updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to update reply", Toast.LENGTH_SHORT).show();
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
