package com.example.diagsmartv2.tests.interactive;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
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

public class TestFlashlightFragment extends Fragment {

    private Button btFlashLightYes, btFlashLightNo;
    private boolean isFlashlightOn = false;
    private CameraManager cameraManager;
    private String cameraId;
    private SharedPreferences.Editor editor;

    public TestFlashlightFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_flashlight, container, false);

        SharedPreferences prefs = requireContext().getSharedPreferences("test_status", Context.MODE_PRIVATE);
        editor = prefs.edit();

        initReferences(view);
        setListenersToButtons();

        turnOnFlashLight();

        return view;
    }

    /**
     * Binds Yes/No buttons from the layout.
     *
     * @param view inflated root view.
     */
    private void initReferences(View view) {
        btFlashLightYes = view.findViewById(R.id.btFlashLightYes);
        btFlashLightNo = view.findViewById(R.id.btFlashLightNo);
    }

    /**
     * Attaches click listeners to Yes/No buttons that save the test result
     * and navigate back to the tests list.
     */
    private void setListenersToButtons() {
        btFlashLightYes.setOnClickListener(v -> setResultAndExit("approved"));
        btFlashLightNo.setOnClickListener(v -> setResultAndExit("failed"));
    }

    /**
     * Saves the test result ("approved" or "failed") to SharedPreferences,
     * turns off the flashlight and navigates back to TestsFragment.
     *
     * @param status test outcome ("approved" or "failed").
     */
    private void setResultAndExit(String status) {
        editor.putString("flashlight_test_status", status);
        editor.apply();
        turnOffFlashLight();
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new TestsFragment())
                .commit();
    }

    /**
     * Initializes CameraManager, gets the first available camera ID and
     * turns on torch mode if not already active.
     */
    private void turnOnFlashLight() {
        cameraManager = (CameraManager) requireContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
            if (!isFlashlightOn) {
                cameraManager.setTorchMode(cameraId, true);
                isFlashlightOn = true;
            }
        } catch (CameraAccessException e) {
            Log.e("TestFlashlightFragment", "Error turning on flashlight");
        }
    }

    /**
     * Turns off torch mode on the camera using CameraManager.setTorchMode.
     */
    private void turnOffFlashLight() {
        if (cameraManager == null || cameraId == null) return;
        try {
            if (isFlashlightOn) {
                cameraManager.setTorchMode(cameraId, false);
                isFlashlightOn = false;
            }
        } catch (CameraAccessException e) {
            Log.e("TestFlashlightFragment", "Error turning off flashlight");
        }
    }

    /**
     * Ensures the flashlight is turned off when the fragment view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        turnOffFlashLight();
    }
}
