package com.example.diagsmartv2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.jaredrummler.android.device.DeviceName;

public class DeviceFragment extends Fragment {

    TextView tvDeviceName, tvModel, tvManufacturer, tvDeviceD, tvBoard, tvHardware, tvBrand,
             tvAndroidDeviceID, tvBuildFingerprint;

    Context context;

    public DeviceFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, container, false);

        initReferences(view);
        context = requireContext();
        setDeviceInfo();

        return view;
    }

    private void initReferences(View view) {
        tvDeviceName = view.findViewById(R.id.tvDeviceName);
        tvModel = view.findViewById(R.id.tvModel);
        tvManufacturer = view.findViewById(R.id.tvManufacturer);
        tvDeviceD = view.findViewById(R.id.tvDeviceD);
        tvBoard = view.findViewById(R.id.tvBoard);
        tvHardware = view.findViewById(R.id.tvHardware);
        tvBrand = view.findViewById(R.id.tvBrand);
        tvAndroidDeviceID = view.findViewById(R.id.tvAndroidDeviceID);
        tvBuildFingerprint = view.findViewById(R.id.tvBuildFingerprint);

    }

    private void setDeviceInfo() {
        DeviceName.with(requireContext()).request(new DeviceName.Callback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onFinished(DeviceName.DeviceInfo info, Exception error) {
                if (error != null) {
                    tvDeviceName.setText(context.getString(R.string.devicename_error));
                    Log.e("DeviceFragment", "DeviceName error", error);
                } else {
                    tvDeviceName.setText(info.marketName);
                }
            }
        });
        tvModel.setText(Build.MODEL);
        tvManufacturer.setText(Build.MANUFACTURER);
        tvDeviceD.setText(Build.DEVICE);
        tvBoard.setText(Build.MODEL);
        tvHardware.setText(Build.HARDWARE);
        tvBrand.setText(Build.BRAND);
        @SuppressLint("HardwareIds")
        String androidId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        tvAndroidDeviceID.setText(androidId);
        tvBuildFingerprint.setText(Build.FINGERPRINT);
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
    }
}