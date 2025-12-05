package com.example.diagsmartv2.tests.interactive;

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
import com.example.diagsmartv2.tests.TestsFragment;

public class TestVolumeUpButtonFragment extends Fragment {

    private TextView tvVolumeUpFeedback;
    private Button btVolumeUpYes, btVolumeUpNo;
    private SharedPreferences.Editor editor;

    public TestVolumeUpButtonFragment() {}

    /**
     * Inflates the volume up button test layout, initializes UI components,
     * sets up button listeners and attaches key listener to detect volume up presses.
     *
     * @param inflater layout inflater used to inflate the view.
     * @param container parent view that the fragment UI should be attached to.
     * @param savedInstanceState previously saved state, if any.
     * @return the root view for this fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_volume_up_button, container, false);

        SharedPreferences prefs = requireContext().getSharedPreferences("test_status", Context.MODE_PRIVATE);
        editor = prefs.edit();

        tvVolumeUpFeedback = view.findViewById(R.id.tvVolumeUpFeedback);
        btVolumeUpYes = view.findViewById(R.id.btVolumeUpYes);
        btVolumeUpNo = view.findViewById(R.id.btVolumeUpNo);

        btVolumeUpYes.setOnClickListener(v -> setResultAndExit("approved"));
        btVolumeUpNo.setOnClickListener(v -> setResultAndExit("failed"));

        // Necesitas esto para capturar el evento del botón de volumen arriba
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                    tvVolumeUpFeedback.setText("Volume Up button pressed");
                    return true;
                }
                return false;
            }
        });

        return view;
    }

    /**
     * Saves the test result ("approved" or "failed") to SharedPreferences and
     * navigates back to TestsFragment.
     *
     * @param status test outcome ("approved" or "failed").
     */
    private void setResultAndExit(String status) {
        editor.putString("volume_up_button_test_status", status);
        editor.apply();
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new TestsFragment())
                .commit();
    }
}
