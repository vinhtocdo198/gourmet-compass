package com.example.gourmetcompass.ui_personal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.MainActivity;
import com.example.gourmetcompass.R;
import com.example.gourmetcompass.adapters.MyCollectionDetailRVAdapter;
import com.example.gourmetcompass.utils.FirestoreUtil;
import com.example.gourmetcompass.models.Dish;
import com.example.gourmetcompass.models.Restaurant;
import com.example.gourmetcompass.utils.BottomSheetUtil;
import com.example.gourmetcompass.utils.EditTextUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
    ImageButton backBtn, moreBtn;
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

        backBtn.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.stay_still, R.anim.slide_out);
        });

        moreBtn.setOnClickListener(v -> openBottomSheet());

        // Navigate to home screen if collection is empty
        addBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MyCollectionDetailActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void openBottomSheet() {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(MyCollectionDetailActivity.this, R.style.BottomSheetTheme);
        View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_my_coll_detail, findViewById(R.id.btms_my_coll_detail_container));
        bottomSheet.setContentView(sheetView);
        BottomSheetUtil.openBottomSheet(bottomSheet);

        // Init views
        Button renameBtn = sheetView.findViewById(R.id.btn_rename_btms_my_coll_detail);
        Button deleteBtn = sheetView.findViewById(R.id.btn_delete_btms_my_coll_detail);

        renameBtn.setOnClickListener(v -> openRenameSheet(bottomSheet));

        deleteBtn.setOnClickListener(v -> {
            deleteCollection();
            bottomSheet.dismiss();
            finish();
            overridePendingTransition(R.anim.stay_still, R.anim.slide_out);
            Toast.makeText(MyCollectionDetailActivity.this, "Collection deleted", Toast.LENGTH_SHORT).show();
        });
    }

    private void openRenameSheet(BottomSheetDialog bottomSheet) {
        BottomSheetDialog renameSheet = new BottomSheetDialog(MyCollectionDetailActivity.this, R.style.BottomSheetTheme);
        View renameSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_rename_coll, findViewById(R.id.btms_rename_container));
        renameSheet.setContentView(renameSheetView);
        BottomSheetUtil.openBottomSheet(renameSheet);

        EditTextUtil nameTextField = renameSheetView.findViewById(R.id.btms_rename_text_field);
        nameTextField.setHeight(150);
        nameTextField.setHint("Enter collection name");
        nameTextField.setText(collName.getText().toString());

        Button doneBtn = renameSheetView.findViewById(R.id.btms_rename_btn_done);

        doneBtn.setOnClickListener(v -> {
            String newName = nameTextField.getText();
            if (newName.isEmpty()) {
                Toast.makeText(MyCollectionDetailActivity.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            db.collection("users")
                    .document(user.getUid())
                    .collection("collections")
                    .document(collectionId)
                    .update("name", newName)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            collName.setText(newName);
                            renameSheet.dismiss();
                            bottomSheet.dismiss();
                            Toast.makeText(MyCollectionDetailActivity.this, "Collection updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Error renaming collection", task.getException());
                        }
                    });
        });
    }

    private void deleteCollection() {
        db.collection("users")
                .document(user.getUid())
                .collection("collections")
                .document(collectionId)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Collection deleted");
                    } else {
                        Log.e(TAG, "Error deleting collection", task.getException());
                    }
                });
    }

    private void initViews() {
        myCollDetailLayout = findViewById(R.id.my_coll_detail_layout);
        myCollDetailEmptyLayout = findViewById(R.id.my_coll_detail_empty_layout);
        addBtn = findViewById(R.id.my_coll_detail_btn_add);
        collName = findViewById(R.id.my_coll_detail_coll_title);
        backBtn = findViewById(R.id.my_coll_detail_btn_back);
        moreBtn = findViewById(R.id.my_coll_detail_btn_more);
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
        if (type.equals("restaurants")) {
            db.collection(type)
                    .document(itemId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Restaurant restaurant = document.toObject(Restaurant.class);
                                if (restaurant != null) {
                                    restaurant.setId(document.getId());
                                }
                                itemList.add(restaurant);
                                adapter = new MyCollectionDetailRVAdapter(MyCollectionDetailActivity.this, itemList, collectionId);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            }
                        } else {
                            Log.e(TAG, "Error getting item from collection", task.getException());
                        }
                    });
        } else {
            // Store reference to restaurant in collection
            db.collection("dishResReferences")
                    .document(itemId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String ofRestaurantId = task.getResult().getString("restaurantId");

                            if (ofRestaurantId != null) {
                                db.collection("restaurants")
                                        .document(ofRestaurantId)
                                        .collection("dishes")
                                        .document(itemId)
                                        .get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful() && task1.getResult().exists()) {
                                                DocumentSnapshot restaurantDoc = task1.getResult();
                                                Dish dish = restaurantDoc.toObject(Dish.class);
                                                if (dish != null) {
                                                    dish.setId(restaurantDoc.getId());
                                                    dish.setRestaurantId(ofRestaurantId);
                                                }
                                                itemList.add(dish);
                                                adapter = new MyCollectionDetailRVAdapter(MyCollectionDetailActivity.this, itemList, collectionId);
                                                recyclerView.setAdapter(adapter);
                                                adapter.notifyDataSetChanged();
                                                progressBar.setVisibility(View.GONE);

                                            } else {
                                                Log.e(TAG, "Error getting restaurant from reference", task1.getException());
                                            }
                                        });
                            }

                        } else {
                            Log.e(TAG, "Error getting item from collection", task.getException());
                        }
                    });
        }
    }

}