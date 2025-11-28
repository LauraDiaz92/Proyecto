package com.example.diagsmartv2.tests;

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

    private void initReferences(View view) {
        btEarSpeakerYes = view.findViewById(R.id.btEarSpeakerYes);
        btEarSpeakerNo = view.findViewById(R.id.btEarSpeakerNo);
    }

    private void setListenersToButtons() {
        btEarSpeakerYes.setOnClickListener(v -> setResultAndExit("approved"));
        btEarSpeakerNo.setOnClickListener(v -> setResultAndExit("failed"));
    }

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

    private void setModeInCall() {
        AudioManager audioManager = (AudioManager) requireContext().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            audioManager.setSpeakerphoneOn(false);

            // Ajustar volumen al máximo para el earspeaker
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, maxVolume, 0);
        }
    }

    private void stopSoundAndResetAudio() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (IllegalStateException e) { /* Ignorar */ }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        // Restaurar el modo de audio
        AudioManager audioManager = (AudioManager) requireContext().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopSoundAndResetAudio();
    }
}
