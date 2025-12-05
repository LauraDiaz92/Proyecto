package com.example.diagsmartv2;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.diagsmartv2.utils.TestsStatusUtils;

public class TestStatusJUnitTest {

        @Test
        public void countsEachStatusCorrectly() {
            String[] results = {"PASSED", "FAILED", "PENDING", "PASSED", "FAILED"};

            assertEquals(2, TestsStatusUtils.countPassed(results));
            assertEquals(2, TestsStatusUtils.countFailed(results));
            assertEquals(1, TestsStatusUtils.countPending(results));
        }


}
