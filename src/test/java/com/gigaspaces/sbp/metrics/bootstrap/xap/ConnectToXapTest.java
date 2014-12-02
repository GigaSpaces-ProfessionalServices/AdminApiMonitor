package com.gigaspaces.sbp.metrics.bootstrap.xap;

import com.gigaspaces.sbp.metrics.Constants;
import com.gigaspaces.sbp.metrics.bootstrap.GsMonitorSettings;
import com.jasonnerothin.testing.Numbers;
import com.jasonnerothin.testing.Strings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.gsc.GridServiceContainers;
import org.openspaces.admin.machine.Machines;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConnectToXapTest {

    private ConnectToXap testInstance;

    @Mock
    private GsMonitorSettings monitorSettings;
    @Mock
    private ConfigureAlerts configureAlerts;
    @Mock
    private SpaceConnections spaceConnections;
    @Mock
    private Admin admin;

    @Mock
    private AdminFactoryFactory adminFactoryFactory;
    @Mock
    private AdminFactory adminFactory;

    @Mock
    private ConnectToSpace connectToSpace0;
    @Mock
    private ConnectToSpace connectToSpace1;

    private String spaceName0, spaceName1;
    private String lookupLocator0, lookupLocator1;
    private String testLocators;

    private Strings strings;
    private Numbers numbers;

    @Mock
    private Machines machines;
    @Mock
    private GridServiceContainers GSCs;

    @Before
    public void setUp() throws Exception {

        strings = new Strings();
        numbers = new Numbers();

        lookupLocator0 = strings.alphabetic();
        lookupLocator1 = strings.alphabetic();
        spaceName0 = strings.alphabetic();
        spaceName1 = strings.alphabetic();
        testLocators = lookupLocator0 + Constants.LIST_ITEM_SEPARATOR + lookupLocator1;

        doReturn(new String[]{spaceName0, spaceName1}).when(monitorSettings).spaceNames();
        doReturn(testLocators).when(monitorSettings).lookupLocators();
        doReturn(connectToSpace0).when(spaceConnections).connect(spaceName0);
        doReturn(connectToSpace1).when(spaceConnections).connect(spaceName1);
        doReturn(adminFactory).when(adminFactoryFactory).build();
        doReturn(admin).when(adminFactory).create();
        doReturn(admin).when(adminFactory).createAdmin();
        doReturn(machines).when(admin).getMachines();
        doReturn(GSCs).when(admin).getGridServiceContainers();

        testInstance = new ConnectToXap(
                monitorSettings
                , configureAlerts
                , spaceConnections
                , adminFactoryFactory
        );
    }

    @Test
    public void testInvokeEstablishesLookups() throws Exception {

        testInstance.invoke();

        verify(adminFactory, times(1)).addLocators(testLocators);

    }

    @Test
    public void testLocatorGroupsNotUsedWhenLocatorGroupsAreEmptyString() {

        doReturn("").when(monitorSettings).lookupGroups();

        testInstance.invoke();

        verify(adminFactory, never()).addGroup(anyString());
        verify(adminFactory, never()).addGroups(anyString());

    }

    @Test
    public void testLocatorGroupsNotUsedWhenLocatorGroupsAreNull() {

        testInstance.invoke();

        verify(adminFactory, never()).addGroup(anyString());
        verify(adminFactory, never()).addGroups(anyString());

    }

    @Test
    public void testInvokeCausesConnectionsToMultipleNamedSpaces() throws Exception {

        testInstance.invoke();

        verify(connectToSpace0, times(1)).invoke(admin);
        verify(connectToSpace1, times(1)).invoke(admin);

    }

    @Test
    public void testInvokeConnectsToMachines() throws Exception {

        Integer numMachines = numbers.positiveInteger();
        doReturn(numMachines).when(monitorSettings).machineCount();

        testInstance.invoke();

        verify(admin, times(1)).getMachines();
        verify(machines, times(1)).waitFor(eq(numMachines));

    }

    @Test
    public void testInvokeConnectsToGSCs() throws Exception {

        Integer numGSCs = numbers.positiveInteger();
        doReturn(numGSCs).when(monitorSettings).gscCount();

        testInstance.invoke();

        verify(admin, times(1)).getGridServiceContainers();
        verify(GSCs, times(1)).waitFor(eq(numGSCs));

    }

    /**
     * Since space connection should greatly speed up connection to GSCs, we want to force
     * that connection attempt to *begin* first (it's an optimization, not a requirement)
     *
     * @throws Exception never
     */
    @Test
    public void testInvokeGscConnectionsStartsAfterConnectToSpaceHappens() throws Exception {

        Integer numGSCs = numbers.positiveInteger();
        doReturn(numGSCs).when(monitorSettings).gscCount();

        testInstance.invoke();

        InOrder inOrder = inOrder(spaceConnections, GSCs);
        inOrder.verify(spaceConnections, times(2)).connect(anyString());
        inOrder.verify(GSCs).waitFor(eq(numGSCs));

    }

    /**
     * Since space connection should greatly speed up connection to Machines, we want to force
     * that connection attempt to *begin* first (it's an optimization, not a requirement)
     *
     * @throws Exception never
     */
    @Test
    public void testInvokeMachineConnectionsStartsAfterConnectToSpaceHappens() throws Exception {

        Integer numMachines = numbers.positiveInteger();
        doReturn(numMachines).when(monitorSettings).machineCount();

        testInstance.invoke();

        InOrder spacesBeforeMachines = inOrder(spaceConnections, machines);
        spacesBeforeMachines.verify(spaceConnections, times(2)).connect(anyString());
        spacesBeforeMachines.verify(machines).waitFor(eq(numMachines));

    }

}