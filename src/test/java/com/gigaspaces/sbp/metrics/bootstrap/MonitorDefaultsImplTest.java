package com.gigaspaces.sbp.metrics.bootstrap;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public final class MonitorDefaultsImplTest {

    private static final String TEST_FILE_NAME = "properties/gs.test.monitor.properties";

    private MonitorDefaultsImpl testInstance;

    @Before
    public void setUp() throws Exception {
        testInstance = new MonitorDefaultsImpl(TEST_FILE_NAME);
    }

    @Test
    public void testIsEmailAlertingEnabled() throws Exception {
        assertFalse(testInstance.isEmailAlertingEnabled());
    }

    @Test
    public void testEmailAlertIntervalInMs() throws Exception {
        assertEquals(2, testInstance.emailAlertIntervalInMs(), 0);
    }

    @Test
    public void testEmailAlertDelayInMs() throws Exception {
        assertEquals(4, testInstance.emailAlertDelayInMs(), 0);
    }

    @Test
    public void testMetricIntervalInMs() throws Exception {
        assertEquals(1, testInstance.metricIntervalInMs(), 0);
    }

    @Test
    public void testMetricDelayInMs() throws Exception {
        assertEquals(3, testInstance.metricDelayInMs(), 0);
    }

    @Test
    public void testMetricAlpha() throws Exception {
        assertEquals(2.71828, testInstance.metricAlpha(), 0.0005);
    }

    @Test(expected = ExceptionInInitializerError.class)
    public void testBogusPropFileNameThrows() throws Exception{
        new MonitorDefaultsImpl("bibbity.bobbity.boo.properties");
    }

    @Test
    public void testOutputFile() throws Exception{
        assertEquals("myfile.txt", testInstance.outputFilename());
    }
}