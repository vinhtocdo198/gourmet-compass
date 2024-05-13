package com.example.gourmetcompass.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

public class ButtonUtil extends AppCompatButton {

    public ButtonUtil(Context context) {
        super(context);
    }

    public ButtonUtil(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ButtonUtil(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setOnClickListener(final OnClickListener l) {
        super.setOnClickListener(v -> {
            // Disable the button
            setEnabled(false);

            // Call the original OnClickListener
            if (l != null) {
                l.onClick(v);
            }

            // Enable the button after 1 second
            new Handler(Looper.getMainLooper()).postDelayed(() -> setEnabled(true), 500);
        });
    }
}
