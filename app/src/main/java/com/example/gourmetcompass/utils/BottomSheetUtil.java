package com.example.gourmetcompass.utils;

import android.os.Handler;
import android.os.Looper;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class BottomSheetUtil {

    private static boolean isClickable = true;

    public static void openBottomSheet(BottomSheetDialog bottomSheet) {
        if (!isClickable) {
            return;
        }

        // Prevent double click when opening bottom sheet
        isClickable = false;
        new Handler(Looper.getMainLooper()).postDelayed(() -> isClickable = true, 1000);

        bottomSheet.show();
    }
}