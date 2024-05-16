package com.example.gourmetcompass.ui_general;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.MainActivity;
import com.example.gourmetcompass.R;
import com.example.gourmetcompass.adapters.HomeRVAdapter;
import com.example.gourmetcompass.models.Restaurant;
import com.example.gourmetcompass.utils.FirestoreUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    FirebaseFirestore db;
    ImageView searchImgBtn;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Init db instance
        db = FirestoreUtil.getInstance().getFirestore();

        searchImgBtn = view.findViewById(R.id.search_bar_home);
        progressBar = view.findViewById(R.id.home_progress_bar);

        // Fetch data from db into 3 RecyclerViews
        initGourmetsChoiceRV(view.findViewById(R.id.first_scroll));
        initTopReviewedRV(view.findViewById(R.id.second_scroll));
        initOlderWiserRV(view.findViewById(R.id.third_scroll));

        // Navigate to browse fragment
        searchImgBtn.setOnClickListener(v -> {
            BrowseFragment browseFragment = new BrowseFragment();
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_frame_layout, browseFragment);
            fragmentTransaction.commit();
            if (getActivity() != null) {
                ((MainActivity) getActivity()).selectBottomNavItem(R.id.browse_fragment);
            }
        });

        return view;
    }

    private void initOlderWiserRV(@NonNull RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        ArrayList<Restaurant> list = new ArrayList<>();
        HomeRVAdapter adapter = new HomeRVAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        getOlderWiserRes(list, adapter);
    }

    private void getOlderWiserRes(ArrayList<Restaurant> list, HomeRVAdapter adapter) {
        db.collection("restaurants")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Restaurant restaurant = document.toObject(Restaurant.class);
                                restaurant.setId(document.getId());
                                list.add(restaurant);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("Firestore error", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void initGourmetsChoiceRV(@NonNull RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        ArrayList<Restaurant> list = new ArrayList<>();
        HomeRVAdapter adapter = new HomeRVAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        getGourmetsChoiceRes(list, adapter);
    }

    private void getGourmetsChoiceRes(ArrayList<Restaurant> list, HomeRVAdapter adapter) {

        progressBar.setVisibility(View.VISIBLE);

        db.collection("restaurants")
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Restaurant restaurant = document.toObject(Restaurant.class);
                                restaurant.setId(document.getId());
                                restaurant.setRatings(restaurant.getRatings().equals("N/A") ? "0" : restaurant.getRatings());
                                list.add(restaurant);
                            }
                            list.sort((r1, r2) -> Float.compare(Float.parseFloat(r2.getRatings()), Float.parseFloat(r1.getRatings())));
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        } else {
                            Log.d("Firestore error", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void initTopReviewedRV(@NonNull RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        ArrayList<Restaurant> list = new ArrayList<>();
        HomeRVAdapter adapter = new HomeRVAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        getTopReviewedRes(list, adapter);
    }

    private void getTopReviewedRes(ArrayList<Restaurant> list, HomeRVAdapter adapter) {
        db.collection("restaurants")
                .orderBy("ratingCount", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Restaurant restaurant = document.toObject(Restaurant.class);
                                restaurant.setId(document.getId());
                                list.add(restaurant);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("Firestore error", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}