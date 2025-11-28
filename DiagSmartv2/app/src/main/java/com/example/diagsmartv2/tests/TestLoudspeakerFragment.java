package com.example.diagsmartv2.tests;

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

    private void initReferences(View view) {
        btSpeakerYes = view.findViewById(R.id.btSpeakerYes);
        btSpeakerNo = view.findViewById(R.id.btSpeakerNo);
    }

    private void setListenersToButtons() {
        btSpeakerYes.setOnClickListener(v -> setResultAndExit("approved"));
        btSpeakerNo.setOnClickListener(v -> setResultAndExit("failed"));
    }

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

    private void playSound() {
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.audio_test);
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    private void stopSound() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (IllegalStateException e) {
                // Ya parado
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopSound();
    }
}
