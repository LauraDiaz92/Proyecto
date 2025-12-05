package com.example.diagsmartv2.tests.interactive;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.diagsmartv2.R;
import com.example.diagsmartv2.tests.TestsFragment;

public class TestLoudspeakerFragment extends Fragment {

    private Button btSpeakerYes, btSpeakerNo;
    private SharedPreferences.Editor editor;
    private MediaPlayer mediaPlayer;

    public TestLoudspeakerFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_loudspeaker, container, false);

        SharedPreferences prefs = requireContext().getSharedPreferences("test_status", Context.MODE_PRIVATE);
        editor = prefs.edit();

        initReferences(view);
        setListenersToButtons();
        playSound();

        return view;
    }

    /**
     * Binds Yes/No buttons from the layout.
     *
     * @param view inflated root view.
     */
    private void initReferences(View view) {
        btSpeakerYes = view.findViewById(R.id.btSpeakerYes);
        btSpeakerNo = view.findViewById(R.id.btSpeakerNo);
    }

    /**
     * Attaches click listeners to Yes/No buttons that save the test result
     * and navigate back to the tests list.
     */
    private void setListenersToButtons() {
        btSpeakerYes.setOnClickListener(v -> setResultAndExit("approved"));
        btSpeakerNo.setOnClickListener(v -> setResultAndExit("failed"));
    }

    /**
     * Saves the test result ("approved" or "failed") to SharedPreferences,
     * stops the audio playback and navigates back to TestsFragment.
     *
     * @param status test outcome ("approved" or "failed").
     */
    private void setResultAndExit(String status) {
        editor.putString("loudspeaker_test_status", status);
        editor.apply();
        stopSound();
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new TestsFragment())
                .commit();
    }

    /**
     * Creates and starts playback of the loudspeaker test sound from raw resources.
     */
    private void playSound() {
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.audio_test);
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    /**
     * Stops MediaPlayer playback, releases resources safely.
     */
    private void stopSound() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (IllegalStateException e) {
                // Stopped
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * Ensures the MediaPlayer is stopped and released when the fragment view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopSound();
    }
}
