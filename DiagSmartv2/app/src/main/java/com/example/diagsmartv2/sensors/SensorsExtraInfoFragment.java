package com.example.diagsmartv2.sensors;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.diagsmartv2.R;

public class SensorsExtraInfoFragment extends DialogFragment {

    private static final String ARG_SENSOR_INFO = "sensorInfo";
    private SensorInfo sensorInfo;

    TextView tvSensorNameII, tvSensorVendorII, tvSensorTypeII, tvSensorPower,
            tvSensorResolution, tvSensorWakeUp, tvSensorDynamic, tvMaxRange;

    public SensorsExtraInfoFragment() {}

    public static SensorsExtraInfoFragment newInstance(SensorInfo sensorInfo) {
        SensorsExtraInfoFragment fragment = new SensorsExtraInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SENSOR_INFO, sensorInfo);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Builds and returns the dialog, inflating the custom view, restoring the
     * SensorInfo from arguments and populating all extra sensor fields.
     *
     * @param savedInstanceState previously saved state, if any.
     * @return the created AlertDialog instance.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_sensors_extra_info, null);

        if (getArguments() != null)
            sensorInfo = getArguments().getParcelable(ARG_SENSOR_INFO);

        initReferences(view);
        setExtraInfo();

        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .setPositiveButton("Cerrar", null)
                .create();
    }

    /**
     * Finds and stores references to all TextViews used to display extra sensor data.
     *
     * @param view inflated dialog content view.
     */
    private void initReferences(View view) {
        tvSensorNameII = view.findViewById(R.id.tvSensorNameII);
        tvSensorVendorII = view.findViewById(R.id.tvSensorVendorII);
        tvSensorTypeII = view.findViewById(R.id.tvSensorTypeII);
        tvSensorPower = view.findViewById(R.id.tvSensorPower);
        tvSensorResolution = view.findViewById(R.id.tvSensorResolution);
        tvSensorWakeUp = view.findViewById(R.id.tvSensorWakeup);
        tvSensorDynamic = view.findViewById(R.id.tvSensorDynamic);
        tvMaxRange = view.findViewById(R.id.tvSensorMaxRange);
    }

    /**
     * Fills the dialog views with details from the SensorInfo object
     * (name, vendor, type, power, resolution, range and flags).
     */
    private void setExtraInfo() {
        if (sensorInfo != null) {
            tvSensorNameII.setText(sensorInfo.getName());
            tvSensorVendorII.setText(sensorInfo.getVendor());
            tvSensorTypeII.setText(sensorInfo.getType());
            tvSensorPower.setText(sensorInfo.getPower());
            tvSensorResolution.setText(sensorInfo.getResolution());
            tvMaxRange.setText(sensorInfo.getMaxRange());
            tvSensorWakeUp.setText(sensorInfo.getWakeupSensor());
            tvSensorDynamic.setText(sensorInfo.getDynamicSensor());
        }
    }
}
