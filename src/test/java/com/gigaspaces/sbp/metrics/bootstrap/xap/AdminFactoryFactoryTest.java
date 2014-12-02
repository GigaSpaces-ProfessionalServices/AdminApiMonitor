package com.gigaspaces.sbp.metrics.bootstrap.xap;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class AdminFactoryFactoryTest {

    @Test
    public void testBuild() throws Exception {
        assertNotNull(new AdminFactoryFactory().build());
    }
}