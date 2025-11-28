package com.example.diagsmartv2.tests;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.diagsmartv2.R;

import java.util.Locale;

public class TestLightSensorFragment extends Fragment implements SensorEventListener {

    private TextView tvLXTest;
    private Button btLightSensorYes, btLightSensorNo;

    private SharedPreferences.Editor editor;

    private SensorManager sensorManager;
    private Sensor lightSensor;

    public TestLightSensorFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_light_sensor, container, false);

        SharedPreferences prefs = requireContext().getSharedPreferences("test_status", Context.MODE_PRIVATE);
        editor = prefs.edit();

        initReferences(view);
        setListenersToButtons();
        setLightSensor();

        return view;
    }

    private void initReferences(View view) {
        tvLXTest = view.findViewById(R.id.tvLXText);
        btLightSensorYes = view.findViewById(R.id.btLightSensorYes);
        btLightSensorNo = view.findViewById(R.id.btLightSensorNo);
        ImageView ivLightSensor = view.findViewById(R.id.ivLightSensor);
    }

    private void setListenersToButtons() {
        btLightSensorYes.setOnClickListener(v -> setResultAndExit("approved"));
        btLightSensorNo.setOnClickListener(v -> setResultAndExit("failed"));
    }

    private void setResultAndExit(String status) {
        editor.putString("light_sensor_test_status", status);
        editor.apply();
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new TestsFragment())
                .commit();
    }

    @SuppressLint("SetTextI18n")
    private void setLightSensor() {
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (lightSensor == null) {
            tvLXTest.setText("Light sensor not available");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (lightSensor != null && sensorManager != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (lightSensor != null && sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lux = event.values[0];
            tvLXTest.setText(String.format(Locale.getDefault(), "%.0f lx", lux));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
