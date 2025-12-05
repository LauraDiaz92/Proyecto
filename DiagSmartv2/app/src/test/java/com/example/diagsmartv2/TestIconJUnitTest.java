package com.example.diagsmartv2;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.diagsmartv2.utils.TestsIconUtils;

public class TestIconJUnitTest {

        @Test
        public void iconGreen_whenPassed() {
            assertEquals("GREEN", TestsIconUtils.getIconColor("PASSED"));
        }

        @Test
        public void iconRed_whenFailed() {
            assertEquals("RED", TestsIconUtils.getIconColor("FAILED"));
        }

        @Test
        public void iconYellow_whenPending() {
            assertEquals("YELLOW", TestsIconUtils.getIconColor("PENDING"));
        }

        @Test
        public void iconGray_whenUnknown() {
            assertEquals("GRAY", TestsIconUtils.getIconColor("OTHER"));
        }

}
