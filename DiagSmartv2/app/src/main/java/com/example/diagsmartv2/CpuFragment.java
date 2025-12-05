package com.example.diagsmartv2;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class CpuFragment extends Fragment {

    TextView tvCortex, tvSocInfo, tvProcessor, tvArchitecture, tvSupportedABIs,
            tvCPUHardware, tvType, tvGovernor, tvCores, tvCPUFrequency, tvRenderer,
            tvVendor, tvVersion;

    Context context;

    public CpuFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cpu, container, false);

        initReferences(view);
        context = requireContext();
        getCortexInfo();
        getSoCInfo();
        getProcessor();
        getCPUArchitecture();
        getSupportedABIs();
        getCPUHardware();
        getCPUType();
        getCPUGovernor();
        getNumberOfCores();
        getCPUFrequencies();
        getGPURenderer();
        getGPUVendor();
        getGPUVersion();

        return view;
    }

    /**
     * Finds and stores references to all CPU and GPU TextViews in the layout.
     *
     * @param view root view that contains the UI components.
     */
    private void initReferences(View view) {
        tvCortex = view.findViewById(R.id.tvCortex);
        tvSocInfo = view.findViewById(R.id.tvSocInfo);
        tvProcessor = view.findViewById(R.id.tvProcessor);
        tvArchitecture = view.findViewById(R.id.tvCPUArchitecture);
        tvSupportedABIs = view.findViewById(R.id.tvSupportedABIs);
        tvCPUHardware = view.findViewById(R.id.tvCPUHardware);
        tvType = view.findViewById(R.id.tvCPUType);
        tvGovernor = view.findViewById(R.id.tvCPUGovernor);
        tvCores = view.findViewById(R.id.tvCores);
        tvCPUFrequency = view.findViewById(R.id.tvCPUFrequency);
        tvRenderer = view.findViewById(R.id.tvGPURenderer);
        tvVendor = view.findViewById(R.id.tvGPUVendor);
        tvVersion = view.findViewById(R.id.tvGPUVersion);
    }

    /**
     * Parses /proc/cpuinfo to group cores by Cortex type and maximum frequency,
     * then displays a summary such as "4x Cortex‑A78 (2.8GHz)".
     */
    @SuppressLint("DefaultLocale")
    public void getCortexInfo() {
        StringBuilder result = new StringBuilder();
        Map<String, int[]> cortexCount = new HashMap<>(); // [count, maxFreq]

        try (BufferedReader br = new BufferedReader(new FileReader("/proc/cpuinfo"))) {
            Pattern partPattern = Pattern.compile("CPU part\\s*:?\\s*(0x[\\da-fA-F]+)", Pattern.CASE_INSENSITIVE);
            String line;
            String currentCpuPart = null;
            int coreNumber = 0;

            while ((line = br.readLine()) != null) {
                Matcher partMatcher = partPattern.matcher(line);
                if (partMatcher.find()) {
                    currentCpuPart = Objects.requireNonNull(partMatcher.group(1)).toLowerCase();
                    String cortexType = getCortexType(currentCpuPart);
                    int maxFreq = getMaxFrequency(coreNumber);

                    cortexCount.compute(cortexType, (k, v) -> {
                        if (v == null) {
                            return new int[]{1, maxFreq};
                        } else {
                            v[0]++; // Increment count
                            v[1] = Math.max(v[1], maxFreq); // Update max frequency
                            return v;
                        }
                    });

                    coreNumber++;
                }
            }

            cortexCount.entrySet().stream()
                    .sorted((e1, e2) -> Integer.compare(e2.getValue()[0], e1.getValue()[0]))
                    .limit(3) // Limitar a 3 entradas como máximo
                    .forEach(entry -> {
                        double ghz = entry.getValue()[1] / 1000.0;
                        result.append(entry.getValue()[0])
                                .append("x ")
                                .append(entry.getKey())
                                .append(String.format(" (%.1fGHz)", ghz))
                                .append("\n");
                    });

        } catch (IOException e) {
            result.append("Error: ").append(e.getMessage());
        }

        tvCortex.setText(result.toString().trim());
    }

    /**
     * Reads the maximum frequency for the given CPU core from sysfs.
     *
     * @param coreNumber index of the CPU core.
     * @return maximum frequency in MHz, or 0 if it cannot be read.
     */
    private int getMaxFrequency(int coreNumber) {
        String path = "/sys/devices/system/cpu/cpu" + coreNumber + "/cpufreq/cpuinfo_max_freq";
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();
            if (line != null) {
                return Integer.parseInt(line) / 1000; // Convert KHz to MHz
            }
        } catch (IOException | NumberFormatException e) {
            Log.e("CPUInfo", "Error reading CPU frequency for core " + coreNumber, e);
        }
        return 0;
    }

    /**
     * Maps a raw CPU part hexadecimal code to a human‑readable Cortex family name.
     *
     * @param cpuPart hexadecimal CPU part value (e.g. "0xd03").
     * @return Cortex model name or an \"Unknown\" label if it cannot be mapped.
     */
    private String getCortexType(String cpuPart) {
        cpuPart = cpuPart.toLowerCase().replace("0x", "");
        switch (cpuPart) {
            case "d03": return "Cortex-A53";
            case "d05": return "Cortex-A5";
            case "d07": return "Cortex-A57";
            case "d08": return "Cortex-A72";
            case "d09": return "Cortex-A73";
            case "d0a": return "Cortex-A75";
            case "d0b": return "Cortex-A76";
            case "d0c": return "Neoverse-N1";
            case "d0d": return "Cortex-A77";
            case "d0e": return "Cortex-A76AE";
            case "d41": return "Cortex-A78";
            case "d44": return "Cortex-X1";
            case "d46": return "Cortex-A510";
            case "d47": return "Cortex-A710";
            case "d48": return "Cortex-X2";
            case "d49": return "Cortex-A78C";
            case "d4a": return "Cortex-X3";
            case "d4b": return "Cortex-A715";
            case "d4c": return "Cortex-A510 Refresh";
            case "d4d": return "Cortex-A715 Refresh";
            case "d4e": return "Cortex-X3 Refresh";
            default: return "Unknown (" + cpuPart + ")";
        }
    }

    /**
     * Infers SoC family, fabrication process and foundry from build information
     * and /proc/cpuinfo, then displays a short SoC description.
     */
    public void getSoCInfo() {
        String socInfo = "Unknown";
        String hardware = Build.HARDWARE.toLowerCase();
        String manufacturer = Build.MANUFACTURER.toLowerCase();
        String model = Build.MODEL.toLowerCase();
        String process = "";
        String fabricator = "";

        if (hardware.contains("tensor") || model.contains("pixel")) {
            socInfo = "Google Tensor";
            fabricator = "Samsung";
            if (model.contains("pixel 6")) {
                process = "5 nm";
            } else if (model.contains("pixel 7")) {
                process = "4 nm";
            } else if (model.contains("pixel 8")) {
                process = "4 nm";
            }
        } else if (hardware.contains("snapdragon") || manufacturer.contains("qualcomm")) {
            socInfo = "Qualcomm Snapdragon";
            fabricator = "TSMC";
        } else if (hardware.contains("exynos") || manufacturer.contains("samsung")) {
            socInfo = "Samsung Exynos";
            fabricator = "Samsung";
            // Lógica adicional para Exynos
        } else if (hardware.contains("kirin") || manufacturer.contains("huawei")) {
            socInfo = "Huawei Kirin";
            fabricator = "TSMC";
            // Lógica adicional para Kirin
        } else if (hardware.contains("dimensity") || manufacturer.contains("mediatek")) {
            socInfo = "MediaTek Dimensity";
            fabricator = "TSMC";
        }

        if (process.isEmpty()) {
            try (BufferedReader br = new BufferedReader(new FileReader("/proc/cpuinfo"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.contains("nm")) {

                        Pattern pattern = Pattern.compile("(\\d+)\\s*nm");
                        Matcher matcher = pattern.matcher(line);
                        if (matcher.find()) {
                            process = matcher.group(1) + " nm";
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                Log.e("BatteryFragment", "Error getting SoC info", e);
            }
        }

        if (!process.isEmpty() && !fabricator.isEmpty()) {
            socInfo = process + " " + fabricator;
        } else if (!process.isEmpty()) {
            socInfo = process + " " + socInfo;
        }

        tvSocInfo.setText(socInfo);
    }

    /**
     * Determines the processor family (Snapdragon, Exynos, Tensor, etc.)
     * using build properties and /proc/cpuinfo as a fallback.
     */
    public void getProcessor() {
        String processor = "Unknown";
        String hardware = Build.HARDWARE.toLowerCase();
        String manufacturer = Build.MANUFACTURER.toLowerCase();
        String model = Build.MODEL.toLowerCase();

        if (hardware.contains("tensor") || model.contains("pixel")) {
            processor = "Google Tensor";
        } else if (hardware.contains("snapdragon") || manufacturer.contains("qualcomm")) {
            processor = "Qualcomm Snapdragon";
        } else if (hardware.contains("exynos") || manufacturer.contains("samsung")) {
            processor = "Samsung Exynos";
        } else if (hardware.contains("kirin") || manufacturer.contains("huawei")) {
            processor = "Huawei Kirin";
        } else if (hardware.contains("dimensity") || manufacturer.contains("mediatek")) {
            processor = "MediaTek Dimensity";
        } else {
            // Intenta obtener información más detallada del /proc/cpuinfo
            try (BufferedReader br = new BufferedReader(new FileReader("/proc/cpuinfo"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("Hardware") || line.startsWith("model name")) {
                        String[] parts = line.split(":");
                        if (parts.length > 1) {
                            processor = parts[1].trim();
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                Log.e("BatteryFragment", "Error getting processor info", e);
            }
        }

        tvProcessor.setText(processor);
    }

    /**
     * Groups CPU cores by maximum frequency and displays the core layout
     * as lines like \"2x 2.40GHz\".
     */
    @SuppressLint("DefaultLocale")
    public void getCPUArchitecture() {
        StringBuilder result = new StringBuilder();
        Map<Double, Integer> frequencyCount = new TreeMap<>(Collections.reverseOrder());

        try {
            int coreNumber = 0;
            while (true) {
                int maxFreq = getMaxFrequency(coreNumber);
                if (maxFreq == 0) break; // No more cores

                double ghz = maxFreq / 1000.0;
                frequencyCount.put(ghz, Optional.ofNullable(frequencyCount.get(ghz)).orElse(0) + 1);
                coreNumber++;
            }

            for (Map.Entry<Double, Integer> entry : frequencyCount.entrySet()) {
                result.append(String.format("%dx %.2fGHz\n", entry.getValue(), entry.getKey()));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        tvArchitecture.setText(result.toString().trim());
    }

    /**
     * Displays the list of supported ABIs reported by the build configuration.
     */
    public void getSupportedABIs() {
        String[] abis = Build.SUPPORTED_ABIS;
        String supportedABIs;

        if (abis != null && abis.length > 0) {
            supportedABIs = TextUtils.join(", ", abis);
        } else {
            supportedABIs = context.getString(R.string.no_abi_info);
        }

        tvSupportedABIs.setText(supportedABIs);
    }

    /**
     * Shows the device codename as basic CPU hardware information.
     */
    public void getCPUHardware() {
        String codename = Build.DEVICE;
        tvCPUHardware.setText(codename);
    }

    /**
     * Detects whether the CPU is 32‑bit or 64‑bit based on supported ABIs
     * and displays the result.
     */
    public void getCPUType() {
        String cpuType = context.getString(R.string.unknown);
        if (Build.SUPPORTED_64_BIT_ABIS != null && Build.SUPPORTED_64_BIT_ABIS.length > 0) {
            cpuType = "64 bit";
        } else if (Build.SUPPORTED_32_BIT_ABIS != null && Build.SUPPORTED_32_BIT_ABIS.length > 0) {
            cpuType = "32 bit";
        }
        tvType.setText(cpuType);
    }

    /**
     * Reads the active CPU frequency governor from sysfs and displays its name.
     */
    public void getCPUGovernor() {
        String governor = context.getString(R.string.unknown);
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor"));
            governor = reader.readLine();
            reader.close();
        } catch (IOException e) {
            Log.e("CPUInfo", "Error reading CPU governor", e);
        }
        tvGovernor.setText(governor);
    }

    /**
     * Displays the number of available CPU cores for the current runtime.
     */
    public void getNumberOfCores() {
        int cores = Runtime.getRuntime().availableProcessors();
        tvCores.setText(String.valueOf(cores));
    }

    /**
     * Reads minimum and maximum frequencies for each core, groups identical ranges,
     * and displays them as lines like \"4 x 300 MHz - 2200 MHz\".
     */
    @SuppressLint("DefaultLocale")
    public void getCPUFrequencies() {
        StringBuilder result = new StringBuilder();
        Map<String, Integer> frequencyCount = new LinkedHashMap<>();

        try {
            int coreNumber = 0;
            while (true) {
                String minFreqPath = "/sys/devices/system/cpu/cpu" + coreNumber + "/cpufreq/cpuinfo_min_freq";
                String maxFreqPath = "/sys/devices/system/cpu/cpu" + coreNumber + "/cpufreq/cpuinfo_max_freq";

                int minFreq = 0, maxFreq = 0;

                try (BufferedReader minReader = new BufferedReader(new FileReader(minFreqPath));
                     BufferedReader maxReader = new BufferedReader(new FileReader(maxFreqPath))) {

                    String minLine = minReader.readLine();
                    String maxLine = maxReader.readLine();

                    if (minLine != null && maxLine != null) {
                        minFreq = Integer.parseInt(minLine) / 1000; // Convert to MHz
                        maxFreq = Integer.parseInt(maxLine) / 1000; // Convert to MHz
                    } else {
                        break; // No more cores or unable to read
                    }
                } catch (IOException | NumberFormatException e) {
                    Log.e("CPUInfo", "Error reading frequency for core " + coreNumber, e);
                    break;
                }

                String range = String.format("%d MHz - %d MHz", minFreq, maxFreq);
                Integer count = frequencyCount.getOrDefault(range, 0);
                frequencyCount.put(range, (count != null ? count : 0) + 1);

                coreNumber++;
            }

            for (Map.Entry<String, Integer> entry : frequencyCount.entrySet()) {
                result.append(String.format("%d x %s\n", entry.getValue(), entry.getKey()));
            }

        } catch (Exception e) {
            Log.e("CPUInfo", "Error getting CPU frequencies", e);
            result.append("Error: Unable to retrieve CPU frequencies\n");
        }

        tvCPUFrequency.setText(result.toString().trim());
    }

    /**
     * Creates a minimal OpenGL ES context and queries the GPU renderer string,
     * then displays it.
     */
    public void getGPURenderer() {
        String gpuRenderer = context.getString(R.string.unknown);

        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        int[] version = new int[2];
        egl.eglInitialize(display, version);
        int[] configAttribs = {
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL10.EGL_NONE
        };
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfig = new int[1];
        egl.eglChooseConfig(display, configAttribs, configs, 1, numConfig);
        if (configs[0] != null) {
            EGLContext context = egl.eglCreateContext(display, configs[0], EGL10.EGL_NO_CONTEXT, new int[]{
                    EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                    EGL10.EGL_NONE
            });
            EGLSurface surface = egl.eglCreatePbufferSurface(display, configs[0], new int[]{
                    EGL10.EGL_WIDTH, 1,
                    EGL10.EGL_HEIGHT, 1,
                    EGL10.EGL_NONE
            });
            egl.eglMakeCurrent(display, surface, surface, context);
            gpuRenderer = GLES20.glGetString(GLES20.GL_RENDERER);
            egl.eglDestroySurface(display, surface);
            egl.eglDestroyContext(display, context);
        }

        egl.eglTerminate(display);

        if (gpuRenderer == null || gpuRenderer.isEmpty()) {
            gpuRenderer = context.getString(R.string.unknown);
        }

        tvRenderer.setText(gpuRenderer);
    }

    /**
     * Queries the GPU vendor string from OpenGL ES and displays it, if available.
     */
    public void getGPUVendor() {
        String gpuVendor = context.getString(R.string.unknown);

        ActivityManager activityManager = (ActivityManager) requireContext().getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configInfo = activityManager.getDeviceConfigurationInfo();

        if (configInfo.reqGlEsVersion != ConfigurationInfo.GL_ES_VERSION_UNDEFINED) {
            gpuVendor = android.opengl.GLES20.glGetString(android.opengl.GLES20.GL_VENDOR);
        }

        if (gpuVendor == null || gpuVendor.isEmpty()) {
            gpuVendor = context.getString(R.string.unknown);
        }

        tvVendor.setText(gpuVendor);
    }

    /**
     * Queries the OpenGL ES version and extracts additional GPU details from
     * the extensions list, then displays both in a multi‑line label.
     */
    public void getGPUVersion() {
        String gpuVersion = "Unknown";
        String gpuDetails = "";

        gpuVersion = GLES20.glGetString(GLES20.GL_VERSION);

        String extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS);
        if (extensions != null && !extensions.isEmpty()) {
            String[] extArray = extensions.split(" ");
            for (String ext : extArray) {
                if (ext.contains("GL_ARM_") || ext.contains("GL_MALI_")) {
                    gpuDetails = ext.replace("GL_ARM_", "").replace("GL_MALI_", "");
                    break;
                }
            }
        }

        if (gpuVersion == null || gpuVersion.isEmpty()) {
            gpuVersion = context.getString(R.string.unknown);
        }
        if (gpuDetails.isEmpty()) {
            gpuDetails = context.getString(R.string.no_details_available);
        }

        String fullInfo = gpuVersion + "\n" + gpuDetails;
        tvVersion.setText(fullInfo);
    }

    /**
     * Registers a back‑press callback to navigate from the CPU screen
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