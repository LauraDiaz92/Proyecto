package com.example.diagsmartv2.sensors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diagsmartv2.R;

import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.SensorViewHolder> {

    List<SensorInfo> sensorInfoList;
    OnItemClickListener onItemClickListener;

    public SensorAdapter(List<SensorInfo> sensorInfoList, OnItemClickListener onItemClickListener) {
        this.sensorInfoList = sensorInfoList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sensors_list, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder holder, int position) {
        SensorInfo sensorInfo = sensorInfoList.get(position);
        holder.bind(sensorInfo);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(sensorInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sensorInfoList.size();
    }

    public static class SensorViewHolder extends RecyclerView.ViewHolder {

        TextView tvSensorName, tvSensorVendor, tvSensorType;
        ImageView ivPlay;

        public SensorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSensorName = itemView.findViewById(R.id.tvSensorName);
            tvSensorVendor = itemView.findViewById(R.id.tvSensorVendor);
            tvSensorType = itemView.findViewById(R.id.tvSensorType);
            ivPlay = itemView.findViewById(R.id.ivPlay);
        }

        public void bind(SensorInfo sensorInfo) {
            tvSensorName.setText(sensorInfo.getName());
            tvSensorVendor.setText(sensorInfo.getVendor());
            tvSensorType.setText(sensorInfo.getType());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(SensorInfo sensorInfo);
    }
}
