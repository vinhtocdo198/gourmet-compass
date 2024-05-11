package com.example.gourmetcompass.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

import com.example.gourmetcompass.R;

public class EditTextUtil extends RelativeLayout {
    private final EditText textField;
    private final ImageView iconEnd;
    private String inputType;
    private boolean isPasswordVisible;

    public EditTextUtil(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.util_text_field, this, true);
        textField = findViewById(R.id.edit_text_field_text);
        iconEnd = findViewById(R.id.edit_text_field_icon);

        textField.setOnFocusChangeListener((v, hasFocus) -> {
            if ("password".equals(inputType)) {
                if (isPasswordVisible) {
                    iconEnd.setImageResource(R.drawable.ic_eye_off);
                } else {
                    iconEnd.setImageResource(R.drawable.ic_eye_on);
                }
                iconEnd.setVisibility(View.VISIBLE);
            } else {
                iconEnd.setImageResource(R.drawable.ic_clear_text);
                iconEnd.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
            }
        });

        iconEnd.setOnClickListener(v -> {
            if ("password".equals(inputType)) {
                int cursorPosition = textField.getSelectionStart();
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    textField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    iconEnd.setImageResource(R.drawable.ic_eye_off);
                } else {
                    textField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    iconEnd.setImageResource(R.drawable.ic_eye_on);
                }
                textField.setTextAppearance(R.style.TextFont);
                textField.setSelection(cursorPosition);
            } else {
                textField.setText("");
            }
        });
    }

    public EditText getTextField() {
        return textField;
    }

    public void setText(String text) {
        textField.setText(text);
    }

    public String getText() {
        return textField.getText().toString();
    }

    public void setHint(String hint) {
        textField.setHint(hint);
    }

    public void setEnabled(boolean enabled) {
        textField.setEnabled(enabled);
        if (!enabled) {
            textField.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.input_text_field_disabled));
        } else {
            textField.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.input_text_field));
        }
    }

    public void setInputType(String type) {
        inputType = type;
        switch (type) {
            case "number":
                textField.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case "password":
                textField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                isPasswordVisible = false;
                iconEnd.setImageResource(R.drawable.ic_eye_on);
                textField.setTextAppearance(R.style.TextFont);
                break;
            default:
                textField.setInputType(InputType.TYPE_CLASS_TEXT);
        }
    }

    public void setHeight(int height) {
        textField.setHeight(height);
    }

    public void setIconEnd(int icon) {
        iconEnd.setImageResource(icon);
    }

    public ImageView getIconEnd() {
        return iconEnd;
    }

    public void setSearchBarBackground() {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.border_search_bar);
        setHeight(140);
        textField.setBackground(drawable);
        textField.setPadding(60, 0, 60, 0);
    }
}