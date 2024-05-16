package com.example.gourmetcompass.ui_general;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.adapters.NotiRVAdapter;
import com.example.gourmetcompass.models.Notification;
import com.example.gourmetcompass.utils.FirestoreUtil;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {

    private static final String TAG = "NotificationFragment";
    FirebaseFirestore db;
    FirebaseUser user;
    ArrayList<Notification> notiList;
    RecyclerView recyclerView;
    NotiRVAdapter adapter;
    ImageButton checkBtn;
    LinearLayout emptyLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        // Init firebase services
        db = FirestoreUtil.getInstance().getFirestore();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Init views
        initViews(view);

        // Fetch notifications
        fetchNotifications();

        // Mark all notifications as read
        checkBtn.setOnClickListener(v -> markAllNotiAsRead());

        return view;
    }

    private void markAllNotiAsRead() {
        for (Notification noti : notiList) {
            db.collection("users")
                    .document(user.getUid())
                    .collection("notifications")
                    .document(noti.getId())
                    .update("checked", true);
        }
        Toast.makeText(getContext(), "All notifications are marked as read", Toast.LENGTH_SHORT).show();
    }

    private void initViews(View view) {
        checkBtn = view.findViewById(R.id.btn_check_noti);
        emptyLayout = view.findViewById(R.id.noti_empty_layout);
        recyclerView = view.findViewById(R.id.noti_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        notiList = new ArrayList<>();
    }

    private void showNotiBadge() {
        if (getActivity() != null) {
            BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottomNavigationView);
            BadgeDrawable badge = bottomNav.getOrCreateBadge(R.id.noti_fragment);
            badge.setVisible(true);
        }
    }

    private void hideNotiBadge() {
        if (getActivity() != null) {
            BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottomNavigationView);
            BadgeDrawable badge = bottomNav.getBadge(R.id.noti_fragment);
            if (badge != null) {
                badge.setVisible(false);
                bottomNav.removeBadge(R.id.noti_fragment);
            }
        }
    }

    private void fetchNotifications() {

        db.collection("users")
                .document(user.getUid())
                .collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }
                    notiList.clear();
                    boolean allRead = true;
                    if (value != null) {
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Notification noti = doc.toObject(Notification.class);
                            if (noti != null) {
                                noti.setId(doc.getId());
                                if (!noti.isChecked()) {
                                    allRead = false;
                                }

                                // Delete notifications that are older than 30 days
                                if (System.currentTimeMillis() - noti.getTimestamp() > TimeUnit.DAYS.toMillis(30)) {
                                    deleteOldNoti(noti);
                                } else {
                                    notiList.add(noti);
                                }
                            }
                        }
                    }

                    // Show or hide the notification badge
                    if (allRead) {
                        hideNotiBadge();
                    } else {
                        showNotiBadge();
                    }

                    checkBtn.setEnabled(!allRead);
                    setLayout(notiList.size());
                    adapter = new NotiRVAdapter(getContext(), notiList);
                    recyclerView.setAdapter(adapter);
                });
    }

    private void deleteOldNoti(Notification noti) {
        db.collection("users")
                .document(user.getUid())
                .collection("notifications")
                .document(noti.getId())
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Old notifications deleted");
                    }
                });
    }

    private void setLayout(int size) {
        if (size == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }
    }
}