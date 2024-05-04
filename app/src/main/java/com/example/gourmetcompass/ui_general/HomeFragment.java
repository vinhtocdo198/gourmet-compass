package com.example.gourmetcompass.ui_general;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.MainActivity;
import com.example.gourmetcompass.R;
import com.example.gourmetcompass.adapters.HomeRVAdapter;
import com.example.gourmetcompass.firebase.FirestoreUtil;
import com.example.gourmetcompass.models.Restaurant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    FirebaseFirestore db;
    ImageButton searchImgBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchImgBtn = view.findViewById(R.id.search_bar_home);

        // Init db instance
        db = FirestoreUtil.getInstance().getFirestore();

        // Fetch data from db into 3 RecyclerViews
        initRecyclerView(view.findViewById(R.id.first_scroll));
        initRecyclerView(view.findViewById(R.id.second_scroll));
        initRecyclerView(view.findViewById(R.id.third_scroll));

        // Navigate to browse fragment
        searchImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrowseFragment browseFragment = new BrowseFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame_layout, browseFragment);
                fragmentTransaction.commit();
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).selectBottomNavItem(R.id.browse_fragment);
                }
            }
        });

    }

    private void initRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        ArrayList<Restaurant> list = new ArrayList<>();
        HomeRVAdapter adapter = new HomeRVAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        fetchRestaurantList(list, adapter);
    }

    private void fetchRestaurantList(ArrayList<Restaurant> list, HomeRVAdapter adapter) {
        db.collection("restaurants")
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