package com.example.gourmetcompass.views.restaurant_detail;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.adapters.MenuRVAdapter;
import com.example.gourmetcompass.models.Dish;
import com.example.gourmetcompass.utils.FirestoreUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class RestaurantMenuFragment extends Fragment {

    private static final String TAG = "RestaurantMenuFragment";
    FirebaseFirestore db;
    String restaurantId, searchQuery;
    ArrayList<Dish> menu;
    RecyclerView recyclerView;
    MenuRVAdapter adapter;
    ProgressBar progressBar;

    public static RestaurantMenuFragment newInstance(String restaurantId) {
        RestaurantMenuFragment fragment = new RestaurantMenuFragment();

        Bundle bundle = new Bundle();
        bundle.putString("restaurantId", restaurantId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant_menu, container, false);

        db = FirestoreUtil.getInstance().getFirestore();

        // Get the restaurant object from the arguments
        if (getArguments() != null) {
            restaurantId = getArguments().getString("restaurantId");
        }

        // Init views
        initViews(view);

        // Fetch restaurant menu
        fetchRestaurantMenu();

        return view;
    }

    private void initViews(View view) {
        progressBar = view.findViewById(R.id.menu_progress_bar);
        recyclerView = view.findViewById(R.id.menu_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        menu = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchRestaurantMenu();
    }

    public void updateSearchQuery(String query) {
        this.searchQuery = query;
        if (query == null || query.isEmpty()) {
            fetchRestaurantMenu();
        } else {
            filterRestaurantMenu(query);
        }
    }

    private void filterRestaurantMenu(String query) {
        db.collection("restaurants")
                .document(restaurantId)
                .collection("dishes")
                .orderBy("name", Query.Direction.ASCENDING)
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query + "\uf8ff")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            menu.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Dish dish = document.toObject(Dish.class);
                                dish.setId(document.getId());
                                menu.add(dish);
                            }
                            adapter = new MenuRVAdapter(getContext(), menu, restaurantId);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchRestaurantMenu() {

        progressBar.setVisibility(View.VISIBLE);

        db.collection("restaurants")
                .document(restaurantId)
                .collection("dishes")
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            menu.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Dish dish = document.toObject(Dish.class);
                                dish.setId(document.getId());
                                menu.add(dish);
                            }
                            adapter = new MenuRVAdapter(getContext(), menu, restaurantId);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

}