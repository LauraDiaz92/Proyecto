package com.example.diagsmartv2.tests;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.diagsmartv2.R;

public class TestVolumeDownButtonFragment extends Fragment {

    private TextView tvVolumeDownFeedback;
    private Button btVolumeDownYes, btVolumeDownNo;
    private SharedPreferences.Editor editor;

    public TestVolumeDownButtonFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_volume_down_button, container, false);

        SharedPreferences prefs = requireContext().getSharedPreferences("test_status", Context.MODE_PRIVATE);
        editor = prefs.edit();

        tvVolumeDownFeedback = view.findViewById(R.id.tvVolumeDownFeedback);
        btVolumeDownYes = view.findViewById(R.id.btVolumeDownYes);
        btVolumeDownNo = view.findViewById(R.id.btVolumeDownNo);

        btVolumeDownYes.setOnClickListener(v -> setResultAndExit("approved"));
        btVolumeDownNo.setOnClickListener(v -> setResultAndExit("failed"));

        // Capturar botón de volumen abajo
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    tvVolumeDownFeedback.setText("Volume Down button pressed");
                    return true;
                }
                return false;
            }
        });

        return view;
    }

    private void setResultAndExit(String status) {
        editor.putString("volume_down_button_test_status", status);
        editor.apply();
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new TestsFragment())
                .commit();
    }
}
