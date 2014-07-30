package com.gigaspaces.sbp.metrics.cli;

import com.gigaspaces.sbp.metrics.Settings;
import org.apache.commons.cli.ParseException;
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
    private static final String SECURED_OPTION = String.format("-%s", Settings.Secured.getOptionCharacter());

    private ProcessArgs testInstance;

    @Before
    public void setUp() throws Exception {
        testInstance = new ProcessArgs();
    }

    @Test
    public void testInvoke() throws Exception{


        EnumSet<Settings> actual = testInstance.invoke(new String[]{ALL_OPTION, SECURED_OPTION, CSV_OPTION});
        assertTrue(actual.contains(Settings.AllMetrics));
        assertTrue(actual.contains(Settings.Csv));
        assertTrue(actual.contains(Settings.Secured));

        actual = testInstance.invoke(new String[]{});
        assertNotNull(actual);

    }

    @Test
    public void testInvokeRequiresHyphens() throws Exception{

        EnumSet<Settings> actual = testInstance.invoke(new String[]{Settings.AllMetrics.getOptionCharacter()});
        assertNotNull(actual);

        assertEquals(actual.size(), 0, 0);
    }

    @Test(expected = ParseException.class)
    public void testInvokeThrows() throws Exception{
        testInstance.invoke(new String[]{"-xyz", "-pdq"});
    }
}
