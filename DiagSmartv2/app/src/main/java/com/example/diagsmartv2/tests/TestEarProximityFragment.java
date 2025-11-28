package com.example.diagsmartv2.tests;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.diagsmartv2.R;

public class TestEarProximityFragment extends Fragment implements SensorEventListener {

    private Button btEarProxYes, btEarProxNo;
    private ImageView ivEarProximity;
    private TextView tvFeedback;

    private SharedPreferences.Editor editor;

    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private boolean isNear = false;
    private Vibrator vibrator;

    public TestEarProximityFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_ear_proximity, container, false);

        SharedPreferences prefs = requireContext().getSharedPreferences("test_status", Context.MODE_PRIVATE);
        editor = prefs.edit();

        initReferences(view);
        setListenersToButtons();
        setSensorConfig();
        setListenerToImage();

        return view;
    }

    private void initReferences(View view) {
        btEarProxYes = view.findViewById(R.id.btEarProxYes);
        btEarProxNo = view.findViewById(R.id.btEarProxNo);
        ivEarProximity = view.findViewById(R.id.ivEarProximity);
        tvFeedback = view.findViewById(R.id.tvFeedback);
    }

    private void setListenersToButtons() {
        btEarProxYes.setOnClickListener(v -> setResultAndExit("approved"));
        btEarProxNo.setOnClickListener(v -> setResultAndExit("failed"));
    }

    private void setResultAndExit(String status) {
        editor.putString("ear_proximity_test_status", status);
        editor.apply();
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new TestsFragment())
                .commit();
    }

    @SuppressLint("SetTextI18n")
    private void setSensorConfig() {
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);

        if (proximitySensor == null) {
            tvFeedback.setText("Sensor not available!");
            ivEarProximity.setEnabled(false);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListenerToImage() {
        ivEarProximity.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                handleProximityDetection();
                v.performClick();
                return true;
            }
            return false;
        });
    }

    @SuppressLint("SetTextI18n")
    private void handleProximityDetection() {
        if (isNear) {
            tvFeedback.setText("Ear detected ✓");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(200, 255));
            }
            editor.putString("ear_proximity_test_status", "approved");
            editor.apply();
        } else {
            tvFeedback.setText("Touch detected ✓");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, 128));
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float distance = event.values[0];
            isNear = distance < proximitySensor.getMaximumRange();

            tvFeedback.setText(
                    isNear ? "Object detected near the sensor." : "No object detected near the sensor.");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onResume() {
        super.onResume();
        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (proximitySensor != null) {
            sensorManager.unregisterListener(this);
        }
    }
}
