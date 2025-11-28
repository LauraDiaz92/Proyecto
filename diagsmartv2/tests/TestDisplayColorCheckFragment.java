package com.example.diagsmartv2.tests;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.diagsmartv2.R;

public class TestDisplayColorCheckFragment extends Fragment {

    private Button btDisplayYes, btDisplayNo;
    private SharedPreferences.Editor editor;

    public TestDisplayColorCheckFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_display_color_check, container, false);

        initReferences(view);

        SharedPreferences prefs = requireContext().getSharedPreferences("test_status", Context.MODE_PRIVATE);
        editor = prefs.edit();

        btDisplayYes.setOnClickListener(v -> {
            editor.putString("display_test_status", "approved");
            editor.apply();
            openTestsFragment();
        });

        btDisplayNo.setOnClickListener(v -> {
            editor.putString("display_test_status", "failed");
            editor.apply();
            openTestsFragment();
        });

        return view;
    }

    private void initReferences(View view) {
        btDisplayYes = view.findViewById(R.id.btDisplayYes);
        btDisplayNo = view.findViewById(R.id.btDisplayNo);
    }


    private void openTestsFragment() {
        Fragment fragment = new TestsFragment();
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
