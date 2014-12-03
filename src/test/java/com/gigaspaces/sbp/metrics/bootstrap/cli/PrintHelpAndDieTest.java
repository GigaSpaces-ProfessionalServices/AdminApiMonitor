package com.gigaspaces.sbp.metrics.bootstrap.cli;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.PrintStream;
import java.io.PrintWriter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PrintHelpAndDieTest {

    @Mock
    private AbstractApplicationContext context;
    @Mock
    private PrintHelpAndDie.HelpFormatterFactory helpFormatterFactory;
    @Mock
    private PrintHelpAndDie.PrintWriterFactory printWriterFactory;
    @Mock
    private HelpFormatter testHelpFormatter;
    @Mock
    private PrintWriter testPrintWriter;
    @Mock
    private CalculateSettingsFromCliArgs calculateSettingsFromCliArgs;
    @Mock
    private Options options;

    private PrintHelpAndDie testInstance;

    @Before
    public void setUp() throws Exception {

        testInstance = new PrintHelpAndDie(context);

        testInstance.helpFormatterFactory = helpFormatterFactory;
        testInstance.printWriterFactory = printWriterFactory;

        doReturn(testPrintWriter).when(printWriterFactory).build();
        doReturn(testHelpFormatter).when(helpFormatterFactory).build();
        doReturn(calculateSettingsFromCliArgs).when(context).getBean(eq("calculateSettingsFromCliArgs"));
        doReturn(options).when(calculateSettingsFromCliArgs).allOptions();

    }

    @Test
    public void testInvoke() throws Exception {

        ParseException exception = mock(ParseException.class);

        Integer actual = testInstance.invoke(exception);

        verify(testHelpFormatter).printHelp(same(testPrintWriter), anyInt(), anyString(), anyString(), same(options), anyInt(),  anyInt(), anyString(), eq(Boolean.TRUE) );
        assertTrue(actual > 0);

    }

    @Test
    public void testHelpFormatterFactoryBuild() throws Exception{
        assertNotNull(testInstance.new HelpFormatterFactory().build());
    }

    @Test
    public void testPrintWriterFactoryBuild() throws Exception{
        assertNotNull(testInstance.new PrintWriterFactory(mock(PrintStream.class)).build());
    }

}