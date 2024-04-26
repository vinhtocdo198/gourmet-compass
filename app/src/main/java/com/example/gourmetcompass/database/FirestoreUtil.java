package com.example.gourmetcompass.database;

import android.annotation.SuppressLint;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreUtil {
    @SuppressLint("StaticFieldLeak")
    private static FirebaseFirestore instance;

    // Singleton pattern for database
    private FirestoreUtil() {
    }

    public static FirebaseFirestore getInstance() {
        if (instance == null) {
            instance = FirebaseFirestore.getInstance();
        }
        return instance;
    }
}
