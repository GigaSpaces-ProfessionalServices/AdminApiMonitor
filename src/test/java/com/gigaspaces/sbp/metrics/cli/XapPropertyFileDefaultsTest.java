package com.gigaspaces.sbp.metrics.cli;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public final class XapPropertyFileDefaultsTest {

    private static final String TEST_PROPERTY_FILENAME = "properties/xap.test.default.properties";

    private XapPropertyFileDefaults testInstance;


    @Before
    public void setUp() throws Exception {
        testInstance = new XapPropertyFileDefaults(TEST_PROPERTY_FILENAME);
    }

    @Test
    public void testLookupLocators() throws Exception {
        assertEquals(testInstance.lookupLocators(), "up:8282");
    }

    @Test
    public void testSpaceNames() throws Exception {
        assertEquals(testInstance.spaceNames(), "large,small");
    }

    @Test
    public void testOutputFile() throws Exception {
        assertEquals(testInstance.outputFile(), "sAndM.csv");
    }

    @Test
    public void testIsSecured() throws Exception {
        assertTrue(testInstance.isSecured());
    }

    @Test(expected = ExceptionInInitializerError.class)
    public void testCtor() throws Exception{
        new XapPropertyFileDefaults("foobarbaz.properties");
    }
}