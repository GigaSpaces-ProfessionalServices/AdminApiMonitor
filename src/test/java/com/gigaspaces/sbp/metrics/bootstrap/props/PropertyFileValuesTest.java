package com.gigaspaces.sbp.metrics.bootstrap.props;

import com.jasonnerothin.testing.Strings;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PropertyFileValuesTest {

    private static final String TEST_PROP_FILE = "properties/PropertyFileValuesTest.properties";

    private PropertyFileValues testInstance;
    private Strings strings;

    @Before
    public void setUp() throws Exception {
        strings = new Strings();
        testInstance = new PropertyFileValues(TEST_PROP_FILE) {
        };
    }

    @Test(expected = IllegalStateException.class)
    public void testGetPropOrThrowThrows() throws Exception {
        testInstance.getPropOrThrow(strings.alphabetic());
    }

    @Test
    public void testGetPropOrThrowGets(){
        assertEquals(testInstance.getPropOrThrow("foo"), "bar");
    }
}