package com.example.diagsmartv2.tests;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diagsmartv2.R;

public class TestsAutomaticAdapter extends RecyclerView.Adapter<TestsAutomaticAdapter.TestAutomaticViewHolder> {

    private final String[] automaticTestsTitles = {
            "Last Restart",
            "USB Debugging",
            "Screen Brightness",
            "Screen Timeout",
            "Screen Lock",
            "NFC",
            "Wi-Fi"
    };

    private final DeviceStateManager deviceStateManager;
    private final OnTestResultListener onTestResultListener;

    public interface OnTestResultListener {
        void onTestResultsChanged(boolean anyTestFailed);
    }

    public TestsAutomaticAdapter(Context context, OnTestResultListener listener) {
        this.deviceStateManager = new DeviceStateManager(context);
        this.onTestResultListener = listener;
    }

    @NonNull
    @Override
    public TestAutomaticViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tests_list, parent, false);
        return new TestAutomaticViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestAutomaticViewHolder holder, int position) {
        holder.tvTestName.setText(automaticTestsTitles[position]);

        boolean restartNeeded = deviceStateManager.isRestartNeeded();
        boolean usbDebuggingEnabled = deviceStateManager.isUsbDebuggingEnabled();
        boolean screenBrightnessOptimal = deviceStateManager.isScreenBrightnessOptimal();
        boolean screenTimeoutOptimal = deviceStateManager.isScreenTimeoutOptimal();
        boolean screenLockConfigured = deviceStateManager.isScreenLockConfigured();
        boolean nfcEnabled = deviceStateManager.isNfcEnabled();
        boolean wifiEnabled = deviceStateManager.isWifiEnabled();

        boolean testFailed = false;

        switch (automaticTestsTitles[position]) {
            case "Last Restart":
                testFailed = restartNeeded;
                holder.tvTestAutomaticInfo.setText(restartNeeded ?
                        "It has been more than 3 days since you restarted your device. Restart your device for best performance." :
                        "Your device was restarted recently.");
                holder.ivTestIcon.setImageResource(restartNeeded ?
                        R.drawable.baseline_cancel_24 :
                        R.drawable.baseline_check_circle_24);
                break;

            case "USB Debugging":
                testFailed = usbDebuggingEnabled;
                holder.tvTestAutomaticInfo.setText(usbDebuggingEnabled ?
                        "USB Debugging is enabled. It is recommended to disable USB Debugging" :
                        "USB Debugging is disabled");
                holder.ivTestIcon.setImageResource(usbDebuggingEnabled ?
                        R.drawable.baseline_cancel_24 :
                        R.drawable.baseline_check_circle_24);
                break;

            case "Screen Brightness":
                testFailed = !screenBrightnessOptimal;
                holder.tvTestAutomaticInfo.setText(screenBrightnessOptimal ?
                        "Your screen brightness is OK" :
                        "Reduce your screen brightness to save battery");
                holder.ivTestIcon.setImageResource(screenBrightnessOptimal ?
                        R.drawable.baseline_check_circle_24 :
                        R.drawable.baseline_cancel_24);
                break;

            case "Screen Timeout":
                testFailed = !screenTimeoutOptimal;
                holder.tvTestAutomaticInfo.setText(screenTimeoutOptimal ?
                        "Your screen timeout is OK" :
                        "Recommended screen timeout is 30 seconds or lower");
                holder.ivTestIcon.setImageResource(screenTimeoutOptimal ?
                        R.drawable.baseline_check_circle_24 :
                        R.drawable.baseline_cancel_24);
                break;

            case "Screen Lock":
                testFailed = !screenLockConfigured;
                holder.tvTestAutomaticInfo.setText(screenLockConfigured ?
                        "Screen lock is configured successfully" :
                        "You should configure a screen lock for better security");
                holder.ivTestIcon.setImageResource(screenLockConfigured ?
                        R.drawable.baseline_check_circle_24 :
                        R.drawable.baseline_cancel_24);
                break;

            case "NFC":
                testFailed = nfcEnabled;
                holder.tvTestAutomaticInfo.setText(nfcEnabled ?
                        "It is recommended to turn off NFC when not in use to save battery" :
                        "NFC is currently disabled. Enable it if you need to use NFC services");
                holder.ivTestIcon.setImageResource(nfcEnabled ?
                        R.drawable.baseline_cancel_24 :
                        R.drawable.baseline_check_circle_24);
                break;

            case "Wi-Fi":
                testFailed = wifiEnabled;
                holder.tvTestAutomaticInfo.setText(wifiEnabled ?
                        "Wi-Fi is enabled. Turn it off if not needed to save battery" :
                        "Wi-Fi is disabled");
                holder.ivTestIcon.setImageResource(wifiEnabled ?
                        R.drawable.baseline_cancel_24 :
                        R.drawable.baseline_check_circle_24);
                break;
        }

        // Notify the listener if any test failed
        if (onTestResultListener != null) {
            onTestResultListener.onTestResultsChanged(testFailed);
        }
    }

    @Override
    public int getItemCount() {
        return automaticTestsTitles.length;
    }

    public static class TestAutomaticViewHolder extends RecyclerView.ViewHolder {
        TextView tvTestName, tvTestAutomaticInfo;
        ImageView ivTestIcon;

        public TestAutomaticViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTestName = itemView.findViewById(R.id.tvTestName);
            tvTestAutomaticInfo = itemView.findViewById(R.id.tvTestAutomaticInfo);
            ivTestIcon = itemView.findViewById(R.id.ivTestIcon);
        }
    }
}


