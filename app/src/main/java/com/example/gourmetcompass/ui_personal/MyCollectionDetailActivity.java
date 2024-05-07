package com.example.gourmetcompass.ui_personal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.MainActivity;
import com.example.gourmetcompass.R;
import com.example.gourmetcompass.adapters.MyCollectionDetailRVAdapter;
import com.example.gourmetcompass.firebase.FirestoreUtil;
import com.example.gourmetcompass.models.Dish;
import com.example.gourmetcompass.models.Restaurant;
import com.example.gourmetcompass.ui_general.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MyCollectionDetailActivity extends AppCompatActivity {

    private final String TAG = "MyCollectionDetailActivity";
    FirebaseFirestore db;
    FirebaseUser user;
    String collectionId, collectionType;
    ImageButton backBtn, searchBtn;
    Button addBtn;
    TextView itemName, collName;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    MyCollectionDetailRVAdapter adapter;
    ArrayList<Object> itemList;
    ArrayList<String> itemIds;
    RelativeLayout myCollDetailLayout;
    LinearLayout myCollDetailEmptyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection_detail);

        // Init firebase services
        db = FirestoreUtil.getInstance().getFirestore();
        user = FirebaseAuth.getInstance().getCurrentUser();

        collectionId = getIntent().getStringExtra("collectionId");
        collectionType = getIntent().getStringExtra("collectionType");

        // Init views
        initViews();
        collName.setText(getIntent().getStringExtra("collectionName"));

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.stay_still, R.anim.slide_out);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Navigate to home screen if collection is empty
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyCollectionDetailActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initViews() {
        myCollDetailLayout = findViewById(R.id.my_coll_detail_layout);
        myCollDetailEmptyLayout = findViewById(R.id.my_coll_detail_empty_layout);
        addBtn = findViewById(R.id.my_coll_detail_btn_add);
        collName = findViewById(R.id.my_coll_detail_coll_title);
        backBtn = findViewById(R.id.my_coll_detail_btn_back);
        searchBtn = findViewById(R.id.my_coll_detail_btn_search);
        itemName = findViewById(R.id.my_coll_detail_title);
        progressBar = findViewById(R.id.my_coll_detail_progress_bar);
        recyclerView = findViewById(R.id.my_coll_detail_recyclerview);
        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyCollectionDetailActivity.this));
        itemList = new ArrayList<>();
        if (collectionType.equals("restaurant")) {
            fetchCollectionList("restaurants");
        } else {
            fetchCollectionList("dishes");
        }
    }

    @SuppressWarnings("unchecked")
    private void fetchCollectionList(String type) {

        progressBar.setVisibility(View.VISIBLE);

        db.collection("users")
                .document(user.getUid())
                .collection("collections")
                .document(collectionId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            itemIds = (ArrayList<String>) document.get(type);
                            if (itemIds != null) {
                                for (String id : itemIds) {
                                    fetchItems(id, type);
                                }
                                setLayout(itemIds.size());
                            }
                        }
                    } else {
                        Log.e(TAG, "Error getting collection list", task.getException());
                    }
                });
    }

    public void setLayout(int size) {
        if (size == 0) {
            myCollDetailLayout.setVisibility(View.GONE);
            myCollDetailEmptyLayout.setVisibility(View.VISIBLE);
        } else {
            myCollDetailLayout.setVisibility(View.VISIBLE);
            myCollDetailEmptyLayout.setVisibility(View.GONE);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchItems(String itemId, String type) {
        db.collection(type)
                .document(itemId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            if (type.equals("restaurants")) {
                                Restaurant restaurant = document.toObject(Restaurant.class);
                                restaurant.setId(document.getId());
                                itemList.add(restaurant);
                            } else {
                                Dish dish = document.toObject(Dish.class);
                                dish.setId(document.getId());
                                itemList.add(dish);
                            }
                            adapter = new MyCollectionDetailRVAdapter(MyCollectionDetailActivity.this, itemList, collectionId);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        }
                    } else {
                        Log.e(TAG, "Error getting item from collection", task.getException());
                    }
                });
//                .addSnapshotListener((value, e) -> {
//                    if (e != null) {
//                        Log.w(TAG, "Listen failed.", e);
//                        return;
//                    }
//
//                    if (value != null && value.exists()) {
//                        if (type.equals("restaurants")) {
//                            Restaurant restaurant = value.toObject(Restaurant.class);
//                            restaurant.setId(value.getId());
//                            itemList.add(restaurant);
//                        } else {
//                            Dish dish = value.toObject(Dish.class);
//                            dish.setId(value.getId());
//                            itemList.add(dish);
//                        }
//                        adapter = new MyCollectionDetailRVAdapter(MyCollectionDetailActivity.this, itemList, collectionId);
//                        recyclerView.setAdapter(adapter);
//                        adapter.notifyDataSetChanged();
//                        progressBar.setVisibility(View.GONE);
//                    } else {
//                        Log.d(TAG, "Current data: null");
//                    }
//                });
    }

}