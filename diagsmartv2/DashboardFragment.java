package com.example.diagsmartv2;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import android.content.SharedPreferences;


public class DashboardFragment extends Fragment {

    TextView tvDashboardTotalRAM, tvDashboardUsedRAM, tvDashboardFreeRAM, tvFreeStorage,
            tvTotalStorage, tvPercentInternalStorage, tvCharging, tvVoltage, tvTemperature,
            tvPercentBattery, tvNumberSensors, tvNumberApps, tvScreenResolution,
            tvScreenHz, tvTestsCompleted;
    LinearLayout layoutCores;
    ConstraintLayout layoutDisplay;

    private final List<TextView> frequencyViews = new ArrayList<>();

    Context context;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isAdded() || getActivity() == null) {
                return;
            }
            updateCoreFrequencies();
            handler.postDelayed(this, 8000);
        }
    };

    public DashboardFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        initReferences(view);
        context = requireContext();
        setMemoryRAM();
        createCoreLayouts();
        handler.post(updateRunnable);
        updateInternalStorageInfo();
        updateBatteryInfo();
        getSensorCount();
        getTotalInstalledApps();
        displayScreenInfo();

        return view;
    }

    private void initReferences(View view) {
        tvDashboardTotalRAM = view.findViewById(R.id.tvDashboardTotalRAM);
        tvDashboardUsedRAM = view.findViewById(R.id.tvDashboardUsedRAM);
        tvDashboardFreeRAM = view.findViewById(R.id.tvDashboardFreeRAM);
        layoutCores = view.findViewById(R.id.layoutCores);
        tvFreeStorage = view.findViewById(R.id.tvFreeStorage);
        tvTotalStorage = view.findViewById(R.id.tvTotalStorage);
        tvPercentInternalStorage = view.findViewById(R.id.tvPercentInternalStorage);
        tvCharging = view.findViewById(R.id.tvCharging);
        tvVoltage = view.findViewById(R.id.tvVoltage);
        tvTemperature = view.findViewById(R.id.tvTemperature);
        tvPercentBattery = view.findViewById(R.id.tvPercentBattery);
        tvNumberSensors = view.findViewById(R.id.tvNumberSensors);
        tvNumberApps = view.findViewById(R.id.tvNumberApps);
        tvScreenResolution = view.findViewById(R.id.tvScreenResolution);
        tvScreenHz = view.findViewById(R.id.tvScreenHz);
        layoutDisplay = view.findViewById(R.id.layoutDisplay);
        tvTestsCompleted = view.findViewById(R.id.tvTestsCompleted);
    }

    /**
     * Retrieves and displays the total RAM of the device.
     * Uses ActivityManager to obtain memory information, calculates the total RAM in MB
     * and sets the text for tvTotalRAM Textview
     */
    @SuppressLint("SetTextI18n")
    private void setMemoryRAM() {
        ActivityManager activityManager = (ActivityManager) requireContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        long totalMemoryMB = memoryInfo.totalMem / (1024 * 1024);
        long availableMemoryMB = memoryInfo.availMem / (1024 * 1024);
        long usedMemoryMB = totalMemoryMB - availableMemoryMB;

        tvDashboardTotalRAM.setText("RAM - " + totalMemoryMB + " MB Total");
        tvDashboardUsedRAM.setText(usedMemoryMB + " MB Used");
        tvDashboardFreeRAM.setText(availableMemoryMB + " MB Free");
    }

    @SuppressLint("SetTextI18n")
    private void createCoreLayouts() {
        int numberOfCores = getNumberOfCores();
        // Crear dos filas
        LinearLayout topRow = new LinearLayout(requireContext());
        LinearLayout bottomRow = new LinearLayout(requireContext());
        topRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        bottomRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        bottomRow.setOrientation(LinearLayout.HORIZONTAL);

        for (int i = 0; i < numberOfCores; i++) {
            LinearLayout coreLayout = new LinearLayout(requireContext());
            coreLayout.setOrientation(LinearLayout.VERTICAL);
            coreLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
            ));
            coreLayout.setPadding(8, 8, 8, 8);

            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setColor(Color.parseColor("#FFFCFC"));
            shape.setCornerRadius(16);
            coreLayout.setBackground(shape);
            ((LinearLayout.LayoutParams) coreLayout.getLayoutParams()).setMargins(4, 4, 4, 4);

            TextView titleView = new TextView(requireContext());
            titleView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            titleView.setText("Core " + i);
            titleView.setTextSize(14);
            titleView.setTypeface(null, Typeface.BOLD);
            titleView.setTextColor(Color.BLACK);
            coreLayout.addView(titleView);

            TextView freqView = new TextView(requireContext());
            freqView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            freqView.setTextSize(12);
            freqView.setTextColor(Color.BLACK);
            coreLayout.addView(freqView);

            frequencyViews.add(freqView);

            if (i < 4) {
                topRow.addView(coreLayout);
            } else {
                bottomRow.addView(coreLayout);
            }
        }

        layoutCores.addView(topRow);
        layoutCores.addView(bottomRow);
    }

    public int getNumberOfCores() {
        return Runtime.getRuntime().availableProcessors();
    }

    @SuppressLint("SetTextI18n")
    private void updateCoreFrequencies() {
        int numberOfCores = getNumberOfCores();
        for (int i = 0; i < numberOfCores && i < frequencyViews.size(); i++) {
            int frequency = getCoreFrequency(i);
            TextView freqView = frequencyViews.get(i);
            if (frequency > 0) {
                freqView.setText(frequency + " MHz");
            } else {
                freqView.setText(context.getString(R.string.not_available));
            }
        }
    }

    private int getCoreFrequency(int coreNumber) {
        try {
            String path = "/sys/devices/system/cpu/cpu" + coreNumber + "/cpufreq/scaling_cur_freq";
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();
            reader.close();
            return Integer.parseInt(line) / 1000; // Convertir a MHz
        } catch (Exception e) {
            Log.e("BatteryFragment", "Error getting core frequency", e);

            return 0;
        }
    }

    @SuppressLint("DefaultLocale")
    public void updateInternalStorageInfo() {
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        long availableBlocks = stat.getAvailableBlocksLong();

        double totalSize = (double) totalBlocks * blockSize / (1024 * 1024 * 1024);
        double freeSize = (double) availableBlocks * blockSize / (1024 * 1024 * 1024);
        double usedPercentage = ((totalSize - freeSize) / totalSize) * 100;

        tvFreeStorage.setText(String.format("Free Storage: %.2f GB", freeSize));
        tvTotalStorage.setText(String.format("Total Storage: %.2f GB", totalSize));
        tvPercentInternalStorage.setText(String.format("Used: %.2f%%", usedPercentage));
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void updateBatteryInfo() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = requireActivity().registerReceiver(null, ifilter);

        if (batteryStatus != null) {
            // Voltaje
            int voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
            tvVoltage.setText(String.format(context.getString(R.string.voltage) + ": %dmV", voltage));

            // Temperature
            int temperature = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
            float temperatureCelsius = temperature / 10f;
            tvTemperature.setText(String.format(context.getString(R.string.temperature) + ": %.0f °C", temperatureCelsius));

            // Percentage
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int percentage = (int)((level / (float)scale) * 100);
            tvPercentBattery.setText(String.format("%d%%", percentage));

            // Charging status
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
            tvCharging.setText(isCharging ? context.getString(R.string.charging_) : "");
        }
    }

    private void getSensorCount() {
        SensorManager sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        int sensorCount = sensorList.size();
        tvNumberSensors.setText(String.valueOf(sensorCount));
    }

    private void getTotalInstalledApps() {
        PackageManager pm = requireContext().getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        List<ResolveInfo> pkgAppsList = pm.queryIntentActivities(mainIntent, 0);
        int count = pkgAppsList.size();

        tvNumberApps.setText(String.valueOf(count));
    }

    private void displayScreenInfo() {
        WindowManager windowManager = (WindowManager) requireActivity().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        String resolution = width + "x" + height;
        tvScreenResolution.setText(resolution);

        // Obtener las pulgadas de la pantalla
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);

        // Redondear a un decimal
        double roundedInches = Math.round(screenInches * 10.0) / 10.0;

        // Obtener la tasa de refresco
        float refreshRate;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            refreshRate = Objects.requireNonNull(requireActivity().getDisplay()).getRefreshRate();
        } else {
            windowManager = (WindowManager) requireActivity().getSystemService(Context.WINDOW_SERVICE);
            android.view.Display display = windowManager.getDefaultDisplay();
            refreshRate = display.getRefreshRate();
        }

        String screenInfo = String.format(Locale.US, "%.1f\" | %d Hz", roundedInches, Math.round(refreshRate));
        tvScreenHz.setText(screenInfo);
    }

    @SuppressLint("SetTextI18n")
    private void updateCompletedTestsLabel() {
        String[] interactiveTestsTitles = {
                "Display", "Multitouch", "Flashlight", "Loudspeaker", "Ear Speaker", "Microphone",
                "Ear Proximity", "Light Sensor", "Charging", "Bluetooth",
                "Volume Up Button", "Volume Down Button"
        };

        int totalTests = interactiveTestsTitles.length;
        int approvedCount = 0;

        SharedPreferences prefs = requireContext().getSharedPreferences("test_status", Context.MODE_PRIVATE);
        for (String test : interactiveTestsTitles) {
            String key = test.toLowerCase().replace(" ", "_") + "_test_status";
            String status = prefs.getString(key, "default");
            if ("approved".equals(status)) {
                approvedCount++;
            }
        }

        tvTestsCompleted.setText(approvedCount + " / " + totalTests);
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(updateRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateRunnable);
    }

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
        updateCompletedTestsLabel();
    }
}
