package com.example.diagsmartv2.tests;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.diagsmartv2.R;

public class TestAutomaticFragment extends Fragment {

    RecyclerView rvTestsAutomatic;
    TextView tvSuggestionsTests;
    TestsAutomaticAdapter adapter;

    private boolean anyTestFailed;

    public TestAutomaticFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_automatic, container, false);

        initReferences(view);
        configureRecyclerView();

        return view;
    }

    private void initReferences(View view) {
        tvSuggestionsTests = view.findViewById(R.id.tvSuggestionsTests);
        rvTestsAutomatic = view.findViewById(R.id.rvTestsAutomatic);
    }

    private void configureRecyclerView() {
        adapter = new TestsAutomaticAdapter(requireContext(), this::updateSuggestionsText);
        rvTestsAutomatic.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTestsAutomatic.setAdapter(adapter);
    }

    @SuppressLint("SetTextI18n")
    private void updateSuggestionsText(boolean anyFailed) {
        anyTestFailed |= anyFailed; // Accumulate failures across all items
        if (anyTestFailed) {
            tvSuggestionsTests.setText("Suggestions are available");
        } else {
            tvSuggestionsTests.setText("All good. No suggestions available");
        }
    }

}