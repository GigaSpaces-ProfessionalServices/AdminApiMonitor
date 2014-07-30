package com.gigaspaces.sbp.metrics.cli;

import com.gigaspaces.sbp.metrics.Settings;
import org.junit.Before;
import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 7/29/14
 * Time: 8:45 PM
 */
public class ProcessArgsTest {

    private static final String ALL_OPTION = String.format("-%s", Settings.AllMetrics.getOptionCharacter());
    private static final String CSV_OPTION = String.format("-%s", Settings.Csv.getOptionCharacter());

    private ProcessArgs testInstance;

    @Before
    public void setUp() throws Exception {
        testInstance = new ProcessArgs();
    }

    @Test
    public void testInvoke(){


        EnumSet<Settings> actual = testInstance.invoke(new String[]{ALL_OPTION, CSV_OPTION});
        assertTrue(actual.contains(Settings.AllMetrics));
        assertTrue(actual.contains(Settings.Csv));

        actual = testInstance.invoke(new String[]{});
        assertNotNull(actual);

    }

    @Test
    public void testInvokeRequiresHyphens(){

        EnumSet<Settings> actual = testInstance.invoke(new String[]{Settings.AllMetrics.getOptionCharacter()});
        assertNotNull(actual);

        assertEquals(actual.size(), 0, 0);
    }

    @Test
    public void testInvokeDoesNotThrow(){
        testInstance.invoke(new String[]{"xyz", "pdq"});
    }
}
