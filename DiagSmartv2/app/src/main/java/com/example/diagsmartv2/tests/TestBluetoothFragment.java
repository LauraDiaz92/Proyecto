package com.example.diagsmartv2.tests;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.diagsmartv2.R;

public class TestBluetoothFragment extends Fragment {

    private TextView tvBluetoothText;
    private Button btBluetoothYes, btBluetoothNo;

    private SharedPreferences.Editor editor;

    private BluetoothAdapter bluetoothAdapter;

    private ActivityResultLauncher<Intent> enableBluetoothLauncher;

    @SuppressLint("SetTextI18n")
    private final ActivityResultLauncher<String[]> bluetoothPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                boolean allGranted = true;
                for (Boolean granted : result.values()) {
                    if (!granted) {
                        allGranted = false;
                        break;
                    }
                }
                if (allGranted) {
                    checkAndEnableBluetooth();
                } else {
                    tvBluetoothText.setText("Test could not be completed");
                    showNearbyDevicesDialog();
                }
            });


    public TestBluetoothFragment() {}

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_bluetooth, container, false);

        SharedPreferences prefs = requireContext().getSharedPreferences("test_status", Context.MODE_PRIVATE);
        editor = prefs.edit();

        tvBluetoothText = view.findViewById(R.id.tvBluetoothText);
        btBluetoothYes = view.findViewById(R.id.btBluetoothYes);
        btBluetoothNo = view.findViewById(R.id.btBluetoothNo);

        btBluetoothYes.setOnClickListener(v -> setResultAndExit("approved"));
        btBluetoothNo.setOnClickListener(v -> setResultAndExit("failed"));

        enableBluetoothLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                        tvBluetoothText.setText("Test passed");
                    } else {
                        tvBluetoothText.setText("Test could not be completed");
                    }
                }
        );

        BluetoothManager bluetoothManager = (BluetoothManager) requireContext().getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null) {
            tvBluetoothText.setText("Bluetooth not supported on this device");
            return view;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!hasBluetoothPermissions()) {
                showNearbyDevicesDialog();
            } else {
                checkAndEnableBluetooth();
            }
        } else {
            checkAndEnableBluetooth();
        }

        return view;
    }

    private boolean hasBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    @SuppressLint("SetTextI18n")
    private void showNearbyDevicesDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Permission required")
                .setMessage("Please enable 'Nearby devices' permission in Settings")
                .setCancelable(false)
                .setNegativeButton("Cancel", (dialog, which) -> tvBluetoothText.setText("Test could not be completed"))
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(android.net.Uri.parse("package:" + requireContext().getPackageName()));
                    startActivity(intent);
                })
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @SuppressLint("SetTextI18n")
    private void showBluetoothPermissionDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Bluetooth permission required")
                .setMessage("This app needs Bluetooth permissions to function properly")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    bluetoothPermissionLauncher.launch(
                            new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN}
                    );
                })
                .show();
    }

    @SuppressLint("SetTextI18n")
    private void checkAndEnableBluetooth() {
        try {
            if (bluetoothAdapter.isEnabled()) {
                tvBluetoothText.setText("Bluetooth is already enabled");
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBluetoothLauncher.launch(enableBtIntent);
            }
        } catch (SecurityException e) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                showBluetoothPermissionDialog();
            }
        }
    }

    private void setResultAndExit(String status) {
        editor.putString("bluetooth_test_status", status);
        editor.apply();
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new TestsFragment())
                .commit();
    }
}
