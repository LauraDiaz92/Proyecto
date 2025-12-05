package com.example.diagsmartv2.utils;

public class TestsIconUtils {

    /**
     * Returns the icon color associated with a test result.
     *
     * @param result logical test result ("PASSED", "FAILED", "PENDING" or other).
     * @return "GREEN", "RED", "YELLOW" or "GRAY" depending on the given result.
     */
    public static String getIconColor(String result) {
        if ("PASSED".equals(result)) {
            return "GREEN";
        } else if ("FAILED".equals(result)) {
            return "RED";
        } else if ("PENDING".equals(result)) {
            return "YELLOW";
        } else {
            return "GRAY";
        }
    }
}
