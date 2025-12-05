package com.example.diagsmartv2.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.diagsmartv2.DashboardFragment;
import com.example.diagsmartv2.MainActivity;
import com.example.diagsmartv2.R;

import java.util.ArrayList;
import java.util.List;

public class SensorsFragment extends Fragment implements OnItemClickListener {

    RecyclerView rvSensorsList;
    SensorAdapter adapter;
    private final List<SensorInfo> sensorInfoList = new ArrayList<>();

    public SensorsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sensors, container, false);

        rvSensorsList = view.findViewById(R.id.rvSensorsList);

        loadData();
        configureRecyclerView();

        return view;
    }

    /**
     * Queries the SensorManager for all available sensors, builds SensorInfo
     * objects with their properties and stores them in a list.
     */
    private void loadData() {
        SensorManager sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        for (Sensor sensor : sensors) {
            String power = String.valueOf(sensor.getPower());
            String resolution = String.valueOf(sensor.getResolution());
            float maxRange = sensor.getMaximumRange();
            String maxRangeStr = maxRange == 0.0f ? "Not available" : String.valueOf(maxRange);
            String wakeupSensor = sensor.isWakeUpSensor() ? "Yes" : "No";

            // More precise verification to determine if the sensor is dynamic
            String dynamicSensor = (sensor.getType() == Sensor.TYPE_ACCELEROMETER ||
                    sensor.getType() == Sensor.TYPE_GYROSCOPE ||
                    sensor.getType() == Sensor.TYPE_GYROSCOPE_UNCALIBRATED ||
                    sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD ||
                    sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED) ? "Yes" : "No";

            SensorInfo info = new SensorInfo(
                    sensor.getName(),
                    sensor.getVendor(),
                    getSensorType(sensor.getType()),
                    power,
                    resolution,
                    maxRangeStr,
                    wakeupSensor,
                    dynamicSensor
            );
            sensorInfoList.add(info);
        }
    }

    /**
     * Maps a numeric Android sensor type constant to a human‑readable
     * sensor type name.
     *
     * @param type integer sensor type from android.hardware.Sensor.
     * @return readable sensor type string.
     */
    private String getSensorType(int type) {
        switch (type) {
            case Sensor.TYPE_ACCELEROMETER:
                return "ACCELEROMETER";
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                return "AMBIENT TEMPERATURE";
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
                return "GAME ROTATION VECTOR";
            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                return "GEOMAGNETIC ROTATION VECTOR";
            case Sensor.TYPE_GRAVITY:
                return "GRAVITY";
            case Sensor.TYPE_GYROSCOPE:
                return "GYROSCOPE";
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                return "GYROSCOPE UNCALIBRATED";
            case Sensor.TYPE_HEART_RATE:
                return "HEART RATE";
            case Sensor.TYPE_LIGHT:
                return "LIGHT";
            case Sensor.TYPE_LINEAR_ACCELERATION:
                return "LINEAR ACCELERATION";
            case Sensor.TYPE_MAGNETIC_FIELD:
                return "MAGNETIC FIELD";
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                return "MAGNETIC FIELD UNCALIBRATED";
            case Sensor.TYPE_PRESSURE:
                return "PRESSURE";
            case Sensor.TYPE_PROXIMITY:
                return "PROXIMITY";
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                return "RELATIVE HUMIDITY";
            case Sensor.TYPE_ROTATION_VECTOR:
                return "ROTATION VECTOR";
            case Sensor.TYPE_SIGNIFICANT_MOTION:
                return "SIGNIFICANT MOTION";
            case Sensor.TYPE_STEP_COUNTER:
                return "STEP COUNTER";
            case Sensor.TYPE_STEP_DETECTOR:
                return "STEP DETECTOR";
            default:
                return "Unknown";
        }
    }

    /**
     * Sets up the RecyclerView with a linear vertical layout and attaches
     * the SensorAdapter containing the sensor list.
     */
    private void configureRecyclerView() {
        adapter = new SensorAdapter(sensorInfoList, this);
        rvSensorsList.setAdapter(adapter);
        rvSensorsList.setLayoutManager(new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false));
    }

    /**
     * Handles clicks on sensor list items by showing a dialog with
     * extended information for the selected sensor.
     *
     * @param sensorInfo SensorInfo instance representing the clicked sensor.
     */
    @Override
    public void onItemClick(SensorInfo sensorInfo) {
        SensorsExtraInfoFragment dialog = SensorsExtraInfoFragment.newInstance(sensorInfo);
        dialog.show(getParentFragmentManager(), "SensorsExtraInfoDialog");
    }

    /**
     * Registers a back‑press callback to return from the sensors screen
     * to the dashboard when this fragment is visible.
     */
    @Override
    public void onResume() {
        super.onResume();
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new DashboardFragment())
                        .commit();
                ((MainActivity) requireActivity()).highlightDashboard();
            }
        });
    }
}