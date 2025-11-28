package com.example.diagsmartv2;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.icu.util.TimeZone;
import android.media.MediaDrm;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SystemFragment extends Fragment {

    TextView tvAndroidVersion, tvVersionName, tvReleasedDate, tvCodeName, tvApiLevel,
            tvReleasedWith, tvSecurityPatch, tvBootloader, tvBuildNumber, tvBaseband,
            tvJavaVM, tvKernel, tvLanguage, tvTimezone, tvOpenGL, tvRootManag, tvSELinux,
            tvGooglePlay, tvSystemUptime, tvVulkan, tvTreble, tvSeamless, tvDynamicPartitions,
            tvVendor, tvVersion, tvDescription, tvAlgorithms, tvSecurityLevel, tvMaxHDPC;

    // ROOT_APPS array contains package names of known root management apps for Android.
    // The purpose is to check if any of these root-related apps are installed on the device.
    private static final String[] ROOT_APPS = {
            "com.noshufou.android.su.elite",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.thirdparty.superuser",
            "com.yellowes.su",
            "com.topjohnwu.magisk",
            "com.kingroot.kinguser",
            "com.kingo.root",
            "com.smedialink.oneclickroot",
            "com.zhiqupk.root.global",
            "com.alephzain.framaroot"
    };

    private Map<Integer, String> RELEASE_DATES;

    Context context;


    public SystemFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_system, container, false);

        initReferences(view);
        context = requireContext();
        initReleaseDates();
        setSystemInfo();

        return view;
    }

    private void initReferences(View view) {
        tvAndroidVersion = view.findViewById(R.id.tvAndroidVersion);
        tvVersionName = view.findViewById(R.id.tvVersionName);
        tvReleasedDate = view.findViewById(R.id.tvReleasedDate);
        tvCodeName = view.findViewById(R.id.tvCodeName);
        tvApiLevel = view.findViewById(R.id.tvApiLevel);
        tvReleasedWith = view.findViewById(R.id.tvReleasedWith);
        tvSecurityPatch = view.findViewById(R.id.tvSecurityPatchDate);
        tvBootloader = view.findViewById(R.id.tvBootLoader);
        tvBuildNumber = view.findViewById(R.id.tvBuildNumber);
        tvBaseband = view.findViewById(R.id.tvBaseband);
        tvJavaVM = view.findViewById(R.id.tvJavaVM);
        tvKernel = view.findViewById(R.id.tvKernel);
        tvLanguage = view.findViewById(R.id.tvLanguage);
        tvTimezone = view.findViewById(R.id.tvTimezone);
        tvRootManag = view.findViewById(R.id.tvRootManag);
        tvOpenGL = view.findViewById(R.id.tvOpenGL);
        tvSELinux = view.findViewById(R.id.tvSELinux);
        tvGooglePlay = view.findViewById(R.id.tvGooglePlay);
        tvSystemUptime = view.findViewById(R.id.tvSystemUptime);
        tvVulkan = view.findViewById(R.id.tvVulkan);
        tvTreble = view.findViewById(R.id.tvTreble);
        tvSeamless = view.findViewById(R.id.tvSeamless);
        tvDynamicPartitions = view.findViewById(R.id.tvDynamicPartitions);
        tvVendor = view.findViewById(R.id.tvVendor);
        tvVersion = view.findViewById(R.id.tvVersion);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvAlgorithms = view.findViewById(R.id.tvAlgorithms);
        tvSecurityLevel = view.findViewById(R.id.tvSecurityLevel);
        tvMaxHDPC = view.findViewById(R.id.tvMaxHDCP);

    }

    @SuppressLint("SetTextI18n")
    private void setSystemInfo() {
        String androidVersion = android.os.Build.VERSION.RELEASE;
        String codeName = android.os.Build.VERSION.CODENAME;
        String text = "Android " + androidVersion + ".0  - " + codeName;
        int sdkVersion = Build.VERSION.SDK_INT;
        String versionName = Build.VERSION.RELEASE;
        String releaseDate = getReleaseDate(sdkVersion);
        String codeNameString = "Android " + Build.VERSION.CODENAME;
        String screenType = getDeviceScreenType();
        String javaVMVersion = java.lang.System.getProperty("java.vm.version");
        String kernel = java.lang.System.getProperty("os.version");
        String tz = getTimezone();
        String timezone = TimeZone.getDefault().getDisplayName(false, TimeZone.LONG, Locale.ENGLISH);


        tvVersionName.setText(versionName);
        tvReleasedDate.setText(releaseDate);
        tvAndroidVersion.setText(text);
        tvCodeName.setText(codeNameString);
        tvApiLevel.setText(String.valueOf(sdkVersion));
        tvReleasedWith.setText(screenType);
        tvSecurityPatch.setText(Build.VERSION.SECURITY_PATCH);
        tvBootloader.setText(Build.BOOTLOADER);
        tvBuildNumber.setText(Build.DISPLAY);
        tvBaseband.setText(Build.getRadioVersion());
        tvJavaVM.setText(javaVMVersion);
        tvKernel.setText(kernel);
        tvLanguage.setText(getLanguage());
        tvTimezone.setText(tz + " (" + timezone + ")");
        tvRootManag.setText(getRootManagementApps());
        tvOpenGL.setText(getOpenGLESVersion());
        tvSELinux.setText(getSELinuxStatus());
        tvGooglePlay.setText(getPlayServicesVersion());
        tvSystemUptime.setText(getSystemUptime());
        tvVulkan.setText(getVulkanSupport());
        tvTreble.setText(getTrebleSupport());
        tvSeamless.setText(getSeamlessUpdatesSupport());
        tvDynamicPartitions.setText(getDynamicPartitionsSupport());
        tvVendor.setText(getDrmVendor());
        tvVersion.setText(getDrmVersion());
        tvDescription.setText(getDrmDescription());
        tvAlgorithms.setText(getDrmAlgorithms());
        tvSecurityLevel.setText(getDrmSecurityLevel());
        tvMaxHDPC.setText(getMaxHdcpLevel());

    }

    private void initReleaseDates() {
        RELEASE_DATES = new HashMap<>();
        RELEASE_DATES.put(14, context.getString(R.string.release_14));
        RELEASE_DATES.put(16, context.getString(R.string.release_16));
        RELEASE_DATES.put(19, context.getString(R.string.release_19));
        RELEASE_DATES.put(21, context.getString(R.string.release_21));
        RELEASE_DATES.put(23, context.getString(R.string.release_23));
        RELEASE_DATES.put(24, context.getString(R.string.release_24));
        RELEASE_DATES.put(26, context.getString(R.string.release_26));
        RELEASE_DATES.put(28, context.getString(R.string.release_28));
        RELEASE_DATES.put(29, context.getString(R.string.release_29));
        RELEASE_DATES.put(30, context.getString(R.string.release_30));
        RELEASE_DATES.put(31, context.getString(R.string.release_31));
        RELEASE_DATES.put(33, context.getString(R.string.release_33));
        RELEASE_DATES.put(34, context.getString(R.string.release_34));
        RELEASE_DATES.put(35, context.getString(R.string.release_35));
        RELEASE_DATES.put(36, context.getString(R.string.release_36));
    }

    public String getReleaseDate(int sdkVersion) {
        if (RELEASE_DATES != null && RELEASE_DATES.containsKey(sdkVersion)) {
            return context.getString(R.string.released_prefix) + RELEASE_DATES.get(sdkVersion);
        } else {
            return context.getString(R.string.selinux_unable); // o "Release date unknown" que prefieras
        }
    }

    public String getDeviceScreenType() {
        int screenSize = context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK;

        String screenType;
        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                screenType = context.getString(R.string.screen_large);
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                screenType = context.getString(R.string.screen_normal);
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                screenType = context.getString(R.string.screen_small);
                break;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                screenType = context.getString(R.string.screen_xlarge);
                break;
            default:
                screenType = context.getString(R.string.screen_undefined);
        }
        return screenType;
    }


    public String getLanguage() {
        Locale locale = Locale.getDefault();
        String languageName = locale.getDisplayLanguage(locale);
        String languageCode = locale.getLanguage();
        return languageName + " (" + languageCode + ")";
    }


    public String getTimezone() {
        String zone;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ZoneId zoneId = ZoneId.systemDefault();
            zone = zoneId.getId();
        } else {
            TimeZone tz = TimeZone.getDefault();
            zone = tz.getID();
        }

        return zone;
    }

    public String getOpenGLESVersion() {
        ActivityManager activityManager =
                (ActivityManager) requireContext().getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        return configurationInfo.getGlEsVersion();
    }


    public String getRootManagementApps() {
        StringBuilder detectedApps = new StringBuilder();
        PackageManager pm = context.getPackageManager();

        for (String appPackage : ROOT_APPS) {
            try {
                pm.getPackageInfo(appPackage, PackageManager.GET_ACTIVITIES);
                if (detectedApps.length() > 0) {
                    detectedApps.append(", ");
                }
                detectedApps.append(appPackage);
            } catch (PackageManager.NameNotFoundException e) {
                Log.d("RootDetector", "App not found: " + appPackage);
            }
        }
        return detectedApps.length() > 0 ? detectedApps.toString() : context.getString(R.string.no_apps_detected);
    }

    public String getSELinuxStatus() {
        String status = context.getString(R.string.selinux_unable);

        try {
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", "getenforce"});
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            status = reader.readLine();
            reader.close();
        } catch (IOException e) {
            Log.e("SystemFragment", "Error getting SELinux status", e);
        }

        if (status == null || status.isEmpty()) {
            try {
                File file = new File("/sys/fs/selinux/enforce");
                if (file.exists()) {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line = reader.readLine();
                    reader.close();

                    if ("0".equals(line)) {
                        status = "Permissive";
                    } else if ("1".equals(line)) {
                        status = "Enforcing";
                    }
                }
            } catch (IOException e) {
                Log.e("SystemFragment", "Error getting SELinux status", e);
            }
        }
        return status;
    }

    public String getPlayServicesVersion() {
        String playServicesVersion = "";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo("com.google.android.gms", 0);
            playServicesVersion = pInfo.versionName + " (" + pInfo.versionCode + ")";
        } catch (PackageManager.NameNotFoundException e) {
            playServicesVersion = context.getString(R.string.not_installed);
        }
        return playServicesVersion;
    }

    public String getSystemUptime() {
        try {
            long elapsedMillis = SystemClock.elapsedRealtime();

            long hours = TimeUnit.MILLISECONDS.toHours(elapsedMillis);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedMillis) % 60;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) % 60;

            return String.format(Locale.UK, "%02d:%02d:%02d", hours, minutes, seconds);
        } catch (Exception e) {
            Log.e("SystemFragment", "Error getting system Uptime", e);
            return context.getString(R.string.unable_to_determine);
        }
    }

    public String getVulkanSupport() {
        PackageManager pm = context.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_VULKAN_HARDWARE_VERSION)) {
            FeatureInfo[] features = pm.getSystemAvailableFeatures();
            for (FeatureInfo feature : features) {
                if (PackageManager.FEATURE_VULKAN_HARDWARE_VERSION.equals(feature.name)) {
                    int version = feature.version;
                    int major = (version >> 22) & 0x3FF;
                    int minor = (version >> 12) & 0x3FF;
                    return String.format(Locale.UK, context.getString(R.string.supported_version_format), major, minor);
                }
            }
        }
        return context.getString(R.string.not_supported);
    }

    public String getTrebleSupport() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return context.getString(R.string.supported);
        } else {
            return context.getString(R.string.not_supported);
        }
    }

    public String getSeamlessUpdatesSupport() {
        if (Build.SUPPORTED_ABIS.length > 1 && Build.SUPPORTED_ABIS[0].equals("arm64-v8a")) {
            return context.getString(R.string.supported);
        }
        return context.getString(R.string.not_supported);
    }

    public String getDynamicPartitionsSupport() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                return context.getString(R.string.supported);
            } else {
                return context.getString(R.string.potentially_supported);
            }
        }
        return context.getString(R.string.not_supported);
    }

    public String getDrmVendor() {
        String vendor = "";
        UUID WIDEVINE_UUID = new UUID(0xEDEF8BA979D64ACEL, 0xA3C827DCD51D21EDL);
        try (MediaDrm mediaDrm = new MediaDrm(WIDEVINE_UUID)) {
            vendor = mediaDrm.getPropertyString(MediaDrm.PROPERTY_VENDOR);
        } catch (Exception e) {
            Log.e("SystemFragment", "Error getting Drm vendor", e);
        }
        return vendor;
    }

    public String getDrmVersion() {
        String version = "";
        UUID WIDEVINE_UUID = new UUID(0xEDEF8BA979D64ACEL, 0xA3C827DCD51D21EDL);
        try (MediaDrm mediaDrm = new MediaDrm(WIDEVINE_UUID)) {
            version = mediaDrm.getPropertyString(MediaDrm.PROPERTY_VERSION);
        } catch (Exception e) {
            Log.e("SystemFragment", "Error getting Drm version", e);
        }
        return version;
    }

    public String getDrmDescription() {
        String description = "";
        UUID WIDEVINE_UUID = new UUID(0xEDEF8BA979D64ACEL, 0xA3C827DCD51D21EDL);
        try (MediaDrm mediaDrm = new MediaDrm(WIDEVINE_UUID)) {
            description = mediaDrm.getPropertyString(MediaDrm.PROPERTY_DESCRIPTION);
        } catch (Exception e) {
            Log.e("SystemFragment", "Error getting Drm description", e);
        }
        return description;
    }

    public String getDrmAlgorithms() {
        String algorithms = "";
        UUID WIDEVINE_UUID = new UUID(0xEDEF8BA979D64ACEL, 0xA3C827DCD51D21EDL);
        try (MediaDrm mediaDrm = new MediaDrm(WIDEVINE_UUID)) {
            algorithms = mediaDrm.getPropertyString(MediaDrm.PROPERTY_ALGORITHMS);
        } catch (Exception e) {
            Log.e("SystemFragment", "Error getting Drm algorithms", e);
        }
        return algorithms;
    }

    public String getDrmSecurityLevel() {
        String securityLevel = context.getString(R.string.unknown);
        UUID WIDEVINE_UUID = new UUID(0xEDEF8BA979D64ACEL, 0xA3C827DCD51D21EDL);
        try (MediaDrm mediaDrm = new MediaDrm(WIDEVINE_UUID)) {
            String securityLevelStr = mediaDrm.getPropertyString("securityLevel");
            switch (securityLevelStr) {
                case "L1":
                    securityLevel = "L1";
                    break;
                case "L2":
                    securityLevel = "L2";
                    break;
                case "L3":
                    securityLevel = "L3";
                    break;
            }
        } catch (Exception e) {
            Log.e("SystemFragment", "Error getting security level", e);
        }
        return securityLevel;
    }

    public String getMaxHdcpLevel() {
        String maxHdcpLevel = context.getString(R.string.unknown);
        UUID WIDEVINE_UUID = new UUID(0xEDEF8BA979D64ACEL, 0xA3C827DCD51D21EDL);
        try (MediaDrm mediaDrm = new MediaDrm(WIDEVINE_UUID)) {
            String hdcpLevel = mediaDrm.getPropertyString("maxHdcpLevel");

            switch (hdcpLevel) {
                case "0":
                    maxHdcpLevel = context.getString(R.string.no_digital_output);
                    break;
                case "1":
                    maxHdcpLevel = context.getString(R.string.hdcp_1_0);
                    break;
                case "2":
                    maxHdcpLevel = context.getString(R.string.hdcp_2_0);
                    break;
                case "2.1":
                    maxHdcpLevel = context.getString(R.string.hdcp_2_1);
                    break;
                case "2.2":
                    maxHdcpLevel = context.getString(R.string.hdcp_2_2);
                    break;
                case "2.3":
                    maxHdcpLevel = context.getString(R.string.hdcp_2_3);
                    break;
                default:
                    maxHdcpLevel = context.getString(R.string.unknown) + " (" + hdcpLevel + ")";
            }
        } catch (Exception e) {
            Log.e("SystemFragment", "Error getting max Hdcp level", e);
        }
        return maxHdcpLevel;
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