package com.example.diagsmartv2.tests.interactive;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.diagsmartv2.R;
import com.example.diagsmartv2.tests.TestsFragment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Locale;

public class TestChargingFragment extends Fragment {

    private TextView tvIsCharging, tvTestLevel, tvTestTemperature, tvTestVoltage, tvTestCurrent, tvTestPower;
    private Button btChargingYes, btChargingNo;

    private SharedPreferences.Editor editor;

    private final Handler handler = new Handler();
    private BatteryManager batteryManager;

    public TestChargingFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_charging, container, false);

        SharedPreferences prefs = requireContext().getSharedPreferences("test_status", Context.MODE_PRIVATE);
        editor = prefs.edit();

        initReferences(view);
        setListenersToButtons();
        isBatteryChargingTest();
        getBatteryLevelTest();
        getBatteryTemperatureTest();
        getBatteryVoltageTest();
        getBatteryCurrentTest();
        getBatteryPowerTest();

        return view;
    }

    /**
     * Binds all TextViews for battery data and result buttons from the layout.
     *
     * @param view inflated root view.
     */
    private void initReferences(View view) {
        tvIsCharging = view.findViewById(R.id.tvIsCharging);
        tvTestLevel = view.findViewById(R.id.tvTestLevel);
        tvTestTemperature = view.findViewById(R.id.tvTestTemperature);
        tvTestVoltage = view.findViewById(R.id.tvTestVoltage);
        tvTestCurrent = view.findViewById(R.id.tvTestCurrent);
        tvTestPower = view.findViewById(R.id.tvTestPower);
        btChargingYes = view.findViewById(R.id.btChargingYes);
        btChargingNo = view.findViewById(R.id.btChargingNo);
    }

    /**
     * Attaches click listeners to Yes/No buttons that save the test result
     * and navigate back to the tests list.
     */
    private void setListenersToButtons() {
        btChargingYes.setOnClickListener(v -> setResultAndExit("approved"));
        btChargingNo.setOnClickListener(v -> setResultAndExit("failed"));
    }

    /**
     * Saves the test result ("approved" or "failed") to SharedPreferences,
     * stops all update handlers and navigates back to TestsFragment.
     *
     * @param status test outcome ("approved" or "failed").
     */
    private void setResultAndExit(String status) {
        editor.putString("charging_test_status", status);
        editor.apply();
        stopHandlers();
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new TestsFragment())
                .commit();
    }

    /**
     * Reads the battery status from the sticky BATTERY_CHANGED intent and
     * displays whether the device is currently charging.
     */
    @SuppressLint("SetTextI18n")
    private void isBatteryChargingTest() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = requireContext().registerReceiver(null, ifilter);

        if (batteryStatus != null) {
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
            tvIsCharging.setText(isCharging ? "Charging" : "Not charging");
        } else {
            tvIsCharging.setText("N/A");
        }
    }

    /**
     * Calculates and displays the current battery level as a percentage.
     */
    private void getBatteryLevelTest() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = requireContext().registerReceiver(null, ifilter);

        if (batteryStatus != null) {
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            float batteryPct = scale > 0 ? level * 100 / (float) scale : 0;
            @SuppressLint("DefaultLocale") String batteryPercentage = String.format("%.0f%%", batteryPct);

            tvTestLevel.setText(batteryPercentage);
        } else {
            tvTestLevel.setText("N/A");
        }
    }

    /**
     * Reads and displays the battery temperature in degrees Celsius.
     */
    @SuppressLint("SetTextI18n")
    private void getBatteryTemperatureTest() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = requireContext().registerReceiver(null, ifilter);

        if (batteryStatus != null) {
            int temperature = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);

            int batteryTemperature = temperature / 10;
            tvTestTemperature.setText(batteryTemperature + " °C");
        } else {
            tvTestTemperature.setText("N/A");
        }
    }

    /**
     * Starts a periodic task (1 second interval) to read and display
     * the current battery voltage in millivolts.
     */
    private void getBatteryVoltageTest() {
        handler.post(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = requireContext().registerReceiver(null, ifilter);

                if (batteryStatus != null) {
                    int voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
                    tvTestVoltage.setText(voltage != -1 ? voltage + " mV" : "N/A mV");
                } else {
                    tvTestVoltage.setText("N/A mV");
                }

                handler.postDelayed(this, 1000);
            }
        });
    }

    /**
     * Starts a periodic task (1 second interval) to read battery current using
     * BatteryManager first, then falls back to multiple sysfs paths if needed.
     */
    private void getBatteryCurrentTest() {
        batteryManager = (BatteryManager) requireContext().getSystemService(Context.BATTERY_SERVICE);

        handler.post(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                int current = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
                boolean success = current != Integer.MIN_VALUE;

                if (!success) {
                    String[] possiblePaths = {
                            "/sys/class/power_supply/battery/current_now",
                            "/sys/class/power_supply/battery/batt_current",
                            "/sys/devices/platform/battery/power_supply/battery/current_now"
                    };

                    for (String path : possiblePaths) {
                        try {
                            BufferedReader reader = new BufferedReader(new FileReader(path));
                            String line = reader.readLine();
                            reader.close();

                            if (line != null) {
                                current = Integer.parseInt(line.trim());
                                success = true;
                                break;
                            }
                        } catch (Exception ignored) {}
                    }
                }

                if (success) {
                    int currentmA = Math.abs(current / 1000);
                    tvTestCurrent.setText(currentmA + " mA");
                } else {
                    tvTestCurrent.setText("N/A mA");
                }

                handler.postDelayed(this, 1000);
            }
        });
    }

    /**
     * Starts a periodic task (1 second interval) to calculate and display
     * battery power (voltage × current) in watts, using BatteryManager or sysfs.
     */
    private void getBatteryPowerTest() {
        batteryManager = (BatteryManager) requireContext().getSystemService(Context.BATTERY_SERVICE);

        handler.post(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                float power = 0;
                boolean success = false;

                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = requireContext().registerReceiver(null, ifilter);

                if (batteryStatus != null) {
                    int voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
                    int current = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);

                    if (voltage != -1 && current != Integer.MIN_VALUE) {
                        float voltageV = voltage / 1000f; // Convert mV to V
                        float currentA = Math.abs(current) / 1000000f; // Convert µA to A
                        power = voltageV * currentA;
                        success = true;
                    }
                }

                if (!success) {
                    String voltagePath = "/sys/class/power_supply/battery/voltage_now";
                    String currentPath = "/sys/class/power_supply/battery/current_now";

                    try {
                        BufferedReader voltageReader = new BufferedReader(new FileReader(voltagePath));
                        BufferedReader currentReader = new BufferedReader(new FileReader(currentPath));

                        String voltageLine = voltageReader.readLine();
                        String currentLine = currentReader.readLine();

                        voltageReader.close();
                        currentReader.close();

                        if (voltageLine != null && currentLine != null) {
                            float voltageV = Float.parseFloat(voltageLine.trim()) / 1000000f; // Convert to V
                            float currentA = Math.abs(Float.parseFloat(currentLine.trim())) / 1000000f; // Convert to A
                            power = voltageV * currentA;
                            success = true;
                        }
                    } catch (Exception ignored) {}
                }

                tvTestPower.setText(success ?
                        String.format(Locale.US, "%.2f W", power) : "N/A W");

                handler.postDelayed(this, 1000);
            }
        });
    }

    /**
     * Stops all periodic update tasks by removing all Handler callbacks.
     */
    private void stopHandlers() {
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * Cleans up all running Handler tasks when the fragment view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopHandlers();
    }
}
