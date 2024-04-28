package com.example.gourmetcompass.firebase;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreUtil {
    private static final FirestoreUtil instance = new FirestoreUtil();
    private final FirebaseFirestore firestore;

    private FirestoreUtil() {
        firestore = FirebaseFirestore.getInstance();
    }

    public static FirestoreUtil getInstance() {
        return instance;
    }

    public FirebaseFirestore getFirestore() {
        return firestore;
    }
}
