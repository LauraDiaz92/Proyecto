package com.example.diagsmartv2.tests;

import android.annotation.SuppressLint;
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
import android.widget.TextView;

import com.example.diagsmartv2.R;

public class TestMultitouchFragment extends Fragment {

    private Button btMultiTouchYes, btMultiTouchNo;
    private MultitouchView multitouchView;
    private TextView touchCounter;

    private SharedPreferences.Editor editor;

    public TestMultitouchFragment() {}
    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_multitouch, container, false);

        SharedPreferences prefs = requireContext().getSharedPreferences("test_status", Context.MODE_PRIVATE);
        editor = prefs.edit();

        initReferences(view);
        setListenersToButtons();

        multitouchView.setOnTouchCountChangeListener(count ->
                touchCounter.setText("Touches Detected: " + count));

        return view;
    }

    private void initReferences(View view) {
        btMultiTouchYes = view.findViewById(R.id.btMultitouchYes);
        btMultiTouchNo = view.findViewById(R.id.btMultitouchNo);
        multitouchView = view.findViewById(R.id.multitouch_view);
        touchCounter = view.findViewById(R.id.touch_counter);
    }

    private void setListenersToButtons() {
        btMultiTouchYes.setOnClickListener(v -> setResultAndExit("approved"));
        btMultiTouchNo.setOnClickListener(v -> setResultAndExit("failed"));
    }

    private void setResultAndExit(String status) {
        editor.putString("multitouch_test_status", status);
        editor.apply();
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new TestsFragment())
                .commit();
    }
}
