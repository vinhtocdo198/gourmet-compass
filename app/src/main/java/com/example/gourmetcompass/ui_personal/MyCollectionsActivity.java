package com.example.gourmetcompass.ui_personal;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gourmetcompass.R;
import com.example.gourmetcompass.adapters.MyCollectionsRVAdapter;
import com.example.gourmetcompass.firebase.FirestoreUtil;
import com.example.gourmetcompass.models.MyCollection;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyCollectionsActivity extends AppCompatActivity {

    private static final String TAG = "MyCollectionsActivity";
    FirebaseFirestore db;
    FirebaseUser user;
    ImageButton backBtn, searchBtn, addBtn;
    ScrollView myCollLayout;
    LinearLayout myCollEmptyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collections);

        // Init firebase services
        db = FirestoreUtil.getInstance().getFirestore();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Init views
        initViews();

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
                // Search
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheet();
            }
        });

    }

    private void initViews() {
        backBtn = findViewById(R.id.btn_back_my_collections);
        searchBtn = findViewById(R.id.btn_search_my_collections);
        addBtn = findViewById(R.id.btn_add_my_collections);
        myCollLayout = findViewById(R.id.my_coll_layout);
        myCollEmptyLayout = findViewById(R.id.my_coll_empty_layout);
        initRecyclerView(findViewById(R.id.my_coll_res_recyclerview), "restaurant");
        initRecyclerView(findViewById(R.id.my_coll_dish_recyclerview), "dish");
    }

    private void openBottomSheet() {
        // Open bottom sheet add collection
        BottomSheetDialog bottomSheet = new BottomSheetDialog(MyCollectionsActivity.this, R.style.BottomSheetTheme);
        View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_my_colls, findViewById(R.id.btms_res_container));
        bottomSheet.setContentView(sheetView);
        bottomSheet.show();

        // Init views
        Button doneBtn = sheetView.findViewById(R.id.btn_done_my_colls);
        RadioGroup radioGroup = sheetView.findViewById(R.id.radio_group_my_colls);
        EditText nameTextField = sheetView.findViewById(R.id.btms_coll_name_my_colls);

        setDefaultCollName(nameTextField);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkedId = radioGroup.getCheckedRadioButtonId();

                if (checkedId == R.id.radio_restaurant) {
                    createNewCollection(nameTextField, "restaurant");
                    bottomSheet.dismiss();
                    Toast.makeText(MyCollectionsActivity.this, "Collection created", Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.radio_dish) {
                    createNewCollection(nameTextField, "dish");
                    bottomSheet.dismiss();
                    Toast.makeText(MyCollectionsActivity.this, "Collection created", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MyCollectionsActivity.this, "Please choose a type", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createNewCollection(EditText nameTextField, String type) {
        String collName = nameTextField.getText().toString();

        // Create a new collection with type restaurant
        Map<String, Object> newColl = new HashMap<>();
        newColl.put("type", type);
        newColl.put("name", collName);
        if (type.equals("restaurant")) {
            newColl.put(type + "s", new ArrayList<>());
        } else {
            newColl.put(type + "es", new ArrayList<>());
        }

        // Add to firestore
        db.collection("users").document(user.getUid())
                .collection("collections")
                .add(newColl)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Collection created");
                    } else {
                        Log.e(TAG, "Error creating collection", task.getException());
                    }
                });
    }

    private void setDefaultCollName(EditText nameTextField) {
        db.collection("users").document(user.getUid())
                .collection("collections")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        nameTextField.setText(getString(R.string.my_collection, task.getResult().size() + 1));
                    }
                });
    }

    private void initRecyclerView(RecyclerView recyclerView, String type) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyCollectionsActivity.this));

        ArrayList<MyCollection> list = new ArrayList<>();
        MyCollectionsRVAdapter adapter = new MyCollectionsRVAdapter(MyCollectionsActivity.this, list);
        recyclerView.setAdapter(adapter);

        fetchCollectionList(list, adapter, type);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchCollectionList(ArrayList<MyCollection> list, MyCollectionsRVAdapter adapter, String type) {
        db.collection("users")
                .document(user.getUid())
                .collection("collections")
                .whereEqualTo("type", type)
                .orderBy("name")
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error fetching collections", e);
                        return;
                    }
                    list.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            MyCollection collection = doc.toObject(MyCollection.class);
                            collection.setId(doc.getId());
                            list.add(collection);
                        }
                    }
                    setLayout();
                    adapter.notifyDataSetChanged();
                });
    }

    // Show empty icon if there are no collections
    private void setLayout() {
        db.collection("users")
                .document(user.getUid())
                .collection("collections")
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error fetching collections", e);
                        return;
                    }
                    if (value != null && value.isEmpty()) {
                        myCollEmptyLayout.setVisibility(View.VISIBLE);
                        myCollLayout.setVisibility(View.GONE);
                    } else {
                        myCollEmptyLayout.setVisibility(View.GONE);
                        myCollLayout.setVisibility(View.VISIBLE);
                    }
                });
    }
}
