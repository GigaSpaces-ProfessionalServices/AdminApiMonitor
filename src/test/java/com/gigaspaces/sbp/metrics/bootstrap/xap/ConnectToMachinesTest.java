package com.gigaspaces.sbp.metrics.bootstrap.xap;

import com.gigaspaces.sbp.metrics.bootstrap.GsMonitorSettings;
import com.jasonnerothin.testing.Numbers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openspaces.admin.Admin;
import org.openspaces.admin.machine.Machines;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ConnectToMachinesTest {

    private ConnectToMachines testInstance;
    @Mock
    private Admin admin;
    @Mock
    private GsMonitorSettings settings;
    @Mock
    private Machines testMachines;

    private Integer testNumMachines;
    private Numbers numbers;

    @Before
    public void setUp() throws Exception {
        testInstance = new ConnectToMachines(admin, settings);

        doReturn(testMachines).when(admin).getMachines();
        numbers = new Numbers();
        testNumMachines = numbers.positiveInteger();

    }

    @Test
    public void testRun() throws Exception {
        doReturn(testNumMachines).when(settings).machineCount();

        testInstance.run();

        verify(testMachines).waitFor(eq(testNumMachines));

    }
}