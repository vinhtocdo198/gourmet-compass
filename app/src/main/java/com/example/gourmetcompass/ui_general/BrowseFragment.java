package com.example.gourmetcompass.ui_general;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.gourmetcompass.R;

public class BrowseFragment extends Fragment {

    EditText searchBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browse, container, false);

        searchBar = view.findViewById(R.id.search_bar_browse);
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH
                        || (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                    // The user has clicked "Enter", start the new activity
                    Intent intent = new Intent(getActivity(), RestaurantSearchListActivity.class);
                    intent.putExtra("searchQuery", v.getText().toString());
                    startActivity(intent);
                    if (getActivity() != null) {
                        getActivity().overridePendingTransition(R.anim.slide_in, R.anim.stay_still);
                    }
                    return true; // Consume the event
                }
                return false; // Pass the event along
            }
        });

        return view;
    }
}