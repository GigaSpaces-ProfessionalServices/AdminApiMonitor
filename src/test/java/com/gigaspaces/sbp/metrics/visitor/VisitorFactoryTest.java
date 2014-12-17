package com.gigaspaces.sbp.metrics.visitor;

import com.gigaspaces.sbp.metrics.ExponentialMovingAverage;
import com.gigaspaces.sbp.metrics.bootstrap.GsMonitorSettings;
import com.gigaspaces.sbp.metrics.bootstrap.cli.OutputFormat;
import com.gigaspaces.sbp.metrics.bootstrap.xap.ConnectToXap;
import com.gigaspaces.sbp.metrics.metric.MetricsRegistry;
import com.jasonnerothin.testing.Numbers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openspaces.admin.Admin;
import org.openspaces.admin.gsc.GridServiceContainer;
import org.openspaces.admin.gsc.GridServiceContainers;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class VisitorFactoryTest {

    private VisitorFactory testInstance;

    @Mock
    private GsMonitorSettings settings;
    @Mock
    private ExponentialMovingAverage average;
    @Mock
    private ConnectToXap connectToXap;

    private Numbers numbers;

    @Mock
    private Admin testAdmin;
    @Mock
    private GridServiceContainers testGridServiceContainers;
    @Mock
    private MetricsRegistry metricsRegistry;

    @Before
    public void setUp() throws Exception {

        testInstance = new VisitorFactory(settings, average, connectToXap, metricsRegistry);
        numbers = new Numbers();

        doReturn(testAdmin).when(connectToXap).getAdmin();

        doReturn(numbers.positiveLong()).when(settings).derivedMetricsPeriodInMs();
        doReturn(new String[]{}).when(settings).spaceNames();

        doReturn(testGridServiceContainers).when(testAdmin).getGridServiceContainers();
        doReturn(new GridServiceContainer[]{}).when(testGridServiceContainers).getContainers();

    }

    @Test
    public void testBuildCsv() throws Exception {

        doReturn(OutputFormat.Csv).when(settings).outputFormat();

        assertNotNull(testInstance.build());

    }

    @Test
    public void testBuildLog() throws Exception {

        doReturn(OutputFormat.LogFormat).when(settings).outputFormat();

        assertNotNull(testInstance.build());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testBuildThrowsForUnsupportedFormat() throws Exception {

        doReturn(OutputFormat.InfluxDb).when(settings).outputFormat();

        testInstance.build();
    }

}