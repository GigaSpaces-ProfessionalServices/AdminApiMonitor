package com.gigaspaces.sbp.metrics.bootstrap.props;

import com.gigaspaces.sbp.metrics.bootstrap.props.XapDefaultsImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class XapDefaultsImplTest {

    private static final String TEST_PROPERTY_FILENAME = "properties/xap.test.default.properties";

    private XapDefaultsImpl testInstance;


    @Before
    public void setUp() throws Exception {
        testInstance = new XapDefaultsImpl(TEST_PROPERTY_FILENAME);
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
        new XapDefaultsImpl("foobarbaz.properties");
    }
}