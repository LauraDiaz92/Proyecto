package com.example.diagsmartv2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Rational;
import android.util.Size;
import android.util.SizeF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class CameraFragment extends Fragment {

    TextView tvMPFrontCamera, tvResolutionFrontCamera, tvFocalLengthFrontCamera,
            tvMPBackCamera, tvResolutionBackCamera, tvFocalLengthBackCamera,
            tvAberrationModes, tvAntibandingModes, tvAutoExposureModes,
            tvCompensationStep, tvAutoFocusModes, tvEffects, tvSceneModes,
            tvVideoStabilizationModes, tvAutoWhiteBalanceModes, tvMaxExposure,
            tvMaxFocus, tvMaxAutoWhiteBalance, tvEdgeModes, tvFlashAvailable,
            tvHotPixelModes, tvHardwareLevel, tvThumbnailSizes, tvLensPlacement,
            tvApertures, tvFilterDensities, tvFocalLengths, tvOpticalStabilization,
            tvFocusCalibration, tvHyperFocalDistance, tvMinFocusDistance,
            tvCameraCapabilities, tvDynamicRange, tvMaxOutputStreams, tvMaxOutputStalling,
            tvPartialResults, tvMaxDigitalZoom, tvCroppingType, tvSupportedResolutions,
            tvTestPatternModes, tvColorFilterArrangement, tvSensorSize, tvPixelArraySize,
            tvTimestampSource, tvCameraOrientation, tvFaceDetection;

    CameraManager cameraManager;

    Context context;

    public CameraFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        initReferences(view);
        context = requireContext();
        setCameraInfo();

        return view;
    }

    private void initReferences(View view) {
        tvMPFrontCamera = view.findViewById(R.id.tvMPFrontCamera);
        tvResolutionFrontCamera = view.findViewById(R.id.tvResolutionFrontCamera);
        tvFocalLengthFrontCamera = view.findViewById(R.id.tvFocalLenghtFrontCamera);
        tvMPBackCamera = view.findViewById(R.id.tvMPBackCamera);
        tvResolutionBackCamera = view.findViewById(R.id.tvResolutionBackCamera);
        tvFocalLengthBackCamera = view.findViewById(R.id.tvFocalLenghtBackCamera);
        tvAberrationModes = view.findViewById(R.id.tvAberrationModes);
        tvAntibandingModes = view.findViewById(R.id.tvAntibandingModes);
        tvAutoExposureModes = view.findViewById(R.id.tvAutoExposureModes);
        tvCompensationStep = view.findViewById(R.id.tvCompensationStep);
        tvAutoFocusModes = view.findViewById(R.id.tvAutofocusModes);
        tvEffects = view.findViewById(R.id.tvEffects);
        tvSceneModes = view.findViewById(R.id.tvSceneModes);
        tvVideoStabilizationModes = view.findViewById(R.id.tvVideoStabilizationModes);
        tvAutoWhiteBalanceModes = view.findViewById(R.id.tvAutoWhiteBalanceModes);
        tvMaxExposure = view.findViewById(R.id.tvMaxExposure);
        tvMaxFocus = view.findViewById(R.id.tvMaxFocus);
        tvMaxAutoWhiteBalance = view.findViewById(R.id.tvMaxAutoWhiteBalance);
        tvEdgeModes = view.findViewById(R.id.tvEdgeModes);
        tvFlashAvailable = view.findViewById(R.id.tvFlashAvailable);
        tvHotPixelModes = view.findViewById(R.id.tvHotPixelModes);
        tvHardwareLevel = view.findViewById(R.id.tvHardwareLevel);
        tvThumbnailSizes = view.findViewById(R.id.tvThumbnailSizes);
        tvLensPlacement = view.findViewById(R.id.tvLensPlacement);
        tvApertures = view.findViewById(R.id.tvApertures);
        tvFilterDensities = view.findViewById(R.id.tvFilterDensities);
        tvFocalLengths = view.findViewById(R.id.tvFocalLengths);
        tvOpticalStabilization = view.findViewById(R.id.tvOpticalStabilization);
        tvFocusCalibration = view.findViewById(R.id.tvFocusCalibration);
        tvHyperFocalDistance = view.findViewById(R.id.tvHyperfocalDistance);
        tvMinFocusDistance = view.findViewById(R.id.tvMinFocusDistance);
        tvCameraCapabilities = view.findViewById(R.id.tvCameraCapabilities);
        tvDynamicRange = view.findViewById(R.id.tvDynamicRange);
        tvMaxOutputStreams = view.findViewById(R.id.tvMaxOutputStreams);
        tvMaxOutputStalling = view.findViewById(R.id.tvMaxOutputStalling);
        tvPartialResults = view.findViewById(R.id.tvPartialResults);
        tvMaxDigitalZoom = view.findViewById(R.id.tvMaxDigitalZoom);
        tvCroppingType = view.findViewById(R.id.tvCroppingType);
        tvSupportedResolutions = view.findViewById(R.id.tvSupportedResolutions);
        tvTestPatternModes = view.findViewById(R.id.tvTestPatternModes);
        tvColorFilterArrangement = view.findViewById(R.id.tvColorFilterArrangement);
        tvSensorSize = view.findViewById(R.id.tvSensorSize);
        tvPixelArraySize = view.findViewById(R.id.tvPixelArraySize);
        tvTimestampSource = view.findViewById(R.id.tvTimestampSource);
        tvCameraOrientation = view.findViewById(R.id.tvCameraOrientation);
        tvFaceDetection = view.findViewById(R.id.tvFaceDetection);

    }

    private void setCameraInfo() {
        cameraManager = (CameraManager) requireContext().getSystemService(Context.CAMERA_SERVICE);
        setMP();
        setResolution();
        setFocalDistance();
        setAberrationModesStatus();
        setAntibandingMode();
        setAutoExposureModes();
        setCompensationStep();
        setAutoFocusModes();
        setEffectsStatus();
        setSceneModes();
        setVideoStabilizationModes();
        setAutoWhiteBalanceModes();
        setMeteringRegions();
        setEdgeModes();
        setFlashAvailable();
        setHotPixelModes();
        setHardwareLevel();
        setThumbnailSizes();
        setLensPlacement();
        setApertures();
        setFilterDensities();
        setFocalLengths();
        setOpticalStabilizationModes();
        setFocusDistanceCalibration();
        setlHyperfocalDistance();
        setMinimumFocusDistance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            setCameraCapabilities();
        }
        setDynamicRangeProfiles();
        setMaxOutputStreams();
        setMaxOutputStalling();
        setPartialResultsCount();
        setMaxDigitalZoom();
        setCroppingType();
        setSupportedResolutions();
        setTestPatternModes();
        setColorFilterArrangement();
        setSensorSize();
        setPixelArraySize();
        setTimestampSource();
        setCameraOrientation();
        setFaceDetectionModes();
    }

    @SuppressLint("SetTextI18n")
    private void setMP() {
        float maxFrontMP = 0;
        float maxBackMP = 0;

        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                Integer lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING);

                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) continue;

                Size[] jpegSizes = map.getOutputSizes(ImageFormat.JPEG);
                if (jpegSizes == null || jpegSizes.length == 0) continue;

                Size maxSize = jpegSizes[0];
                for (Size size : jpegSizes) {
                    if (size.getWidth() * size.getHeight() > maxSize.getWidth() * maxSize.getHeight()) {
                        maxSize = size;
                    }
                }

                float mp = (maxSize.getWidth() * maxSize.getHeight()) / 1_000_000f;

                if (lensFacing != null) {
                    if (lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                        maxFrontMP = Math.max(maxFrontMP, mp);
                    } else if (lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                        maxBackMP = Math.max(maxBackMP, mp);
                    }
                }
            }

            tvMPFrontCamera.setText(formatMP(maxFrontMP, context.getString(R.string.camera_front)));
            tvMPBackCamera.setText(formatMP(maxBackMP, context.getString(R.string.camera_back)));


        } catch (CameraAccessException e) {
            tvMPFrontCamera.setText(context.getString(R.string.na_front));
            tvMPBackCamera.setText(context.getString(R.string.na_back));
        }
    }


    private String formatMP(float mp, String position) {
        return (mp > 0) ? String.format(Locale.US, "%.0f MP - %s", mp, position) : "N/A - " + position;
    }

    private void setResolution() {
        String frontResolution = "N/A";
        String backResolution = "N/A";

        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                Integer lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING);

                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) continue;

                Size[] jpegSizes = map.getOutputSizes(ImageFormat.JPEG);
                if (jpegSizes == null || jpegSizes.length == 0) continue;

                // Buscar el tamaño máximo
                Size maxSize = jpegSizes[0];
                for (Size size : jpegSizes) {
                    if (size.getWidth() * size.getHeight() > maxSize.getWidth() * maxSize.getHeight()) {
                        maxSize = size;
                    }
                }

                String resolution = maxSize.getWidth() + "x" + maxSize.getHeight();

                if (lensFacing != null) {
                    if (lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                        frontResolution = resolution;
                    } else if (lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                        backResolution = resolution;
                    }
                }
            }

            tvResolutionFrontCamera.setText(frontResolution);
            tvResolutionBackCamera.setText(backResolution);

        } catch (CameraAccessException e) {
            tvResolutionFrontCamera.setText(context.getString(R.string.not_available));
            tvResolutionBackCamera.setText(context.getString(R.string.not_available));
        }
    }

    private void setFocalDistance() {
        float frontFocalLength = 0f;
        float backFocalLength = 0f;

        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                Integer lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING);
                float[] focalLengths = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);

                if (focalLengths != null && focalLengths.length > 0) {
                    float focalLength = focalLengths[0];
                    if (lensFacing != null) {
                        if (lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                            frontFocalLength = Math.max(frontFocalLength, focalLength);
                        } else if (lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                            backFocalLength = Math.max(backFocalLength, focalLength);
                        }
                    }
                }
            }

            tvFocalLengthFrontCamera.setText(frontFocalLength > 0 ? String.format(Locale.getDefault(), "%.2fmm", frontFocalLength) : "N/A");
            tvFocalLengthBackCamera.setText(backFocalLength > 0 ? String.format(Locale.getDefault(), "%.2fmm", backFocalLength) : "N/A");

        } catch (CameraAccessException e) {
            tvFocalLengthFrontCamera.setText(context.getString(R.string.not_available));
            tvFocalLengthBackCamera.setText(context.getString(R.string.not_available));
        }
    }

    private void setAberrationModesStatus() {
        boolean aberrationSupported = false;

        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                int[] aberrationModes = characteristics.get(CameraCharacteristics.COLOR_CORRECTION_AVAILABLE_ABERRATION_MODES);

                if (aberrationModes != null) {
                    for (int mode : aberrationModes) {
                        if (mode != CameraCharacteristics.COLOR_CORRECTION_ABERRATION_MODE_OFF) {
                            aberrationSupported = true;
                            break;
                        }
                    }
                }
                if (aberrationSupported) break;
            }

            tvAberrationModes.setText(aberrationSupported ? "On" : "Off");

        } catch (CameraAccessException e) {
            tvAberrationModes.setText(context.getString(R.string.not_available));
        }
    }

    private void setAntibandingMode() {
        String modeText = "N/A";

        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                int[] modes = characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_ANTIBANDING_MODES);

                if (modes != null) {
                    for (int mode : modes) {
                        switch (mode) {
                            case CameraMetadata.CONTROL_AE_ANTIBANDING_MODE_AUTO:
                                modeText = "Auto";
                                break;
                            case CameraMetadata.CONTROL_AE_ANTIBANDING_MODE_50HZ:
                                modeText = "50hz";
                                break;
                            case CameraMetadata.CONTROL_AE_ANTIBANDING_MODE_60HZ:
                                if (!modeText.equals("50hz")) modeText = "60HZ";
                                break;
                            case CameraMetadata.CONTROL_AE_ANTIBANDING_MODE_OFF:
                                if (modeText.equals("N/A")) modeText = "Off";
                                break;
                        }
                    }
                }
            }
        } catch (CameraAccessException e) {
            modeText = context.getString(R.string.not_available);
        }

        tvAntibandingModes.setText(modeText);
    }

    private void setAutoExposureModes() {
        Set<String> modesSet = new LinkedHashSet<>();
        Context context = requireContext();

        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                int[] aeModes = characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);

                if (aeModes != null) {
                    for (int mode : aeModes) {
                        switch (mode) {
                            case CameraMetadata.CONTROL_AE_MODE_OFF:
                                modesSet.add(context.getString(R.string.ae_mode_off));
                                break;
                            case CameraMetadata.CONTROL_AE_MODE_ON:
                                modesSet.add(context.getString(R.string.ae_mode_on));
                                break;
                            case CameraMetadata.CONTROL_AE_MODE_ON_AUTO_FLASH:
                                modesSet.add(context.getString(R.string.ae_mode_auto_flash));
                                break;
                            case CameraMetadata.CONTROL_AE_MODE_ON_ALWAYS_FLASH:
                                modesSet.add(context.getString(R.string.ae_mode_always_flash));
                                break;
                            case CameraMetadata.CONTROL_AE_MODE_ON_AUTO_FLASH_REDEYE:
                                modesSet.add(context.getString(R.string.ae_mode_auto_flash_redeye));
                                break;
                            case CameraMetadata.CONTROL_AE_MODE_ON_EXTERNAL_FLASH:
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                    modesSet.add(context.getString(R.string.ae_mode_external_flash));
                                }
                                break;
                        }
                    }
                }
            }

            String result = modesSet.isEmpty() ? context.getString(R.string.not_available) : TextUtils.join(", ", modesSet);
            tvAutoExposureModes.setText(result);

        } catch (CameraAccessException e) {
            tvAutoExposureModes.setText(context.getString(R.string.not_available));
        }
    }


    private void setCompensationStep() {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                Rational compensationStep = characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP);

                if (compensationStep != null) {
                    int numerator = compensationStep.getNumerator();
                    int denominator = compensationStep.getDenominator();

                    String stepText = (denominator == 1) ?
                            String.valueOf(numerator) :
                            numerator + "/" + denominator;

                    tvCompensationStep.setText(stepText);
                    return;
                }
            }
            tvCompensationStep.setText(context.getString(R.string.not_available));

        } catch (CameraAccessException e) {
            tvCompensationStep.setText(context.getString(R.string.not_available));
        }
    }

    private void setAutoFocusModes() {
        Map<Integer, String> afModeMap = new HashMap<>();
        Context context = requireContext();
        afModeMap.put(0, context.getString(R.string.af_mode_off));
        afModeMap.put(1, context.getString(R.string.af_mode_auto));
        afModeMap.put(2, context.getString(R.string.af_mode_macro));
        afModeMap.put(3, context.getString(R.string.af_mode_continuous_video));
        afModeMap.put(4, context.getString(R.string.af_mode_continuous_picture));
        afModeMap.put(5, context.getString(R.string.af_mode_edof));

        Set<String> modesSet = new LinkedHashSet<>();

        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                int[] afModes = characteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);

                if (afModes != null) {
                    for (int mode : afModes) {
                        String modeName = afModeMap.get(mode);
                        if (modeName != null) {
                            modesSet.add(modeName);
                        }
                    }
                }
            }
            StringBuilder sb = new StringBuilder();
            for (String mode : modesSet) {
                sb.append("- ").append(mode).append("\n");
            }
            tvAutoFocusModes.setText(sb.length() > 0 ? sb.toString().trim() : context.getString(R.string.not_available));

        } catch (CameraAccessException e) {
            tvAutoFocusModes.setText(context.getString(R.string.not_available));
        }
    }

    private void setEffectsStatus() {
        Context context = requireContext();
        boolean effectsOn = false;

        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                int[] effects = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_EFFECTS);

                if (effects != null) {
                    for (int effect : effects) {
                        if (effect != CameraMetadata.CONTROL_EFFECT_MODE_OFF) {
                            effectsOn = true;
                            break;
                        }
                    }
                }
                if (effectsOn) break;
            }
            tvEffects.setText(effectsOn ? context.getString(R.string.effects_on) : context.getString(R.string.effects_off));
        } catch (CameraAccessException e) {
            tvEffects.setText(context.getString(R.string.not_available));
        }
    }

    private void setSceneModes() {
        Map<Integer, String> sceneModeMap = new HashMap<>();
        Context context = requireContext();
        sceneModeMap.put(0, context.getString(R.string.scene_mode_disabled));
        sceneModeMap.put(1, context.getString(R.string.scene_mode_face_priority));
        sceneModeMap.put(2, context.getString(R.string.scene_mode_action));
        sceneModeMap.put(3, context.getString(R.string.scene_mode_portrait));
        sceneModeMap.put(4, context.getString(R.string.scene_mode_landscape));
        sceneModeMap.put(5, context.getString(R.string.scene_mode_night));
        sceneModeMap.put(6, context.getString(R.string.scene_mode_night_portrait));
        sceneModeMap.put(7, context.getString(R.string.scene_mode_theatre));
        sceneModeMap.put(8, context.getString(R.string.scene_mode_beach));
        sceneModeMap.put(9, context.getString(R.string.scene_mode_snow));
        sceneModeMap.put(10, context.getString(R.string.scene_mode_sunset));
        sceneModeMap.put(11, context.getString(R.string.scene_mode_steady_photo));
        sceneModeMap.put(12, context.getString(R.string.scene_mode_fireworks));
        sceneModeMap.put(13, context.getString(R.string.scene_mode_sports));
        sceneModeMap.put(14, context.getString(R.string.scene_mode_party));
        sceneModeMap.put(15, context.getString(R.string.scene_mode_candlelight));
        sceneModeMap.put(16, context.getString(R.string.scene_mode_barcode));
        sceneModeMap.put(18, context.getString(R.string.scene_mode_hdr));

        Set<String> modesSet = new LinkedHashSet<>();

        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                int[] sceneModes = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_SCENE_MODES);

                if (sceneModes != null) {
                    for (int mode : sceneModes) {
                        String modeName = sceneModeMap.get(mode);
                        if (modeName != null) {
                            modesSet.add(modeName);
                        }
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            for (String mode : modesSet) {
                sb.append("- ").append(mode).append("\n");
            }
            tvSceneModes.setText(sb.length() > 0 ? sb.toString().trim() : context.getString(R.string.not_available));

        } catch (CameraAccessException e) {
            tvSceneModes.setText(context.getString(R.string.not_available));
        }
    }

    private void setVideoStabilizationModes() {
        Set<String> modeNames = new LinkedHashSet<>();
        Context context = requireContext();

        Map<Integer, String> stabilizationMap = new HashMap<>();
        stabilizationMap.put(CameraMetadata.CONTROL_VIDEO_STABILIZATION_MODE_OFF, context.getString(R.string.vs_mode_off));
        stabilizationMap.put(CameraMetadata.CONTROL_VIDEO_STABILIZATION_MODE_ON, context.getString(R.string.vs_mode_on));
        if (Build.VERSION.SDK_INT >= 33) {
            stabilizationMap.put(CameraMetadata.CONTROL_VIDEO_STABILIZATION_MODE_PREVIEW_STABILIZATION, context.getString(R.string.vs_mode_preview_stabilization));
        }

        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                int[] stabilizationModes = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES);

                if (stabilizationModes != null) {
                    for (int mode : stabilizationModes) {
                        String name = stabilizationMap.get(mode);
                        if (name != null) {
                            modeNames.add(name);
                        }
                    }
                }
            }
            String result = modeNames.isEmpty() ? context.getString(R.string.not_available) : TextUtils.join(", ", modeNames);
            tvVideoStabilizationModes.setText(result);

        } catch (CameraAccessException e) {
            tvVideoStabilizationModes.setText(context.getString(R.string.not_available));
        }
    }

    private void setAutoWhiteBalanceModes() {
        Map<Integer, String> awbModeMap = new HashMap<>();
        Context context = requireContext();

        awbModeMap.put(CameraMetadata.CONTROL_AWB_MODE_OFF, context.getString(R.string.awb_mode_off));
        awbModeMap.put(CameraMetadata.CONTROL_AWB_MODE_AUTO, context.getString(R.string.awb_mode_auto));
        awbModeMap.put(CameraMetadata.CONTROL_AWB_MODE_INCANDESCENT, context.getString(R.string.awb_mode_incandescent));
        awbModeMap.put(CameraMetadata.CONTROL_AWB_MODE_FLUORESCENT, context.getString(R.string.awb_mode_fluorescent));
        awbModeMap.put(CameraMetadata.CONTROL_AWB_MODE_WARM_FLUORESCENT, context.getString(R.string.awb_mode_warm_fluorescent));
        awbModeMap.put(CameraMetadata.CONTROL_AWB_MODE_DAYLIGHT, context.getString(R.string.awb_mode_daylight));
        awbModeMap.put(CameraMetadata.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT, context.getString(R.string.awb_mode_cloudy_daylight));
        awbModeMap.put(CameraMetadata.CONTROL_AWB_MODE_TWILIGHT, context.getString(R.string.awb_mode_twilight));
        awbModeMap.put(CameraMetadata.CONTROL_AWB_MODE_SHADE, context.getString(R.string.awb_mode_shade));

        Set<String> modesSet = new LinkedHashSet<>();

        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                int[] awbModes = characteristics.get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES);

                if (awbModes != null) {
                    for (int mode : awbModes) {
                        String modeName = awbModeMap.get(mode);
                        if (modeName != null) {
                            modesSet.add(modeName);
                        }
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            for (String mode : modesSet) {
                sb.append("- ").append(mode).append("\n");
            }
            tvAutoWhiteBalanceModes.setText(sb.length() > 0 ? sb.toString().trim() : context.getString(R.string.not_available));

        } catch (CameraAccessException e) {
            tvAutoWhiteBalanceModes.setText(context.getString(R.string.not_available));
        }
    }

    private void setMeteringRegions() {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);

                Integer maxAeRegions = characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AE);
                Integer maxAfRegions = characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF);
                Integer maxAwbRegions = characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AWB);

                tvMaxExposure.setText(String.valueOf(maxAeRegions != null ? maxAeRegions : 0));
                tvMaxFocus.setText(String.valueOf(maxAfRegions != null ? maxAfRegions : 0));
                tvMaxAutoWhiteBalance.setText(String.valueOf(maxAwbRegions != null ? maxAwbRegions : 0));

                break;
            }
        } catch (CameraAccessException e) {
            tvMaxExposure.setText(context.getString(R.string.not_available));
            tvMaxFocus.setText(context.getString(R.string.not_available));
            tvMaxAutoWhiteBalance.setText(context.getString(R.string.not_available));
        }
    }

    private void setEdgeModes() {
        Map<Integer, String> edgeModeMap = new HashMap<>();
        Context context = requireContext();
        edgeModeMap.put(CameraMetadata.EDGE_MODE_OFF, context.getString(R.string.edge_mode_off));
        edgeModeMap.put(CameraMetadata.EDGE_MODE_FAST, context.getString(R.string.edge_mode_fast));
        edgeModeMap.put(CameraMetadata.EDGE_MODE_HIGH_QUALITY, context.getString(R.string.edge_mode_high_quality));
        edgeModeMap.put(CameraMetadata.EDGE_MODE_ZERO_SHUTTER_LAG, context.getString(R.string.edge_mode_zero_shutter_lag));

        Set<String> modesSet = new LinkedHashSet<>();

        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                int[] edgeModes = characteristics.get(CameraCharacteristics.EDGE_AVAILABLE_EDGE_MODES);

                if (edgeModes != null) {
                    for (int mode : edgeModes) {
                        String modeName = edgeModeMap.get(mode);
                        if (modeName != null) {
                            modesSet.add(modeName);
                        }
                    }
                }
            }

            String result = modesSet.isEmpty() ? context.getString(R.string.not_available) : TextUtils.join(", ", modesSet);
            tvEdgeModes.setText(result);

        } catch (CameraAccessException e) {
            tvEdgeModes.setText(context.getString(R.string.not_available));
        }
    }

    private void setFlashAvailable() {
        Context context = requireContext();
        String flashStatus = context.getString(R.string.flash_no);
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                Boolean flashAvailable = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                if (flashAvailable != null && flashAvailable) {
                    flashStatus = context.getString(R.string.flash_yes);
                    break;
                }
            }
            tvFlashAvailable.setText(flashStatus);
        } catch (CameraAccessException e) {
            tvFlashAvailable.setText(context.getString(R.string.not_available));
        }
    }

    private void setHotPixelModes() {
        Set<String> modesSet = new LinkedHashSet<>();
        Context context = requireContext();

        Map<Integer, String> hotPixelModeMap = new HashMap<>();
        hotPixelModeMap.put(CameraMetadata.HOT_PIXEL_MODE_OFF, context.getString(R.string.hot_pixel_mode_off));
        hotPixelModeMap.put(CameraMetadata.HOT_PIXEL_MODE_FAST, context.getString(R.string.hot_pixel_mode_fast));
        hotPixelModeMap.put(CameraMetadata.HOT_PIXEL_MODE_HIGH_QUALITY, context.getString(R.string.hot_pixel_mode_high_quality));

        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                int[] modes = characteristics.get(CameraCharacteristics.HOT_PIXEL_AVAILABLE_HOT_PIXEL_MODES);

                if (modes != null) {
                    for (int mode : modes) {
                        String modeName = hotPixelModeMap.get(mode);
                        if (modeName != null) {
                            modesSet.add(modeName);
                        }
                    }
                }
            }

            String result = modesSet.isEmpty() ? context.getString(R.string.not_available) : TextUtils.join(", ", modesSet);
            tvHotPixelModes.setText(result);

        } catch (CameraAccessException e) {
            tvHotPixelModes.setText(context.getString(R.string.not_available));
        }
    }

    private void setHardwareLevel() {
        Context context = requireContext();
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                Integer hardwareLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                String levelText = context.getString(R.string.not_available);
                if (hardwareLevel != null) {
                    switch (hardwareLevel) {
                        case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY:
                            levelText = context.getString(R.string.hardware_level_legacy);
                            break;
                        case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED:
                            levelText = context.getString(R.string.hardware_level_limited);
                            break;
                        case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL:
                            levelText = context.getString(R.string.hardware_level_full);
                            break;
                        case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3:
                            levelText = context.getString(R.string.hardware_level_3);
                            break;
                        case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL:
                            levelText = context.getString(R.string.hardware_level_external);
                            break;
                        default:
                            levelText = context.getString(R.string.hardware_level_unknown);
                            break;
                    }
                }
                tvHardwareLevel.setText(levelText);
                break;
            }
        } catch (CameraAccessException e) {
            tvHardwareLevel.setText(context.getString(R.string.not_available));
        }
    }

    private void setThumbnailSizes() {
        StringBuilder sb = new StringBuilder();

        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                Size[] thumbnailSizes = characteristics.get(CameraCharacteristics.JPEG_AVAILABLE_THUMBNAIL_SIZES);

                if (thumbnailSizes != null && thumbnailSizes.length > 0) {
                    for (Size size : thumbnailSizes) {
                        sb.append("- ").append(size.getWidth()).append(" x ").append(size.getHeight()).append("\n");
                    }
                } else {
                    sb.append("- 0 x 0\n");
                }
                break;
            }

            final String result = sb.toString().trim();
            requireActivity().runOnUiThread(() -> tvThumbnailSizes.setText(result));

        } catch (CameraAccessException e) {
            requireActivity().runOnUiThread(() -> tvThumbnailSizes.setText(context.getString(R.string.not_available)));
        }
    }

    private void setLensPlacement() {
        String lensPlacementText = requireContext().getString(R.string.na);
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                Integer lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (lensFacing != null) {
                    switch (lensFacing) {
                        case CameraCharacteristics.LENS_FACING_BACK:
                            lensPlacementText = requireContext().getString(R.string.lens_placement_back);
                            break;
                        case CameraCharacteristics.LENS_FACING_FRONT:
                            lensPlacementText = requireContext().getString(R.string.lens_placement_front);
                            break;
                        case CameraCharacteristics.LENS_FACING_EXTERNAL:
                            lensPlacementText = requireContext().getString(R.string.lens_placement_external);
                            break;
                        default:
                            lensPlacementText = requireContext().getString(R.string.lens_placement_unknown);
                            break;
                    }
                }
                break;
            }
            tvLensPlacement.setText(lensPlacementText);
        } catch (CameraAccessException e) {
            tvLensPlacement.setText(requireContext().getString(R.string.na));
        }
    }

    private void setApertures() {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                float[] apertures = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES);

                if (apertures != null && apertures.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (float aperture : apertures) {
                        sb.append(String.format(Locale.getDefault(), "f/%.2f", aperture)).append("\n");
                    }
                    tvApertures.setText(sb.toString().trim());
                } else {
                    tvApertures.setText(context.getString(R.string.not_available));
                }
                break;
            }
        } catch (CameraAccessException e) {
            tvApertures.setText(context.getString(R.string.not_available));
        }
    }

    private void setFilterDensities() {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                float[] filterDensities = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FILTER_DENSITIES);

                if (filterDensities != null && filterDensities.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (float density : filterDensities) {
                        sb.append(String.format(Locale.getDefault(), "%.1f", density)).append("\n");
                    }
                    tvFilterDensities.setText(sb.toString().trim());
                } else {
                    tvFilterDensities.setText(context.getString(R.string.not_available));
                }
                break;
            }
        } catch (CameraAccessException e) {
            tvFilterDensities.setText(context.getString(R.string.not_available));
        }
    }

    private void setFocalLengths() {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                float[] focalLengths = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);

                if (focalLengths != null && focalLengths.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (float focal : focalLengths) {
                        sb.append(String.format(Locale.getDefault(), "%.2fmm", focal)).append("\n");
                    }
                    tvFocalLengths.setText(sb.toString().trim());
                } else {
                    tvFocalLengths.setText(context.getString(R.string.not_available));
                }
                break;
            }
        } catch (CameraAccessException e) {
            tvFocalLengths.setText(context.getString(R.string.not_available));
        }
    }

    private void setOpticalStabilizationModes() {
        Set<String> modesSet = new LinkedHashSet<>();
        Context context = requireContext();

        Map<Integer, String> opticalStabilizationMap = new HashMap<>();
        opticalStabilizationMap.put(CameraMetadata.LENS_OPTICAL_STABILIZATION_MODE_OFF, context.getString(R.string.off));
        opticalStabilizationMap.put(CameraMetadata.LENS_OPTICAL_STABILIZATION_MODE_ON, context.getString(R.string.on));

        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                int[] opticalStabilizationModes = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION);

                if (opticalStabilizationModes != null) {
                    for (int mode : opticalStabilizationModes) {
                        String modeName = opticalStabilizationMap.get(mode);
                        if (modeName != null) {
                            modesSet.add(modeName);
                        }
                    }
                }
            }

            String result = modesSet.isEmpty() ? context.getString(R.string.not_available) : TextUtils.join(", ", modesSet);
            tvOpticalStabilization.setText(result);

        } catch (CameraAccessException e) {
            tvOpticalStabilization.setText(context.getString(R.string.not_available));
        }
    }

    private void setFocusDistanceCalibration() {
        Map<Integer, String> calibrationMap = new HashMap<>();
        Context context = requireContext();

        calibrationMap.put(0, context.getString(R.string.focus_calibration_uncalibrated));
        calibrationMap.put(1, context.getString(R.string.focus_calibration_approximate));
        calibrationMap.put(2, context.getString(R.string.focus_calibration_calibrated));

        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                Integer calibration = characteristics.get(CameraCharacteristics.LENS_INFO_FOCUS_DISTANCE_CALIBRATION);

                String calibrationText = calibration != null && calibrationMap.containsKey(calibration)
                        ? calibrationMap.get(calibration)
                        : context.getString(R.string.not_available);

                tvFocusCalibration.setText(calibrationText);
                break;
            }
        } catch (CameraAccessException e) {
            tvFocusCalibration.setText(context.getString(R.string.not_available));
        }
    }

    private void setlHyperfocalDistance() {
        float focalLength = 4.38f;
        float aperture = 2.0f;
        float circleOfConfusion = 0.02f;

        float hyperfocal_mm = (focalLength * focalLength) / (aperture * circleOfConfusion) + focalLength;
        float hyperfocal_m = hyperfocal_mm / 1000f;

        tvHyperFocalDistance.setText(String.format(Locale.US, "%.8f", hyperfocal_m));
    }

    private void setMinimumFocusDistance() {
        float maxDistance = 0f;

        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                try {
                    CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                    Float distance = characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);

                    if (distance != null && distance >= maxDistance) {
                        maxDistance = distance;
                    }
                } catch (Exception ignored) {
                }
            }

            String formatted = (maxDistance > 0)
                    ? String.format(Locale.US, "%.6f", maxDistance)
                    : "0";

            tvMinFocusDistance.setText(formatted);

        } catch (Exception e) {
            tvMinFocusDistance.setText("0");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void setCameraCapabilities() {
        Set<String> capabilitiesSet = new LinkedHashSet<>();
        Context context = requireContext();

        Map<Integer, String> capabilityMap = new HashMap<>();
        capabilityMap.put(CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE, context.getString(R.string.cap_backward_compatible));
        capabilityMap.put(CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR, context.getString(R.string.cap_manual_sensor));
        capabilityMap.put(CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_POST_PROCESSING, context.getString(R.string.cap_manual_post_processing));
        capabilityMap.put(CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_RAW, context.getString(R.string.cap_raw_capture));
        capabilityMap.put(CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_PRIVATE_REPROCESSING, context.getString(R.string.cap_private_reprocessing));
        capabilityMap.put(CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_READ_SENSOR_SETTINGS, context.getString(R.string.cap_read_sensor_settings));
        capabilityMap.put(CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_BURST_CAPTURE, context.getString(R.string.cap_burst_capture));
        capabilityMap.put(CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_YUV_REPROCESSING, context.getString(R.string.cap_yuv_reprocessing));
        capabilityMap.put(CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO, context.getString(R.string.cap_constrained_high_speed_video));
        capabilityMap.put(CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_LOGICAL_MULTI_CAMERA, context.getString(R.string.cap_logical_multi_camera));
        capabilityMap.put(CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_MONOCHROME, context.getString(R.string.cap_monochrome));
        capabilityMap.put(CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_SECURE_IMAGE_DATA, context.getString(R.string.cap_secure_image_data));
        capabilityMap.put(CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_SYSTEM_CAMERA, context.getString(R.string.cap_system_camera));
        capabilityMap.put(CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT, context.getString(R.string.cap_depth_output));
        capabilityMap.put(CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_DYNAMIC_RANGE_TEN_BIT, context.getString(R.string.cap_dynamic_range_10bit));

        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                int[] capabilities = characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
                if (capabilities != null) {
                    for (int capability : capabilities) {
                        String capName = capabilityMap.get(capability);
                        if (capName != null) {
                            capabilitiesSet.add(capName);
                        }
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            for (String cap : capabilitiesSet) {
                sb.append("- ").append(cap).append("\n");
            }

            tvCameraCapabilities.setText(sb.length() > 0 ? sb.toString().trim() : context.getString(R.string.not_available));

        } catch (CameraAccessException e) {
            tvCameraCapabilities.setText(context.getString(R.string.not_available));
        }
    }

    @SuppressLint("SetTextI18n")
    private void setDynamicRangeProfiles() {
        StringBuilder sb = new StringBuilder();
        Context context = requireContext();

        Map<Long, String> profileMap = new HashMap<>();
        profileMap.put(1L, context.getString(R.string.profile_standard));
        profileMap.put(2L, context.getString(R.string.profile_hlg10));
        profileMap.put(4L, context.getString(R.string.profile_hdr10));
        profileMap.put(8L, context.getString(R.string.profile_hdr10_plus));
        profileMap.put(16L, context.getString(R.string.profile_dolby_10b_hdr_ref));
        profileMap.put(32L, context.getString(R.string.profile_dolby_10b_hdr_ref_po));
        profileMap.put(64L, context.getString(R.string.profile_dolby_10b_hdr_oem));
        profileMap.put(128L, context.getString(R.string.profile_dolby_10b_hdr_oem_po));
        profileMap.put(256L, context.getString(R.string.profile_dolby_8b_hdr_ref));
        profileMap.put(512L, context.getString(R.string.profile_dolby_8b_hdr_ref_po));
        profileMap.put(1024L, context.getString(R.string.profile_dolby_8b_hdr_oem));
        profileMap.put(2048L, context.getString(R.string.profile_dolby_8b_hdr_oem_po));

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                for (String cameraId : cameraManager.getCameraIdList()) {
                    CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);

                    android.hardware.camera2.params.DynamicRangeProfiles profiles =
                            characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_DYNAMIC_RANGE_PROFILES);

                    if (profiles != null) {
                        Set<Long> supportedProfiles = profiles.getSupportedProfiles();
                        for (Long profile : supportedProfiles) {
                            String profileName = profileMap.getOrDefault(profile, "UNKNOWN_" + profile);
                            sb.append("- ").append(profileName).append("\n");
                        }
                    }

                    break;
                }
            } else {
                sb.append("- ").append(context.getString(R.string.not_supported))
                        .append(" on API ").append(Build.VERSION.SDK_INT)
                        .append(" (requires Android 13+)");
            }

            tvDynamicRange.setText(sb.toString().trim());

        } catch (CameraAccessException e) {
            tvDynamicRange.setText(context.getString(R.string.camera_access_error));
        } catch (Exception e) {
            tvDynamicRange.setText(context.getString(R.string.not_supported));
        }
    }

    private void setMaxOutputStreams() {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);

                Integer maxStreams = characteristics.get(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_PROC);
                if (maxStreams != null) {
                    tvMaxOutputStreams.setText(String.valueOf(maxStreams));
                } else {
                    tvMaxOutputStreams.setText(context.getString(R.string.not_available));
                }

                break;
            }
        } catch (CameraAccessException e) {
            tvMaxOutputStreams.setText(context.getString(R.string.not_available));
        }
    }

    private void setMaxOutputStalling() {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);

                Integer maxStallingStreams = characteristics.get(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_PROC_STALLING);
                if (maxStallingStreams != null) {
                    tvMaxOutputStalling.setText(String.valueOf(maxStallingStreams));
                } else {
                    tvMaxOutputStalling.setText(context.getString(R.string.not_available));
                }

                break;
            }
        } catch (CameraAccessException e) {
            tvMaxOutputStalling.setText(context.getString(R.string.not_available));
        }
    }

    private void setPartialResultsCount() {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                Integer partialResultCount = characteristics.get(CameraCharacteristics.REQUEST_PARTIAL_RESULT_COUNT);
                if (partialResultCount != null) {
                    tvPartialResults.setText(String.valueOf(partialResultCount));
                } else {
                    tvPartialResults.setText(context.getString(R.string.not_available));
                }
                break;
            }
        } catch (CameraAccessException e) {
            tvPartialResults.setText(context.getString(R.string.not_available));
        }
    }

    private void setMaxDigitalZoom() {
        float maxZoom = 0f;
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                Float zoom = characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
                if (zoom != null && zoom > maxZoom) {
                    maxZoom = zoom;
                }
            }
            String formattedZoom = String.format(Locale.US, "%.1f", maxZoom);
            tvMaxDigitalZoom.setText(formattedZoom);
        } catch (CameraAccessException e) {
            tvMaxDigitalZoom.setText(context.getString(R.string.not_available));
        }
    }

    private void setCroppingType() {
        String croppingTypeText = requireContext().getString(R.string.not_available);
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                Integer croppingType = characteristics.get(CameraCharacteristics.SCALER_CROPPING_TYPE);
                if (croppingType != null) {
                    switch (croppingType) {
                        case 0:
                            croppingTypeText = requireContext().getString(R.string.cropping_center_only);
                            break;
                        case 1:
                            croppingTypeText = requireContext().getString(R.string.cropping_freeform);
                            break;
                        default:
                            croppingTypeText = requireContext().getString(R.string.cropping_unknown);
                            break;
                    }
                }
                break;
            }
        } catch (CameraAccessException e) {
            croppingTypeText = requireContext().getString(R.string.not_available);
        }
        tvCroppingType.setText(croppingTypeText);
    }

    private void setSupportedResolutions() {
        StringBuilder sb = new StringBuilder();

        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map != null) {
                    Size[] sizes = map.getOutputSizes(ImageFormat.JPEG);
                    if (sizes != null) {
                        for (Size size : sizes) {
                            sb.append("- ")
                                    .append(size.getWidth())
                                    .append(" x ")
                                    .append(size.getHeight())
                                    .append("\n");
                        }
                    }
                }
                break;
            }
            tvSupportedResolutions.setText(sb.length() > 0 ? sb.toString().trim() : context.getString(R.string.not_available));
        } catch (CameraAccessException e) {
            tvSupportedResolutions.setText(context.getString(R.string.not_available));
        }
    }

    private void setTestPatternModes() {
        Set<String> modesSet = new LinkedHashSet<>();
        Context context = requireContext();

        Map<Integer, String> testPatternMap = new HashMap<>();
        testPatternMap.put(0, context.getString(R.string.test_pattern_off));
        testPatternMap.put(1, context.getString(R.string.test_pattern_solid_color));
        testPatternMap.put(2, context.getString(R.string.test_pattern_color_bars));
        testPatternMap.put(3, context.getString(R.string.test_pattern_color_bars_fade_to_gray));
        testPatternMap.put(4, context.getString(R.string.test_pattern_pn9));

        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                int[] testPatternModes = characteristics.get(CameraCharacteristics.SENSOR_AVAILABLE_TEST_PATTERN_MODES);

                if (testPatternModes != null) {
                    for (int mode : testPatternModes) {
                        String modeName = testPatternMap.get(mode);
                        if (modeName != null) {
                            modesSet.add(modeName);
                        }
                    }
                }
            }

            String result = modesSet.isEmpty() ? context.getString(R.string.not_available) : String.join(", ", modesSet);
            tvTestPatternModes.setText(result);

        } catch (CameraAccessException e) {
            tvTestPatternModes.setText(context.getString(R.string.not_available));
        }
    }

    private void setColorFilterArrangement() {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                Integer arrangement = characteristics.get(CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT);
                String arrangementText = context.getString(R.string.not_available);
                if (arrangement != null) {
                    switch (arrangement) {
                        case CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_BGGR:
                            arrangementText = "BGGR";
                            break;
                        case CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_GBRG:
                            arrangementText = "GBRG";
                            break;
                        case CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_GRBG:
                            arrangementText = "GRBG";
                            break;
                        case CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_RGGB:
                            arrangementText = "RGGB";
                            break;
                        case CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_MONO:
                            arrangementText = "MONO";
                            break;
                        case CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_NIR:
                            arrangementText = "NIR";
                            break;
                        default:
                            arrangementText = requireContext().getString(R.string.timestamp_unknown);
                            break;
                    }
                }
                tvColorFilterArrangement.setText(arrangementText);
                break;
            }
        } catch (CameraAccessException e) {
            tvColorFilterArrangement.setText(context.getString(R.string.not_available));
        }
    }

    private void setSensorSize() {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                SizeF sensorSize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
                if (sensorSize != null) {
                    String formattedSize = String.format(Locale.US, "%.2f x %.2f", sensorSize.getWidth(), sensorSize.getHeight());
                    tvSensorSize.setText(formattedSize);
                } else {
                    tvSensorSize.setText(context.getString(R.string.not_available));
                }
                break;
            }
        } catch (CameraAccessException e) {
            tvSensorSize.setText(context.getString(R.string.not_available));
        }
    }

    private void setPixelArraySize() {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                Size pixelArraySize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);
                if (pixelArraySize != null) {
                    String formattedSize = String.format(Locale.US, "%04d x %04d", pixelArraySize.getWidth(), pixelArraySize.getHeight());
                    tvPixelArraySize.setText(formattedSize);
                } else {
                    tvPixelArraySize.setText(context.getString(R.string.not_available));
                }
                break;
            }
        } catch (CameraAccessException e) {
            tvPixelArraySize.setText(context.getString(R.string.not_available));
        }
    }

    private void setTimestampSource() {
        String timestampSourceText = requireContext().getString(R.string.timestamp_na);
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                Integer timestampSource = characteristics.get(CameraCharacteristics.SENSOR_INFO_TIMESTAMP_SOURCE);
                if (timestampSource != null) {
                    switch (timestampSource) {
                        case CameraCharacteristics.SENSOR_INFO_TIMESTAMP_SOURCE_UNKNOWN:
                            timestampSourceText = requireContext().getString(R.string.timestamp_unknown);
                            break;
                        case CameraCharacteristics.SENSOR_INFO_TIMESTAMP_SOURCE_REALTIME:
                            timestampSourceText = requireContext().getString(R.string.timestamp_realtime);
                            break;
                        default:
                            timestampSourceText = requireContext().getString(R.string.timestamp_other);
                            break;
                    }
                }
                break;
            }
        } catch (CameraAccessException e) {
            timestampSourceText = requireContext().getString(R.string.timestamp_na);
        }
        tvTimestampSource.setText(timestampSourceText);
    }

    private void setCameraOrientation() {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                Integer orientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                if (orientation != null) {
                    String formattedOrientation = orientation + " deg";
                    tvCameraOrientation.setText(formattedOrientation);
                } else {
                    tvCameraOrientation.setText(context.getString(R.string.not_available));
                }
                break;
            }
        } catch (CameraAccessException e) {
            tvCameraOrientation.setText(context.getString(R.string.not_available));
        }
    }

    private void setFaceDetectionModes() {
        Map<Integer, String> faceDetectionModeMap = new HashMap<>();
        faceDetectionModeMap.put(0, context.getString(R.string.face_detection_off));
        faceDetectionModeMap.put(1, context.getString(R.string.face_detection_simple));
        faceDetectionModeMap.put(2, context.getString(R.string.face_detection_full));

        Set<String> modesSet = new LinkedHashSet<>();

        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                int[] modes = characteristics.get(CameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES);

                if (modes != null) {
                    for (int mode : modes) {
                        String modeName = faceDetectionModeMap.getOrDefault(mode, context.getString(R.string.unknown) + mode);
                        modesSet.add(modeName);
                    }
                }
                break;
            }

            String result = modesSet.isEmpty() ? context.getString(R.string.not_available) : String.join(", ", modesSet);
            tvFaceDetection.setText(result);

        } catch (CameraAccessException e) {
            tvFaceDetection.setText(context.getString(R.string.not_available));
        }
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