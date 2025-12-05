package com.example.diagsmartv2.tests.interactive;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.diagsmartv2.R;
import com.example.diagsmartv2.tests.TestsFragment;

public class TestMicrophoneFragment extends Fragment {

    private Button btMicrophoneYes, btMicrophoneNo;
    private ProgressBar pbMicrophone;
    private SharedPreferences.Editor editor;

    private static final int SAMPLE_RATE = 44100;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT);
    private AudioRecord audioRecord;
    private final Handler handler = new Handler();
    private Runnable updateTask;
    private boolean isRecording = false;

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startMicrophoneTest();
                } else {
                    Toast.makeText(requireContext(), "Microphone permission required", Toast.LENGTH_SHORT).show();
                }
            });


    public TestMicrophoneFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_microphone, container, false);

        SharedPreferences prefs = requireContext().getSharedPreferences("test_status", Context.MODE_PRIVATE);
        editor = prefs.edit();

        initReferences(view);
        setListenersToButtons();
        checkAudioPermission();

        return view;
    }

    /**
     * Binds Yes/No buttons and audio level ProgressBar from the layout.
     *
     * @param view inflated root view.
     */
    private void initReferences(View view) {
        btMicrophoneYes = view.findViewById(R.id.btMicrophoneYes);
        btMicrophoneNo = view.findViewById(R.id.btMicrophoneNo);
        pbMicrophone = view.findViewById(R.id.pbMicrophone);
    }

    /**
     * Attaches click listeners to Yes/No buttons that save the test result
     * and navigate back to the tests list.
     */
    private void setListenersToButtons() {
        btMicrophoneYes.setOnClickListener(v -> setResultAndExit("approved"));
        btMicrophoneNo.setOnClickListener(v -> setResultAndExit("failed"));
    }

    /**
     * Saves the test result ("approved" or "failed") to SharedPreferences,
     * stops microphone recording and navigates back to TestsFragment.
     *
     * @param status test outcome ("approved" or "failed").
     */
    private void setResultAndExit(String status) {
        editor.putString("microphone_test_status", status);
        editor.apply();
        stopMicrophoneTest();
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new TestsFragment())
                .commit();
    }

    /**
     * Checks RECORD_AUDIO permission and launches permission request if needed,
     * or starts the microphone test directly if already granted.
     */
    private void checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO);
        } else {
            startMicrophoneTest();
        }
    }

    /**
     * Initializes AudioRecord with 44.1kHz mono 16‑bit PCM, checks for microphone hardware
     * and starts recording with real‑time progress bar updates.
     */
    private void startMicrophoneTest() {
        PackageManager pm = requireContext().getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
            Toast.makeText(requireContext(), "No microphone detected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    BUFFER_SIZE
            );

            if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
                throw new Exception("AudioRecord initialization failed");
            }

            isRecording = true;
            audioRecord.startRecording();
            updateProgressBar();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Microphone error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("MicrophoneTest", "Configuration error", e);
        }
    }

    /**
     * Starts a periodic task (100ms interval) that reads audio buffer, calculates
     * dB level from maximum amplitude and updates the ProgressBar.
     */
    private void updateProgressBar() {
        updateTask = new Runnable() {
            @Override
            public void run() {
                if (isRecording) {
                    short[] buffer = new short[BUFFER_SIZE];
                    int read = audioRecord.read(buffer, 0, BUFFER_SIZE);

                    if (read > 0) {
                        int maxAmplitude = 0;
                        for (short sample : buffer) {
                            if (Math.abs(sample) > maxAmplitude) {
                                maxAmplitude = Math.abs(sample);
                            }
                        }
                        int progress = (int) (20 * Math.log10((maxAmplitude + 1) / 327.67));
                        pbMicrophone.setProgress(Math.max(0, progress + 60));
                    }
                }
                handler.postDelayed(this, 100);
            }
        };
        handler.post(updateTask);
    }

    /**
     * Stops AudioRecord recording, removes update task callbacks and releases resources.
     */
    private void stopMicrophoneTest() {
        isRecording = false;
        if (updateTask != null) {
            handler.removeCallbacks(updateTask);
        }
        if (audioRecord != null) {
            try {
                audioRecord.stop();
                audioRecord.release();
            } catch (IllegalStateException ignored) {}
            audioRecord = null;
        }
    }

    /**
     * Cleans up AudioRecord and stops updates when the fragment view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopMicrophoneTest();
    }
}
