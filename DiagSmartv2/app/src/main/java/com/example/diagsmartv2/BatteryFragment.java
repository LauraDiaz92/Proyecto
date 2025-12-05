package com.example.diagsmartv2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class BatteryFragment extends Fragment {

    TextView tvMa, tvW, tvTemperatureBattery, tvChargingStateBattery, tvHealth,
             tvLevel, tvStatus, tvPowerSource, tvTechnology, tvTemperatureII,
             tvCurrentII, tvPowerII, tvVoltageBattery, tvTimeToCharge, tvChargeCycles,
             tvCapacity;

    private final Handler handler = new Handler();
    private BatteryManager batteryManager;

    Context context;

    public BatteryFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_battery, container, false);

        initReferences(view);
        context = requireContext();
        getBatteryHealth();
        getBatteryLevel();
        isBatteryCharging();
        getBatteryPowerSource();
        getBatteryTechnology();
        getBatteryTemperature();
        getBatteryCurrent();
        getBatteryPower();
        getBatteryVoltage();
        getEstimatedChargeTime();
        getBatteryChargeCycles();
        getBatteryCapacity();

        return view;
    }

    /**
     * Finds and stores references to all battery TextViews in the layout.
     *
     * @param view root view containing the UI components.
     */
    private void initReferences(View view) {
        tvMa = view.findViewById(R.id.tvMa);
        tvW = view.findViewById(R.id.tvW);
        tvTemperatureBattery = view.findViewById(R.id.tvTemperatureBattery);
        tvChargingStateBattery = view.findViewById(R.id.tvChargingStateBattery);
        tvHealth = view.findViewById(R.id.tvHealth);
        tvLevel = view.findViewById(R.id.tvLevel);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvPowerSource = view.findViewById(R.id.tvPowerSource);
        tvTechnology = view.findViewById(R.id.tvTechnology);
        tvTemperatureII = view.findViewById(R.id.tvTemperatureII);
        tvCurrentII = view.findViewById(R.id.tvCurrentII);
        tvPowerII = view.findViewById(R.id.tvPowerII);
        tvVoltageBattery = view.findViewById(R.id.tvVoltageBattery);
        tvTimeToCharge = view.findViewById(R.id.tvTimeToCharge);
        tvChargeCycles = view.findViewById(R.id.tvChargeCycles);
        tvCapacity = view.findViewById(R.id.tvCapacity);
    }

    /**
     * Reads the current battery health and updates the corresponding TextView.
     */
    private void getBatteryHealth() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = requireActivity().registerReceiver(null, ifilter);

        assert batteryStatus != null;
        int health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);

        String healthStatus;
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_GOOD:
                healthStatus = context.getString(R.string.battery_health_good);
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                healthStatus = context.getString(R.string.battery_health_overheat);
                break;
            case BatteryManager.BATTERY_HEALTH_DEAD:
                healthStatus = context.getString(R.string.battery_health_dead);
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                healthStatus = context.getString(R.string.battery_health_over_voltage);
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                healthStatus = context.getString(R.string.battery_health_unspecified_failure);
                break;
            case BatteryManager.BATTERY_HEALTH_COLD:
                healthStatus = context.getString(R.string.battery_health_cold);
                break;
            default:
                healthStatus = context.getString(R.string.battery_health_unknown);
                break;
        }

        tvHealth.setText(healthStatus);
    }

    /**
     * Reads the current battery level percentage and updates the UI.
     */
    private void getBatteryLevel() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = requireActivity().registerReceiver(null, ifilter);

        assert batteryStatus != null;
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level * 100 / (float) scale;
        @SuppressLint("DefaultLocale") String batteryPercentage = String.format("%.0f%%", batteryPct);

        tvLevel.setText(batteryPercentage);
    }

    /**
     * Checks whether the battery is currently charging and updates the status labels.
     */
    @SuppressLint("SetTextI18n")
    private void isBatteryCharging() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = requireActivity().registerReceiver(null, ifilter);

        assert batteryStatus != null;
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        if (isCharging) {
            tvChargingStateBattery.setText(context.getString(R.string.charging));
            tvStatus.setText(context.getString(R.string.charging));
        } else {
            tvChargingStateBattery.setText(context.getString(R.string.not_charging));
            tvStatus.setText(context.getString(R.string.not_charging));
        }
    }

    /**
     * Determines the current power source (AC, USB, wireless or battery) and shows it.
     */
    private void getBatteryPowerSource() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = requireActivity().registerReceiver(null, ifilter);

        assert batteryStatus != null;
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        String powerSource;

        switch (chargePlug) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                powerSource = context.getString(R.string.ac_power_source);
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                powerSource = context.getString(R.string.usb_power_source);
                break;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                powerSource = context.getString(R.string.wireless_power_source);
                break;
            default:
                powerSource = context.getString(R.string.battery_power_source);
                break;
        }

        tvPowerSource.setText(powerSource);
    }

    /**
     * Retrieves the battery technology string and displays it.
     */
    private void getBatteryTechnology() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = requireActivity().registerReceiver(null, ifilter);

        assert batteryStatus != null;
        String technology = batteryStatus.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);

        if (technology == null || technology.isEmpty()) {
            technology = context.getString(R.string.unknown_technology);
        }

        tvTechnology.setText(technology);
    }

    /**
     * Reads the battery temperature, converts it to degrees Celsius and updates the UI.
     */
    @SuppressLint("SetTextI18n")
    private void getBatteryTemperature() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = requireActivity().registerReceiver(null, ifilter);

        assert batteryStatus != null;
        int temperature = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);

        int batteryTemperature = temperature / 10;
        tvTemperatureBattery.setText(String.valueOf(batteryTemperature) + " °C");
        tvTemperatureII.setText(String.valueOf(batteryTemperature) + " °C");
    }

    /**
     * Continuously reads the battery current and shows it in milliamps.
     */
    private void getBatteryCurrent() {
        batteryManager = (BatteryManager) requireContext().getSystemService(Context.BATTERY_SERVICE);

        handler.post(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                int current = 0;
                boolean success = false;

                current = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
                if (current != Integer.MIN_VALUE) {
                    success = true;
                }

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
                                current = Integer.parseInt(line);
                                success = true;
                                break;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }

                if (success) {
                    int currentmA = Math.abs(current / 1000);
                    tvCurrentII.setText(currentmA + " mA");
                    tvMa.setText(currentmA + " mA");
                } else {
                    tvCurrentII.setText("N/A mA");
                    tvMa.setText("N/A mA");
                }

                handler.postDelayed(this, 1000);
            }
        });
    }

    /**
     * Continuously calculates the battery power consumption in watts and displays it.
     */
    private void getBatteryPower() {
        batteryManager = (BatteryManager) requireContext().getSystemService(Context.BATTERY_SERVICE);
        handler.post(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                // Verificar si el fragment está adjunto
                if (!isAdded() || getActivity() == null) {
                    return;
                }
                float power = 0;
                boolean success = false;

                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = requireActivity().registerReceiver(null, ifilter);

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
                            float voltageV = Float.parseFloat(voltageLine) / 1000000f; // Convert to V
                            float currentA = Math.abs(Float.parseFloat(currentLine)) / 1000000f; // Convert to A
                            power = voltageV * currentA;
                            success = true;
                        }
                    } catch (Exception e) {
                        Log.e("BatteryFragment", "Error getting battery power", e);

                    }
                }

                if (success) {
                    tvPowerII.setText(String.format(Locale.US, "%.2f W", power));
                    tvW.setText(String.format(Locale.US, "%.2f W", power));
                } else {
                    tvPowerII.setText("N/A W");
                    tvW.setText("N/A W");
                }

                handler.postDelayed(this, 1000);
            }
        });
    }

    /**
     * Periodically reads the battery voltage and shows it in millivolts.
     */
    private void getBatteryVoltage() {
        handler.post(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = requireActivity().registerReceiver(null, ifilter);

                if (batteryStatus != null) {
                    int voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
                    if (voltage != -1) {
                        tvVoltageBattery.setText(voltage + " mV");
                    } else {
                        tvVoltageBattery.setText("N/A mV");
                    }
                } else {
                    tvVoltageBattery.setText("N/A mV");
                }

                handler.postDelayed(this, 1000);
            }
        });
    }

    /**
     * Estimates the remaining time to full charge and updates the timer label.
     */
    private void getEstimatedChargeTime() {
        handler.post(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = requireActivity().registerReceiver(null, ifilter);

                if (batteryStatus != null) {
                    int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                    int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

                    if (level != -1 && scale != -1 && status == BatteryManager.BATTERY_STATUS_CHARGING) {
                        float batteryPct = level / (float) scale;
                        float remainingPct = 1 - batteryPct;

                        long estimatedTimeMs = (long) (remainingPct * 2 * 60 * 60 * 1000);

                        @SuppressLint("DefaultLocale") String timeString = String.format("%02d:%02d:%02d",
                                TimeUnit.MILLISECONDS.toHours(estimatedTimeMs),
                                TimeUnit.MILLISECONDS.toMinutes(estimatedTimeMs) % TimeUnit.HOURS.toMinutes(1),
                                TimeUnit.MILLISECONDS.toSeconds(estimatedTimeMs) % TimeUnit.MINUTES.toSeconds(1));

                        tvTimeToCharge.setText(timeString);
                    } else {
                        tvTimeToCharge.setText("00:00:00");
                    }
                } else {
                    tvTimeToCharge.setText("00:00:00");
                }

                handler.postDelayed(this, 1000);
            }
        });
    }

    /**
     * Tries to obtain the number of battery charge cycles and displays it.
     */
    private void getBatteryChargeCycles() {
        int chargeCycles = -1;

        try {
            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = requireActivity().registerReceiver(null, iFilter);

            if (batteryStatus != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    // Android 14 (API 34) and higher
                    chargeCycles = batteryStatus.getIntExtra(BatteryManager.EXTRA_CYCLE_COUNT, -1);
                } else {
                    BatteryManager batteryManager = (BatteryManager) requireContext().getSystemService(Context.BATTERY_SERVICE);
                    if (batteryManager != null) {
                        chargeCycles = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("BatteryFragment", "Error getting charge cycles", e);
        }

        if (chargeCycles != -1) {
            tvChargeCycles.setText(String.format(Locale.US, "%d", chargeCycles));
        } else {
            tvChargeCycles.setText(requireContext().getString(R.string.not_available));
        }
    }

    /**
     * Reads the battery capacity in mAh and updates the corresponding TextView.
     */
    private void getBatteryCapacity() {
        batteryManager = (BatteryManager) requireContext().getSystemService(Context.BATTERY_SERVICE);
        int batteryCapacity = 0;

        batteryCapacity = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER) / 1000;

        if (batteryCapacity > 0) {
            tvCapacity.setText(String.format(Locale.US, "%d mAh", batteryCapacity));
        } else {
            tvCapacity.setText("N/A");
        }
    }

    /**
     * Stops all pending handler callbacks when the fragment view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * Registers a back-press callback to return to the dashboard when this fragment is visible.
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