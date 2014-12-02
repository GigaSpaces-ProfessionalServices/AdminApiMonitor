package com.gigaspaces.sbp.metrics;

import com.gigaspaces.sbp.metrics.bootstrap.BootSpring;
import com.gigaspaces.sbp.metrics.bootstrap.CreateGsMonitorSettings;
import com.gigaspaces.sbp.metrics.bootstrap.GsMonitorSettingsImpl;
import com.gigaspaces.sbp.metrics.bootstrap.cli.PrintHelpAndDie;
import com.gigaspaces.sbp.metrics.bootstrap.xap.ConnectToXap;
import com.jasonnerothin.testing.Strings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.support.AbstractApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MainTest {

    @Mock
    private BootSpring bootSpring;
    @Mock
    private CollectMetrics collectMetrics;
    @Mock
    private PrintHelpAndDie printHelpAndDie;
    @Mock
    private AbstractApplicationContext applicationContext;
    @Mock
    private CreateGsMonitorSettings createGsMonitorSettings;
    @Mock
    private ConnectToXap connectToXap;
    @Mock
    private GsMonitorSettingsImpl gsMonitorSettings;

    private Strings strings;

    private Main testInstance;

    @Before
    public void setUp() throws Exception {

        testInstance = new Main();

        testInstance.bootSpring = bootSpring;
        testInstance.collectMetrics = collectMetrics;
        testInstance.printHelpAndDie = printHelpAndDie;
        testInstance.context = applicationContext;
        testInstance.connectToXap = connectToXap;

        doReturn(createGsMonitorSettings).when(applicationContext).getBean(eq(Main.beanNameForClass(CreateGsMonitorSettings.class)));
        doReturn(gsMonitorSettings).when(applicationContext).getBean(eq(Main.beanNameForClass(GsMonitorSettingsImpl.class)));
        doReturn(connectToXap).when(applicationContext).getBean(eq(Main.beanNameForClass(ConnectToXap.class)));
        doReturn(collectMetrics).when(applicationContext).getBean(eq(Main.beanNameForClass(CollectMetrics.class)));

        doReturn(applicationContext).when(bootSpring).readySetGo();

        strings = new Strings();

    }


    @Test
    public void testBootSpring() throws Exception {

        testInstance.bootSpring();

        verify(bootSpring).readySetGo();

    }

    @Test
    public void testProcessCommandLine() throws Exception {



        String[] testArgs = new String[]{};

        mockOutputFilePath();
        testInstance.processCommandLine(testArgs);

        verify(createGsMonitorSettings).invokeOrThrow(same(testArgs));

    }

    @Test
    public void testConnectToXapGrids() throws Exception {

        testInstance.connectToXapGrids();

        verify(connectToXap).invoke();

    }

    @Test
    public void testRun() throws Exception {

        testInstance.run();

        verify(collectMetrics).readySetGo();

    }

    @Test
    public void testSetOutputFile() throws Exception {

        String testPath = mockOutputFilePath();

        testInstance.setOutputFile();

        String actual = System.getProperty("outputFile");

        assertEquals(actual, testPath);

    }

    private String mockOutputFilePath() {
        String testPath = strings.alphabetic();
        doReturn(testPath).when(gsMonitorSettings).outputFilePath();
        return testPath;
    }
}