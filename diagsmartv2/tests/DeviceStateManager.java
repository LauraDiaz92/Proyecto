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

    public boolean isRestartNeeded() {
        long uptime = SystemClock.elapsedRealtime() / 1000; // Time in seconds since the last restart
        long daysSinceBoot = uptime / (60 * 60 * 24); // Days since the last restart

        return daysSinceBoot > 3;
    }

    public boolean isUsbDebuggingEnabled() {
        return Settings.Global.getInt(context.getContentResolver(), Settings.Global.ADB_ENABLED, 0) == 1;
    }

    public boolean isScreenBrightnessOptimal() {
        int screenBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
        int batteryLevel = getBatteryLevel();

        if (batteryLevel < 80) {
            return screenBrightness < 128;
        } else {
            return screenBrightness < 192;
        }
    }

    private int getBatteryLevel() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        assert batteryStatus != null;
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        return (int) (((float) level / scale) * 100);
    }

    public boolean isScreenTimeoutOptimal() {
        int timeout = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 0);

        return timeout <= 30000; // 30000 miliseconds = 30 seconds
    }

    public boolean isScreenLockConfigured() {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);

        return keyguardManager != null && keyguardManager.isKeyguardSecure();
    }

    public boolean isNfcEnabled() {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);

        return nfcAdapter != null && nfcAdapter.isEnabled();
    }

    public boolean isWifiEnabled() {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        return wifiManager != null && wifiManager.isWifiEnabled();
    }
}

