package com.example.diagsmartv2.tests;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.diagsmartv2.R;

public class TestDisplayMainFragment extends Fragment {

    public TestDisplayMainFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_display_main, container, false);

        Button btDisplayNextMain = view.findViewById(R.id.btDisplayNextMain);

        btDisplayNextMain.setOnClickListener(v -> launchNextTest());

        return view;
    }

    private void launchNextTest() {
        requireActivity()
                .getSupportFragmentManager()
                .popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new TestDisplayColorWhiteFragment()) // O el fragment que corresponda
                .addToBackStack("colorTest")
                .commit();
    }

}
