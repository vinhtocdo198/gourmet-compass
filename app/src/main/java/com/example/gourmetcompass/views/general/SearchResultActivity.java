package com.example.gourmetcompass.views.general;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.example.gourmetcompass.R;
import com.example.gourmetcompass.adapters.SearchResultRVAdapter;
import com.example.gourmetcompass.models.Restaurant;
import com.example.gourmetcompass.utils.FirestoreUtil;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchResultActivity extends AppCompatActivity {

    private static final String TAG = "SearchResultActivity";
    String searchQuery;
    TextView searchTextResult, resultQuantity;
    Button highestRatedBtn, mostReviewedBtn;
    ImageButton backBtn;
    ProgressBar progressBar;
    FirebaseFirestore db;
    Client client;
    Index index;
    ArrayList<Restaurant> resResultList;
    RecyclerView recyclerView;
    SearchResultRVAdapter adapter;
    LinearLayout emptyLayout;
    HorizontalScrollView filterScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_search_list);

        // Init firebase services
        db = FirestoreUtil.getInstance().getFirestore();

        // Get search query from intent
        searchQuery = getIntent().getStringExtra("searchQuery");

        // Init views
        initViews();

        backBtn.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.stay_still, R.anim.slide_out);
        });

        // Set search text result and quantity
        searchTextResult.setText(searchQuery);

        // Show search results
        fetchSearchResults(new Query(searchQuery).setHitsPerPage(50));

        // Set up filters
        setupFilterBtn(highestRatedBtn, "highestRated",
                R.drawable.ic_star_filter_on, R.drawable.ic_star_filter_off
        );

        setupFilterBtn(mostReviewedBtn, "mostReviewed",
                R.drawable.ic_edit_filter_on, R.drawable.ic_edit_filter_off
        );
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchSearchResults(Query query) {

        progressBar.setVisibility(View.VISIBLE);

        // Init Algolia client and index
        client = new Client("78N1O9F3YM", "4c4b59f91005ba629915c09271699474");
        index = client.getIndex("restaurants");

        index.searchAsync(query, (JSONObject content, AlgoliaException e) -> {
            if (e != null) {
                Log.e("Algolia", "Error while fetching search results: ", e);
                return;
            }

            // Pass the result id to fetch the restaurant from Firestore
            try {
                if (content != null) {
                    JSONArray hits = content.getJSONArray("hits");

                    // Set the quantity of search results
                    int resultSize = hits.length();
                    if (resultSize == 0) {
                        resultQuantity.setText("");
                        emptyLayout.setVisibility(View.VISIBLE);
                        filterScrollView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    } else if (resultSize == 1) {
                        resultQuantity.setText(R.string._1_result);
                        emptyLayout.setVisibility(View.GONE);
                    } else {
                        resultQuantity.setText(String.format(SearchResultActivity.this.getString(R.string.n_results), resultSize));
                        emptyLayout.setVisibility(View.GONE);
                    }

                    resResultList.clear();
                    for (int i = 0; i < hits.length(); i++) {
                        JSONObject hit = hits.getJSONObject(i);
                        String objectID = hit.getString("objectID");
                        fetchResFromFirestore(objectID);
                    }
                    adapter = new SearchResultRVAdapter(this, resResultList);
                    recyclerView.setAdapter(adapter);
                }
            } catch (JSONException jsonException) {
                Log.e(TAG, "Error parsing JSON results: ", jsonException);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchResFromFirestore(String restaurantId) {
        db.collection("restaurants")
                .document(restaurantId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            Restaurant restaurant = document.toObject(Restaurant.class);
                            if (restaurant != null) {
                                restaurant.setId(document.getId());
                                resResultList.add(restaurant);
                            }
                        } else {
                            Log.d(TAG, "No such document");
                        }
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });
    }

    private void initViews() {
        searchTextResult = findViewById(R.id.search_text_result);
        resultQuantity = findViewById(R.id.result_quantity);
        highestRatedBtn = findViewById(R.id.highest_rated_filter);
        mostReviewedBtn = findViewById(R.id.most_reviewed_filter);
        backBtn = findViewById(R.id.btn_back_res_search_list);
        progressBar = findViewById(R.id.res_search_list_progress_bar);
        emptyLayout = findViewById(R.id.empty_search_layout);
        filterScrollView = findViewById(R.id.filter_scroll_view);
        resResultList = new ArrayList<>();
        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.res_search_list_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }

    private void setupFilterBtn(Button button, String filter, int icFilterOn, int icFilterOff) {
        boolean[] isFilterOn = {false};

        button.setOnClickListener(v -> {
            isFilterOn[0] = !isFilterOn[0];
            if (isFilterOn[0]) {
                button.setBackgroundResource(R.drawable.btn_filter_full);
                button.setTextColor(ContextCompat.getColor(SearchResultActivity.this, R.color.white));
                button.setCompoundDrawablesWithIntrinsicBounds(icFilterOn, 0, 0, 0);
                fetchFilteredResults(filter);
            } else {
                button.setBackgroundResource(R.drawable.btn_filter);
                button.setTextColor(ContextCompat.getColor(SearchResultActivity.this, R.color.content_text));
                button.setCompoundDrawablesWithIntrinsicBounds(icFilterOff, 0, 0, 0);
                fetchSearchResults(new Query(searchQuery).setHitsPerPage(50));
            }
        });
    }

    private void fetchFilteredResults(String filter) {
        ArrayList<Restaurant> filteredList = new ArrayList<>();
        switch (filter) {
            case "highestRated":
                resResultList.sort((r1, r2) -> {
                    float rating1 = r1.getRatings().equals("N/A") ? 0 : Float.parseFloat(r1.getRatings());
                    float rating2 = r2.getRatings().equals("N/A") ? 0 : Float.parseFloat(r2.getRatings());
                    return Float.compare(rating2, rating1);
                });
                filteredList = resResultList;
                break;
            case "mostReviewed":
                resResultList.sort((r1, r2) -> Integer.compare(r2.getRatingCount(), r1.getRatingCount()));
                filteredList = resResultList;
                break;
        }
        adapter = new SearchResultRVAdapter(SearchResultActivity.this, filteredList);
        recyclerView.setAdapter(adapter);
    }

}