package com.gigaspaces.sbp.metrics.bootstrap;

import com.gigaspaces.sbp.metrics.cli.Settings;
import com.gigaspaces.sbp.metrics.bootstrap.GsMonitorSettings;
import com.jasonnerothin.testing.Strings;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GsMonitorSettingsTest {

    private GsMonitorSettings testInstance;

    private Strings strings = new Strings();

    @Before
    public void setUp() throws Exception {
        testInstance = new GsMonitorSettings();
    }

    @Test(expected = IllegalStateException.class)
    public void testGetOutputFilePathThrowsWithoutInitialization() throws Exception {
        testInstance.getOutputFilePath();
    }

    @Test
    public void testGetOutputFilePath() throws Exception{

        final String testFilename = strings.alphabetic();

        HashMap<Settings,String> map = new HashMap<Settings,String>(){{put(Settings.OutputFile,testFilename);}};
        testInstance.initialize(map);

        assertEquals(testFilename, testInstance.getOutputFilePath());
    }

    @Test
    public void testGetoutputFilePathIsNullWhenInitializationDoesNotIncludeOutputFile() throws Exception{

        testInstance.initialize(new HashMap<Settings,String>(){{put(Settings.Csv, "true");}});

        assertNull(testInstance.getOutputFilePath());

    }
}