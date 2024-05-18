package com.example.gourmetcompass.views.general;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.MainActivity;
import com.example.gourmetcompass.R;
import com.example.gourmetcompass.adapters.CategoryRVAdapter;
import com.example.gourmetcompass.utils.EditTextUtil;
import com.example.gourmetcompass.utils.FirestoreUtil;
import com.example.gourmetcompass.models.RestaurantCategory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class BrowseFragment extends Fragment {

    private static final String TAG = "BrowseFragment";
    EditTextUtil searchBarUtil;
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

        // Set up search bar, including focus change listener and hide keyboard on touch outside
        setUpSearchBar(container);

        return view;
    }

    private void setUpSearchBar(ViewGroup container) {
        searchBarUtil.setHint("Restaurant, cuisine,...");
        searchBarUtil.setSearchBarBackground();
        searchBarUtil.setIconEnd(R.drawable.ic_search);
        searchBarUtil.getIconEnd().setOnClickListener(v -> searchBarUtil.getTextField().requestFocus());
        searchBarUtil.getTextField().setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (user != null) {
                    searchBarUtil.setIconEnd(R.drawable.ic_clear_text);
                    searchBarUtil.getIconEnd().setOnClickListener(v1 -> searchBarUtil.getTextField().setText(""));
                } else {
                    if (getContext() instanceof MainActivity) {
                        ((MainActivity) getContext()).selectBottomNavItem(R.id.account_fragment);
                    }
                    Toast.makeText(getContext(), "Log in to see our restaurants", Toast.LENGTH_SHORT).show();
                }
            } else {
                searchBarUtil.setIconEnd(R.drawable.ic_search);
                searchBarUtil.getIconEnd().setOnClickListener(v2 -> searchBarUtil.getTextField().requestFocus());
            }
        });
        searchBarUtil.getTextField().setOnEditorActionListener(this::showSearchResults);
        hideKeyboardAndLoseFocus(container);
    }

    private void hideKeyboardAndLoseFocus(ViewGroup container) {
        container.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.performClick();
            }
            searchBarUtil.getTextField().clearFocus();
            Activity activity = ((Activity) getContext());
            if (activity != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(searchBarUtil.getTextField().getWindowToken(), 0);
            }
            return false;
        });
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
        searchBarUtil = view.findViewById(R.id.search_bar_browse);
        recyclerView = view.findViewById(R.id.res_categories_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        categoryList = new ArrayList<>();
    }

    private boolean showSearchResults(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE
                || actionId == EditorInfo.IME_ACTION_SEARCH
                || (event != null
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

            String query = v.getText().toString();
            if (query.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a search query", Toast.LENGTH_SHORT).show();
                return false;
            }

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