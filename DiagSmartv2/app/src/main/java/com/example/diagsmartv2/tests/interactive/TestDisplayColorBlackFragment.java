package com.example.diagsmartv2.tests.interactive;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.diagsmartv2.R;

public class TestDisplayColorBlackFragment extends Fragment {

    public TestDisplayColorBlackFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_display_color_black, container, false);

        view.setOnClickListener(v -> launchWhite());

        return view;
    }

    /**
     * Navigates to the white color display test by replacing the current fragment
     * with TestDisplayColorWhiteFragment.
     */
    private void launchWhite() {
        Fragment fragment = new TestDisplayColorWhiteFragment();
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
