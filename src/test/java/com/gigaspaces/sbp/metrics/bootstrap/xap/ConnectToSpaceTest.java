package com.gigaspaces.sbp.metrics.bootstrap.xap;

import com.jasonnerothin.testing.Strings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openspaces.admin.Admin;
import org.openspaces.admin.space.Space;
import org.openspaces.admin.space.SpaceInstance;
import org.openspaces.admin.space.Spaces;
import org.openspaces.admin.transport.Transport;
import org.openspaces.admin.transport.TransportLRMIMonitoring;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConnectToSpaceTest {

    private ConnectToSpace testInstance;

    private Strings strings = new Strings();
    private String testSpaceName;

    @Mock
    private Admin admin;
    @Mock
    private Spaces spaces;
    @Mock
    private Space space;
    @Mock
    private SpaceInstance inst0;
    @Mock
    private SpaceInstance inst1;
    @Mock
    private Transport transport;
    @Mock
    private TransportLRMIMonitoring monitoring;

    @Before
    public void setUp() throws Exception {

        testSpaceName = strings.alphabetic();
        testInstance = new ConnectToSpace(testSpaceName, admin);

        doReturn(spaces).when(admin).getSpaces();
        doReturn(space).when(spaces).waitFor(eq(testSpaceName));

        doReturn(new SpaceInstance[]{inst0, inst1}).when(space).getInstances();
        doReturn(monitoring).when(transport).getLRMIMonitoring();
        doReturn(transport).when(inst0).getTransport();
        doReturn(transport).when(inst1).getTransport();

    }

    @Test
    public void testInvokeCausesWaitForSpaceName() throws Exception {

        testInstance.run();

        verify(spaces).waitFor(eq(testSpaceName));

    }

    @Test
    public void testInvokeCausesLrmiMonitoringToBeEnabledOnAllSpaceInstances() throws Exception {


        testInstance.run();

        verify(monitoring, times(2)).enableMonitoring(); // one for each of the two SpaceInstances

    }

}