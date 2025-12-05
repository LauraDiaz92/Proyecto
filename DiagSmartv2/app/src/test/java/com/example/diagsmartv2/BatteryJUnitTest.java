package com.example.diagsmartv2;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.diagsmartv2.utils.BatteryUtils;


public class BatteryJUnitTest {

        @Test
        public void lowBattery_whenBelow20() {
            assertEquals("LOW", BatteryUtils.getBatteryLevelCategory(5));
        }

        @Test
        public void mediumBattery_between20And79() {
            assertEquals("MEDIUM", BatteryUtils.getBatteryLevelCategory(50));
        }

        @Test
        public void highBattery_when80OrMore() {
            assertEquals("HIGH", BatteryUtils.getBatteryLevelCategory(95));
        }

        @Test(expected = IllegalArgumentException.class)
        public void throwsWhenInvalidPercent() {
            BatteryUtils.getBatteryLevelCategory(150);
        }

}
