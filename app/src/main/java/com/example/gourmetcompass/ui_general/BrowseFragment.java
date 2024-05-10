package com.example.gourmetcompass.ui_general;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.MainActivity;
import com.example.gourmetcompass.R;
import com.example.gourmetcompass.adapters.CategoryRVAdapter;
import com.example.gourmetcompass.firebase.FirestoreUtil;
import com.example.gourmetcompass.models.RestaurantCategory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class BrowseFragment extends Fragment {

    private static final String TAG = "BrowseFragment";
    EditText searchBar;
    RecyclerView recyclerView;
    CategoryRVAdapter adapter;
    ArrayList<RestaurantCategory> categoryList;
    FirebaseFirestore db;
    FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browse, container, false);

        // Init firebase services
        db = FirestoreUtil.getInstance().getFirestore();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Init views
        initViews(view);

        // Fetch category images
        fetchCategoryImages();

        searchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO: hide icon
                if (hasFocus) {
                    if (user != null) {
                        Log.d(TAG, "onFocusChange: " + "User is logged in");
                    } else {
                        if (getContext() instanceof MainActivity) {
                            ((MainActivity) getContext()).selectBottomNavItem(R.id.account_fragment);
                        }
                        Toast.makeText(getContext(), "Log in to see our restaurants", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return showSearchResults(v, actionId, event);
            }
        });

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchCategoryImages() {
        db.collection("categories")
                .orderBy("name")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            RestaurantCategory category = document.toObject(RestaurantCategory.class);
                            category.setId(document.getId());
                            categoryList.add(category);
                        }
                        adapter = new CategoryRVAdapter(getActivity(), categoryList);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void initViews(View view) {
        searchBar = view.findViewById(R.id.search_bar_browse);
        recyclerView = view.findViewById(R.id.res_categories_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        categoryList = new ArrayList<>();
    }

    private boolean showSearchResults(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH
                || (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

            // Show search results
            Intent intent = new Intent(getActivity(), SearchResultActivity.class);
            intent.putExtra("searchQuery", v.getText().toString());
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.stay_still);
            }
            return true;
        }
        return false;
    }
}