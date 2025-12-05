package com.example.diagsmartv2.utils;

public class BatteryUtils {

    /**
     * Calculates the battery level category from a percentage.
     *
     * @param percent battery percentage in the range 0 to 100.
     * @return "LOW", "MEDIUM" or "HIGH" depending on the percentage range.
     * @throws IllegalArgumentException if the percentage is outside the range 0–100.
     */
    public static String getBatteryLevelCategory(int percent) {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("percent must be 0-100");
        }
        if (percent < 20) {
            return "LOW";
        } else if (percent < 80) {
            return "MEDIUM";
        } else {
            return "HIGH";
        }
    }
}
