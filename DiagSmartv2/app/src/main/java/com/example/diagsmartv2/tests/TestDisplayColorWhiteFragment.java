package com.example.diagsmartv2.tests;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.diagsmartv2.R;

public class TestDisplayColorWhiteFragment extends Fragment {

    public TestDisplayColorWhiteFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_display_color_white, container, false);

        view.setOnClickListener(v -> launchRed());

        return view;
    }

    private void launchRed() {
        Fragment fragment = new TestDisplayColorRedFragment();
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
