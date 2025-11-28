package com.example.diagsmartv2.tests;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diagsmartv2.R;

public class TestsInteractiveAdapter extends RecyclerView.Adapter<TestsInteractiveAdapter.TestInteractiveViewHolder> {

    private final String[] interactiveTestsTitles = {
            "Display", "Multitouch", "Flashlight", "Loudspeaker", "Ear Speaker", "Microphone",
            "Ear Proximity", "Light Sensor", "Charging", "Bluetooth",
            "Volume Up Button", "Volume Down Button"
    };

    private final Context context;
    private final OnTestSelectedListener onTestSelectedListener;

    public interface OnTestSelectedListener {
        void onTestSelected(int position, String testName);
    }

    public TestsInteractiveAdapter(Context context, OnTestSelectedListener listener) {
        this.context = context;
        this.onTestSelectedListener = listener;
    }

    @NonNull
    @Override
    public TestInteractiveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tests_list, parent, false);
        return new TestInteractiveViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TestInteractiveViewHolder holder, int position) {
        holder.tvTestName.setText(interactiveTestsTitles[position]);

        // Obtener estado guardado en SharedPreferences para cada test
        SharedPreferences prefs = context.getSharedPreferences("test_status", Context.MODE_PRIVATE);
        String key = interactiveTestsTitles[position].toLowerCase().replace(" ", "_") + "_test_status";
        String status = prefs.getString(key, "default");
        updateIcon(holder.ivTestIcon, status);

        holder.itemView.setOnClickListener(v -> {
            if (onTestSelectedListener != null) {
                onTestSelectedListener.onTestSelected(position, interactiveTestsTitles[position]);
            }
        });
    }

    private void updateIcon(ImageView iv, String status) {
        switch (status) {
            case "approved": iv.setImageResource(R.drawable.baseline_check_circle_24); break;
            case "failed": iv.setImageResource(R.drawable.baseline_cancel_24); break;
            default: iv.setImageResource(R.drawable.baseline_help_24); break;
        }
    }

    @Override
    public int getItemCount() {
        return interactiveTestsTitles.length;
    }

    public static class TestInteractiveViewHolder extends RecyclerView.ViewHolder {
        TextView tvTestName;
        ImageView ivTestIcon;

        public TestInteractiveViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTestName = itemView.findViewById(R.id.tvTestName);
            ivTestIcon = itemView.findViewById(R.id.ivTestIcon);
        }
    }
}
