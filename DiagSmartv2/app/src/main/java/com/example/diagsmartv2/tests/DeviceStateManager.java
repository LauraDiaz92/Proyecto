package com.example.diagsmartv2.tests;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.os.BatteryManager;
import android.os.SystemClock;
import android.provider.Settings;

public class DeviceStateManager {

    private final Context context;

    public DeviceStateManager(Context context) {
        this.context = context;
    }

    /**
     * Checks whether the device has been running for more than three days
     * since the last reboot and suggests a restart if so.
     *
     * @return true if uptime exceeds three days, false otherwise.
     */
    public boolean isRestartNeeded() {
        long uptime = SystemClock.elapsedRealtime() / 1000; // Time in seconds since the last restart
        long daysSinceBoot = uptime / (60 * 60 * 24); // Days since the last restart

        return daysSinceBoot > 3;
    }

    /**
     * Indicates whether USB debugging (ADB over USB) is currently enabled
     * in global system settings.
     *
     * @return true if ADB is enabled, false otherwise.
     */
    public boolean isUsbDebuggingEnabled() {
        return Settings.Global.getInt(context.getContentResolver(), Settings.Global.ADB_ENABLED, 0) == 1;
    }

    /**
     * Evaluates if the current screen brightness is within an “optimal” range
     * depending on the battery level: stricter when battery is below 80%.
     *
     * @return true if brightness is considered optimal, false otherwise.
     */
    public boolean isScreenBrightnessOptimal() {
        int screenBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
        int batteryLevel = getBatteryLevel();

        if (batteryLevel < 80) {
            return screenBrightness < 128;
        } else {
            return screenBrightness < 192;
        }
    }

    /**
     * Reads the current battery level as a percentage using the sticky
     * BATTERY_CHANGED broadcast.
     *
     * @return battery percentage from 0 to 100.
     */
    private int getBatteryLevel() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        assert batteryStatus != null;
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        return (int) (((float) level / scale) * 100);
    }

    /**
     * Checks whether the screen‑off timeout is at or below 30 seconds.
     *
     * @return true if timeout ≤ 30 000 ms, false otherwise.
     */
    public boolean isScreenTimeoutOptimal() {
        int timeout = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 0);

        return timeout <= 30000; // 30 seconds
    }

    /**
     * Returns whether the device has a secure lock screen configured
     * (PIN, pattern, password or locked SIM).
     *
     * @return true if a secure keyguard is configured, false otherwise.
     */
    public boolean isScreenLockConfigured() {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);

        return keyguardManager != null && keyguardManager.isKeyguardSecure();
    }

    /**
     * Indicates whether NFC hardware is present and currently enabled.
     *
     * @return true if NFC is available and turned on, false otherwise.
     */
    public boolean isNfcEnabled() {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);

        return nfcAdapter != null && nfcAdapter.isEnabled();
    }

    /**
     * Indicates whether Wi‑Fi is available and currently enabled.
     *
     * @return true if Wi‑Fi is enabled, false otherwise.
     */
    public boolean isWifiEnabled() {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        return wifiManager != null && wifiManager.isWifiEnabled();
    }
}

