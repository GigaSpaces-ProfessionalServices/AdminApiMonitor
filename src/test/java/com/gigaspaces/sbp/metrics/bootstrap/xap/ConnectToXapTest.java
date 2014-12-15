package com.gigaspaces.sbp.metrics.bootstrap.xap;

import akka.actor.ActorSystem;
import com.gigaspaces.sbp.metrics.Constants;
import com.gigaspaces.sbp.metrics.ActorSystemEden;
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
import scala.concurrent.ExecutionContextExecutor;

import static org.junit.Assert.assertSame;
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
    @Mock
    private ActorSystemEden contextHolder;
    @Mock
    private ActorSystem actorSystem;
    @Mock
    private ExecutionContextExecutor contextExecutor;

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

        String spaceName0, spaceName1;
        String lookupLocator0, lookupLocator1;

        lookupLocator0 = strings.alphabetic();
        lookupLocator1 = strings.alphabetic();
        spaceName0 = strings.alphabetic();
        spaceName1 = strings.alphabetic();
        testLocators = lookupLocator0 + Constants.LIST_ITEM_SEPARATOR + lookupLocator1;

        doReturn(new String[]{spaceName0, spaceName1}).when(monitorSettings).spaceNames();
        doReturn(testLocators).when(monitorSettings).lookupLocators();
        doReturn(connectToSpace0).when(spaceConnections).connect(eq(spaceName0), eq(admin));
        doReturn(connectToSpace1).when(spaceConnections).connect(eq(spaceName1), eq(admin));
        doReturn(adminFactory).when(adminFactoryFactory).build();
        doReturn(admin).when(adminFactory).create();
        doReturn(admin).when(adminFactory).createAdmin();
        doReturn(machines).when(admin).getMachines();
        doReturn(GSCs).when(admin).getGridServiceContainers();
        doReturn(actorSystem).when(contextHolder).getSystem();
        doReturn(contextExecutor).when(actorSystem).dispatcher();

        testInstance = new ConnectToXap(
                monitorSettings
                , configureAlerts
                , spaceConnections
                , adminFactoryFactory
                , contextHolder);
    }

    @Test
    public void testInvokeEstablishesLookups() throws Exception {

        testInstance.invoke();

        verify(adminFactory, times(1)).addLocators(testLocators);

    }

    @Test
    public void testInvokeEnablesSecurity() throws Exception{

        doReturn(true).when(monitorSettings).alertsEnabled();

        testInstance.invoke();

        verify(configureAlerts).invoke(same(admin));

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

        verify(spaceConnections, times(2)).connect(anyString(), same(admin));

    }

    @Test
    public void testInvokeConnectsToMachines() throws Exception {

        Integer numMachines = numbers.positiveInteger();
        doReturn(numMachines).when(monitorSettings).machineCount();

        testInstance.invoke();

        verify(contextExecutor).execute(isA(ConnectToMachines.class));

    }

    @Test
    public void testInvokeConnectsToGSCs() throws Exception {

        Integer numGSCs = numbers.positiveInteger();
        doReturn(numGSCs).when(monitorSettings).gscCount();

        testInstance.invoke();

        verify(contextExecutor).execute(isA(ConnectToGscs.class));

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

        InOrder inOrder = inOrder(contextExecutor, GSCs);
        inOrder.verify(contextExecutor, times(2)).execute(isA(ConnectToSpace.class));
        inOrder.verify(contextExecutor).execute(isA(ConnectToMachines.class));

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

        InOrder spacesBeforeMachines = inOrder(contextExecutor);
        spacesBeforeMachines.verify(contextExecutor, times(2)).execute(isA(ConnectToSpace.class));
        spacesBeforeMachines.verify(contextExecutor).execute(isA(ConnectToMachines.class));

    }

    @Test(expected = IllegalStateException.class)
    public void testEnsureCredentialsThrowsWhenNullUsername() throws Exception{

        doReturn(null).when(monitorSettings).username();

        testInstance.ensureCredentials();

    }

    @Test(expected = IllegalStateException.class)
    public void testEnsureCredentialsThrowsWhenNullPw() throws Exception{

        doReturn(strings.alphabetic()).when(monitorSettings).username();
        doReturn("").when(monitorSettings).password();

        testInstance.ensureCredentials();

    }

    @Test
    public void testEnableSecurity() throws Exception{

        String uname = strings.alphabetic();
        String pw = strings.alphabetic();

        doReturn(true).when(monitorSettings).xapSecurityEnabled();
        doReturn(uname).when(monitorSettings).username();
        doReturn(pw).when(monitorSettings).password();

        testInstance.enableSecurity(adminFactory);

        verify(adminFactory).credentials(eq(uname), eq(pw));

    }

    @Test
    public void testEstablishLookupsWithSingleLookupGroupSingleLookupLocator() throws Exception{

        String group = strings.alphabetic();
        String locator = strings.alphabetic();

        doReturn(group).when(monitorSettings).lookupGroups();
        doReturn(locator).when(monitorSettings).lookupLocators();

        testInstance.establishLookups(adminFactory);

        verify(adminFactory).addLocator(eq(locator));
        verify(adminFactory).addGroup(eq(group));

    }

    @Test
    public void testEstablishLookupsWithSingleLookupGroupMultipleLookupLocators() throws Exception{

        String group = strings.alphabetic();
        String locators = strings.alphabetic() + Constants.LIST_ITEM_SEPARATOR + strings.alphabetic();

        doReturn(group).when(monitorSettings).lookupGroups();
        doReturn(locators).when(monitorSettings).lookupLocators();

        testInstance.establishLookups(adminFactory);

        verify(adminFactory).addLocators(eq(locators));
        verify(adminFactory).addGroup(eq(group));

    }

    @Test
    public void testEstablishLookupsWithMultipleLookupGroupsSingleLookupLocator() throws Exception{

        String groups = strings.alphabetic() + Constants.LIST_ITEM_SEPARATOR + strings.alphabetic();
        String locator = strings.alphabetic();

        doReturn(groups).when(monitorSettings).lookupGroups();
        doReturn(locator).when(monitorSettings).lookupLocators();

        testInstance.establishLookups(adminFactory);

        verify(adminFactory).addLocator(eq(locator));
        verify(adminFactory).addGroups(eq(groups));

    }

    @Test
    public void testEstablishLookupsWithMultipleLookupGroupsMultipleLookupLocators() throws Exception{

        String groups = strings.alphabetic() + Constants.LIST_ITEM_SEPARATOR + strings.alphabetic();
        String locators = strings.alphabetic() + Constants.LIST_ITEM_SEPARATOR + strings.alphabetic();

        doReturn(groups).when(monitorSettings).lookupGroups();
        doReturn(locators).when(monitorSettings).lookupLocators();

        testInstance.establishLookups(adminFactory);

        verify(adminFactory).addLocators(eq(locators));
        verify(adminFactory).addGroups(eq(groups));

    }

    @Test
    public void testGetAdmin(){

        Admin actual = testInstance.getAdmin();

        assertSame(admin, actual);

    }

}