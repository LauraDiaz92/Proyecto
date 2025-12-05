package com.example.diagsmartv2.utils;

public class TestsStatusUtils {

    /**
     * Counts how many tests are marked as passed in the given results array.
     *
     * @param results array of test results.
     * @return number of elements equal to "PASSED".
     */
        public static int countPassed(String[] results) {
            int count = 0;
            for (String r : results) {
                if ("PASSED".equals(r)) count++;
            }
            return count;
        }

    /**
     * Counts how many tests are marked as failed in the given results array.
     *
     * @param results array of test results.
     * @return number of elements equal to "FAILED".
     */
        public static int countFailed(String[] results) {
            int count = 0;
            for (String r : results) {
                if ("FAILED".equals(r)) count++;
            }
            return count;
        }

    /**
     * Counts how many tests are still pending in the given results array.
     *
     * @param results array of test results.
     * @return number of elements equal to "PENDING".
     */
        public static int countPending(String[] results) {
            int count = 0;
            for (String r : results) {
                if ("PENDING".equals(r)) count++;
            }
            return count;
        }
}
