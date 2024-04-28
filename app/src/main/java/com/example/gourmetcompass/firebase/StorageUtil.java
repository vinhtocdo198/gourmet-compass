package com.example.gourmetcompass.firebase;

import com.google.firebase.storage.FirebaseStorage;

public class StorageUtil {
    private static final StorageUtil instance = new StorageUtil();
    private final FirebaseStorage storage;

    private StorageUtil() {
        storage = FirebaseStorage.getInstance();
    }

    public static StorageUtil getInstance() {
        return instance;
    }

    public FirebaseStorage getStorage() {
        return storage;
    }
}
