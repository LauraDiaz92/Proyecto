package com.example.diagsmartv2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

public class DisplayFragment extends Fragment {

    TextView tvResolution, tvDensity, tvFontScale, tvPhysicalSize, tvRefreshRate,
             tvHDR, tvHDRCapabilities, tvBrightness, tvBrightnessMode,
             tvScreenTimeout, tvOrientation, tvOrientationScreen, tvPixels, tvInchHz;

    Context context;

    public DisplayFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display, container, false);

        initReferences(view);
        context = requireContext();
        getScreenResolution();
        getOrientation();
        getDensity();
        getFontScale();
        getScreenSizeInches();
        getRefreshRate();
        getHDRStatus();
        getBrightness();
        getScreenTimeout();

        return view;
    }

    /**
     * Finds and stores references to all TextViews used to display display parameters.
     *
     * @param view root view that contains the UI components.
     */
    private void initReferences(View view) {
        tvResolution = view.findViewById(R.id.tvResolution);
        tvDensity = view.findViewById(R.id.tvDensity);
        tvFontScale = view.findViewById(R.id.tvFontScale);
        tvPhysicalSize = view.findViewById(R.id.tvPhysicalSize);
        tvRefreshRate = view.findViewById(R.id.tvRefreshRate);
        tvHDR = view.findViewById(R.id.tvHDR);
        tvHDRCapabilities = view.findViewById(R.id.tvHDRCapabilities);
        tvBrightness = view.findViewById(R.id.tvBrightness);
        tvBrightnessMode = view.findViewById(R.id.tvBrightnessMode);
        tvScreenTimeout = view.findViewById(R.id.tvScreenTimeout);
        tvOrientationScreen = view.findViewById(R.id.tvOrientationScreen);
        tvOrientation = view.findViewById(R.id.tvOrientation);
        tvPixels = view.findViewById(R.id.tvPixels);
        tvInchHz = view.findViewById(R.id.tvIncHz);
    }

    /**
     * Calculates the real screen resolution, classifies it (HD, FHD, QHD, etc.),
     * and displays the result with the pixel count.
     */
    public void getScreenResolution() {
        WindowManager windowManager = (WindowManager) requireContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(realDisplayMetrics);

        int width = realDisplayMetrics.widthPixels;
        int height = realDisplayMetrics.heightPixels;
        float aspectRatio = (float) Math.max(width, height) / Math.min(width, height);

        String resolution = width + " x " + height + context.getString(R.string.pixels);
        String displayType = "";

        if (width >= 3840 || height >= 3840) {
            displayType = "4K UHD+";
        } else if (width >= 3200 || height >= 3200) {
            displayType = "WQHD+";
        } else if (width >= 2560 || height >= 2560) {
            displayType = "QHD+";
        } else if (width >= 1920 || height >= 1920) {
            if (aspectRatio >= 2.1f) {
                displayType = "FHD+";
            } else {
                displayType = "FHD";
            }
        } else if (width >= 1280 || height >= 1280) {
            if (aspectRatio >= 2.0f) {
                displayType = "HD+";
            } else {
                displayType = "HD";
            }
        } else {
            displayType = "SD";
        }

        resolution += " (" + displayType + ")";

        tvPixels.setText(resolution);
        tvResolution.setText(resolution);
    }

    /**
     * Detects the current screen orientation (portrait or landscape) and shows it.
     */
    public void getOrientation() {
        int orientation = getResources().getConfiguration().orientation;
        String orientationText;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            orientationText = context.getString(R.string.portrait);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            orientationText = context.getString(R.string.landscape);
        } else {
            orientationText = context.getString(R.string.unknown);
        }

        tvOrientation.setText(orientationText);
        tvOrientationScreen.setText(orientationText);
    }

    /**
     * Retrieves the screen density in dpi, maps it to the density bucket
     * (LDPI, MDPI, HDPI, etc.) and displays both.
     */
    public void getDensity() {
        WindowManager windowManager = (WindowManager) requireContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int densityDpi = displayMetrics.densityDpi;
        String densityBucket;

        if (densityDpi <= DisplayMetrics.DENSITY_LOW) {
            densityBucket = "LDPI";
        } else if (densityDpi <= DisplayMetrics.DENSITY_MEDIUM) {
            densityBucket = "MDPI";
        } else if (densityDpi <= DisplayMetrics.DENSITY_HIGH) {
            densityBucket = "HDPI";
        } else if (densityDpi <= DisplayMetrics.DENSITY_XHIGH) {
            densityBucket = "XHDPI";
        } else if (densityDpi <= DisplayMetrics.DENSITY_XXHIGH) {
            densityBucket = "XXHDPI";
        } else {
            densityBucket = "XXXHDPI";
        }

        String densityInfo = densityDpi + " dpi (" + densityBucket + ")";
        tvDensity.setText(densityInfo);
    }

    /**
     * Reads the current system font scale factor and displays it as a decimal value.
     */
    public void getFontScale() {
        float fontScale = getResources().getConfiguration().fontScale;
        @SuppressLint("DefaultLocale") String formattedScale = String.format("%.1f", fontScale);
        tvFontScale.setText(formattedScale);
    }

    /**
     * Computes the physical screen size in inches from pixel and dpi values,
     * updates the corresponding label and returns the formatted size string.
     *
     * @return formatted screen size in inches.
     */
     public String getScreenSizeInches() {
        WindowManager windowManager = (WindowManager) requireContext().getSystemService(Context.WINDOW_SERVICE);
        android.view.Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getRealMetrics(displayMetrics);

        double widthInches = displayMetrics.widthPixels / displayMetrics.xdpi;
        double heightInches = displayMetrics.heightPixels / displayMetrics.ydpi;
        double diagonalInches = Math.sqrt(Math.pow(widthInches, 2) + Math.pow(heightInches, 2));

        // Redondear a un decimal
        diagonalInches = Math.round(diagonalInches * 10.0) / 10.0;

        @SuppressLint("DefaultLocale") String screenSize = String.format("%.1f inches", diagonalInches);

        tvPhysicalSize.setText(screenSize);
        return screenSize;
     }

    /**
     * Reads the current display refresh rate in hertz and shows it, together with
     * the physical screen size, in a combined label.
     */
     @SuppressLint("SetTextI18n")
     public void getRefreshRate() {
        WindowManager windowManager = (WindowManager) requireContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        float refreshRate = display.getRefreshRate();
        @SuppressLint("DefaultLocale") String formattedRate = String.format("%.1f Hz", refreshRate);
        tvRefreshRate.setText(formattedRate);
        tvInchHz.setText(getScreenSizeInches() + " | " + formattedRate);
     }

    /**
     * Checks whether the display reports HDR support and lists the supported HDR
     * formats such as HDR10, HLG or Dolby Vision.
     */
     public void getHDRStatus() {
        String hdrStatus;
        String hdrCapabilitiesString = "None";

        WindowManager windowManager = (WindowManager) requireContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Display.HdrCapabilities hdrCapabilities = display.getHdrCapabilities();

        if (hdrCapabilities != null && hdrCapabilities.getSupportedHdrTypes().length > 0) {
            hdrStatus = "Supported";

            int[] hdrTypes = hdrCapabilities.getSupportedHdrTypes();
            StringBuilder capabilitiesBuilder = new StringBuilder();

            for (int i = 0; i < hdrTypes.length; i++) {
                if (i > 0) {
                    capabilitiesBuilder.append(", ");
                }
                switch (hdrTypes[i]) {
                    case Display.HdrCapabilities.HDR_TYPE_DOLBY_VISION:
                        capabilitiesBuilder.append("Dolby Vision");
                        break;
                    case Display.HdrCapabilities.HDR_TYPE_HDR10:
                        capabilitiesBuilder.append("HDR10");
                        break;
                    case Display.HdrCapabilities.HDR_TYPE_HLG:
                        capabilitiesBuilder.append("HLG");
                        break;
                    case Display.HdrCapabilities.HDR_TYPE_HDR10_PLUS:
                        capabilitiesBuilder.append("HDR10+");
                        break;
                    default:
                        capabilitiesBuilder.append(context.getString(R.string.unknown));
                        break;
                }
            }

            hdrCapabilitiesString = capabilitiesBuilder.toString();
        } else {
            hdrStatus = context.getString(R.string.not_supported);;
        }

        tvHDR.setText(hdrStatus);
        tvHDRCapabilities.setText(hdrCapabilitiesString);
     }

    /**
     * Reads the current screen brightness and mode (manual or automatic),
     * converts brightness to a percentage and displays both values.
     */
     @SuppressLint("DefaultLocale")
     public void getBrightness() {
        int brightness = 0;
        int brightnessMode = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;
        String brightnessModeName = context.getString(R.string.manual);

        try {
            brightness = Settings.System.getInt(requireContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            brightnessMode = Settings.System.getInt(requireContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("DisplayFragment", "Error getting screen brightness");
        }

        if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
            brightnessModeName = context.getString(R.string.automatic);
        }

        int maxBrightness = 255;
        int brightnessPercentage = (int) ((brightness / (float) maxBrightness) * 100);

        String formattedBrightness = String.format(context.getString(R.string.percentage_format), brightnessPercentage);
        tvBrightness.setText(formattedBrightness);

        tvBrightnessMode.setText(brightnessModeName);
     }

    /**
     * Reads the screen‑off timeout setting, converts it to seconds and displays it.
     */
     @SuppressLint("DefaultLocale")
     public void getScreenTimeout() {
        int timeoutMillis = 0;
        try {
            timeoutMillis = Settings.System.getInt(requireContext().getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("DisplayFragment", "Error getting screen timeout");
        }

        int timeoutSeconds = timeoutMillis / 1000;
        String formattedTimeout = String.format(context.getString(R.string.seconds_format), timeoutSeconds);

        tvScreenTimeout.setText(formattedTimeout);
     }

    /**
     * Registers a back‑press callback to navigate from the display screen
     * back to the dashboard when this fragment is visible.
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