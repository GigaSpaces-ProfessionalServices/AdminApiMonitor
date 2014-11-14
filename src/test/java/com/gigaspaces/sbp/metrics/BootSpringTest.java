package com.gigaspaces.sbp.metrics;

import com.gigaspaces.sbp.metrics.bootstrap.BootSpring;
import org.junit.Before;
import org.junit.Test;

public class BootSpringTest {

    private BootSpring testInstance;

    @Before
    public void setup(){
        testInstance = new BootSpring();
    }

    @Test
    public void testReadySetGo() throws Exception {
        testInstance.readySetGo();
    }
}