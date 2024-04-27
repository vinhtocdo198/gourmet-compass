package com.example.gourmetcompass.ui_restaurant_detail;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.adapters.MenuRVAdapter;
import com.example.gourmetcompass.database.FirestoreUtil;
import com.example.gourmetcompass.models.Dish;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class RestaurantMenuFragment extends Fragment {

    private static final String TAG = "RestaurantMenuFragment";
    FirebaseFirestore db;
    String restaurantId;
    ArrayList<Dish> menu;
    RecyclerView recyclerView;

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

        recyclerView = view.findViewById(R.id.menu_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        // Fetch restaurant menu
        fetchRestaurantMenu();

        return view;
    }

    private void fetchRestaurantMenu() {
        db.collection("restaurants").document(restaurantId).collection("dishes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            menu = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Dish dish = document.toObject(Dish.class);
                                menu.add(dish);
                            }
                            MenuRVAdapter adapter = new MenuRVAdapter(getContext(), menu);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

}