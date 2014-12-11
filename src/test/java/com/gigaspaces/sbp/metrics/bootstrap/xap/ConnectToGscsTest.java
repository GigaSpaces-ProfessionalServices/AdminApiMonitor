package com.gigaspaces.sbp.metrics.bootstrap.xap;

import com.gigaspaces.sbp.metrics.bootstrap.GsMonitorSettings;
import com.jasonnerothin.testing.Numbers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openspaces.admin.Admin;
import org.openspaces.admin.gsc.GridServiceContainers;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ConnectToGscsTest {

    @Mock
    private GsMonitorSettings settings;
    @Mock
    private Admin admin;
    @Mock
    private GridServiceContainers gscs;

    private ConnectToGscs testInstance;

    private Numbers numbers ;
    private Integer testNumGscs;

    @Before
    public void setUp() throws Exception {

        testInstance = new ConnectToGscs(admin, settings);

        numbers = new Numbers();
        testNumGscs = numbers.positiveInteger();

        doReturn(gscs).when(admin).getGridServiceContainers();

    }

    @Test
    public void testRun() throws Exception {

        doReturn(testNumGscs).when(settings).gscCount();

        testInstance.run();

        verify(gscs).waitFor(eq(testNumGscs));

    }
}