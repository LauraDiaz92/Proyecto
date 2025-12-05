package com.example.diagsmartv2.tests.interactive;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.diagsmartv2.R;
import com.example.diagsmartv2.tests.TestsFragment;

import java.io.IOException;

public class TestEarSpeakerFragment extends Fragment {

    private Button btEarSpeakerYes, btEarSpeakerNo;
    private SharedPreferences.Editor editor;
    private MediaPlayer mediaPlayer;

    public TestEarSpeakerFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_ear_speaker, container, false);

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
        btEarSpeakerYes = view.findViewById(R.id.btEarSpeakerYes);
        btEarSpeakerNo = view.findViewById(R.id.btEarSpeakerNo);
    }

    /**
     * Attaches click listeners to Yes/No buttons that save the test result
     * and navigate back to the tests list.
     */
    private void setListenersToButtons() {
        btEarSpeakerYes.setOnClickListener(v -> setResultAndExit("approved"));
        btEarSpeakerNo.setOnClickListener(v -> setResultAndExit("failed"));
    }

    /**
     * Saves the test result ("approved" or "failed") to SharedPreferences,
     * stops the audio playback and resets audio mode before navigating back.
     *
     * @param status test outcome ("approved" or "failed").
     */
    private void setResultAndExit(String status) {
        editor.putString("ear_speaker_test_status", status);
        editor.apply();
        stopSoundAndResetAudio();
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new TestsFragment())
                .commit();
    }

    /**
     * Configures AudioManager for ear speaker playback (MODE_IN_CALL, speaker off,
     * max voice call volume) and starts looping playback of the test sound.
     */
    private void playSound() {
        setModeInCall();

        mediaPlayer = new MediaPlayer();
        AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.audio_eartest);
        try {
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Log.e("TestEarSpeakerFragment", "Error playing the sound");
        }
    }

    /**
     * Sets AudioManager to IN_CALL mode, disables speakerphone and maximizes
     * voice call stream volume for ear speaker testing.
     */
    private void setModeInCall() {
        AudioManager audioManager = (AudioManager) requireContext().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            audioManager.setSpeakerphoneOn(false);

            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, maxVolume, 0);
        }
    }

    /**
     * Stops MediaPlayer playback, releases resources and resets AudioManager
     * to normal mode.
     */
    private void stopSoundAndResetAudio() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (IllegalStateException e) { /* Ignore */ }
            mediaPlayer.release();
            mediaPlayer = null;
        }

        AudioManager audioManager = (AudioManager) requireContext().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
        }
    }

    /**
     * Cleans up MediaPlayer and resets audio mode when the fragment view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopSoundAndResetAudio();
    }
}
