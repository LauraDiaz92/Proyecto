package com.example.diagsmartv2;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.diagsmartv2.sensors.SensorsFragment;
import com.example.diagsmartv2.tests.TestsFragment;

public class MainActivity extends AppCompatActivity {

    TextView tvDashboard, tvDevice, tvCamera, tvBattery, tvCPU, tvDisplay,
             tvSensors, tvSystem, tvTests, tvNetwork;

    private TextView lastSelected = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new DashboardFragment())
                .commit();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initReferences();
        updateMenuSelection(tvDashboard);
        setListenersToMenuItems();
    }

    /**
     * Finds and stores references to all TextViews used as menu items.
     */
    private void initReferences() {
        tvDashboard = findViewById(R.id.tvDashboard);
        tvDevice = findViewById(R.id.tvDevice);
        tvCamera = findViewById(R.id.tvCamera);
        tvBattery = findViewById(R.id.tvBattery);
        tvCPU = findViewById(R.id.tvCPU);
        tvDisplay = findViewById(R.id.tvDisplay);
        tvSensors = findViewById(R.id.tvSensors);
        tvSystem = findViewById(R.id.tvSystem);
        tvTests = findViewById(R.id.tvTests);
        tvNetwork = findViewById(R.id.tvNetwork);
    }

    /**
     * Attaches click listeners to each menu item and switches to the
     * corresponding fragment when an item is selected.
     */
    private void setListenersToMenuItems() {
        tvDashboard.setOnClickListener(v -> {
            updateMenuSelection(tvDashboard);
            setFragmentIfNotCurrent(new DashboardFragment(), "DashboardFragment");
        });
        tvDevice.setOnClickListener(v -> {
            updateMenuSelection(tvDevice);
            setFragmentIfNotCurrent(new DeviceFragment(), "DeviceFragment");
        });
        tvSystem.setOnClickListener(v -> {
            updateMenuSelection(tvSystem);
            setFragmentIfNotCurrent(new SystemFragment(), "SystemFragment");
        });
        tvCPU.setOnClickListener(v -> {
            updateMenuSelection(tvCPU);
            setFragmentIfNotCurrent(new CpuFragment(), "CpuFragment");
        });
        tvBattery.setOnClickListener(v -> {
            updateMenuSelection(tvBattery);
            setFragmentIfNotCurrent(new BatteryFragment(), "BatteryFragment");
        });
        tvNetwork.setOnClickListener(v -> {
            updateMenuSelection(tvNetwork);
            setFragmentIfNotCurrent(new NetworkFragment(), "NetworkFragment");
        });
        tvDisplay.setOnClickListener(v -> {
            updateMenuSelection(tvDisplay);
            setFragmentIfNotCurrent(new DisplayFragment(), "DisplayFragment");
        });
        tvCamera.setOnClickListener(v -> {
            updateMenuSelection(tvCamera);
            setFragmentIfNotCurrent(new CameraFragment(), "CameraFragment");
        });
        tvSensors.setOnClickListener(v -> {
            updateMenuSelection(tvSensors);
            setFragmentIfNotCurrent(new SensorsFragment(), "SensorsFragment");
        });
        tvTests.setOnClickListener(v -> {
            updateMenuSelection(tvTests);
            setFragmentIfNotCurrent(new TestsFragment(), "TestsFragment");
        });
    }

    /**
     * Updates the visual state of the menu, clearing previous highlights
     * and applying the selected style to the given TextView.
     *
     * @param selected menu TextView that should appear as selected.
     */
    private void updateMenuSelection(TextView selected) {
        if (lastSelected != null && lastSelected != selected) {
            lastSelected.setBackgroundResource(0);
            lastSelected.setTextColor(getThemeColor(this, com.google.android.material.R.attr.colorOnBackground));
        }
        tvDashboard.setBackgroundResource(0);
        tvDevice.setBackgroundResource(0);
        tvSystem.setBackgroundResource(0);
        tvCPU.setBackgroundResource(0);
        tvBattery.setBackgroundResource(0);
        tvCamera.setBackgroundResource(0);
        tvDisplay.setBackgroundResource(0);
        tvSensors.setBackgroundResource(0);
        tvTests.setBackgroundResource(0);
        tvNetwork.setBackgroundResource(0);

        selected.setBackgroundResource(R.drawable.shape_menu_textviews);
        selected.setTextColor(Color.WHITE);

        lastSelected = selected;
    }

    /**
     * Replaces the current fragment only if it is different from the one
     * requested, avoiding unnecessary fragment transactions.
     *
     * @param fragment new fragment instance to display.
     * @param tag tag used to identify the fragment in the manager.
     */
    private void setFragmentIfNotCurrent(Fragment fragment, String tag) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment == null || !currentFragment.getClass().equals(fragment.getClass())) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment, tag)
                    .commit();
        }
    }

    /**
     * Highlights the dashboard item in the menu.
     * Called from fragments when navigating back to the dashboard.
     */
    public void highlightDashboard() {
        updateMenuSelection(tvDashboard);
    }

    /**
     * Resolves the given theme attribute to its actual color value.
     *
     * @param context context whose theme should be used.
     * @param attrResId resource id of the theme attribute.
     * @return resolved color integer.
     */
    private int getThemeColor(Context context, int attrResId) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(attrResId, typedValue, true);
        return typedValue.data;
    }


}