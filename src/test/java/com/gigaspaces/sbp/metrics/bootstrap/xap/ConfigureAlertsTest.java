package com.gigaspaces.sbp.metrics.bootstrap.xap;

import com.gigaspaces.sbp.metrics.bootstrap.GsMonitorSettings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openspaces.admin.Admin;
import org.openspaces.admin.alert.AlertManager;
import org.openspaces.admin.alert.config.AlertConfiguration;
import org.openspaces.admin.alert.events.AlertTriggeredEventListener;
import org.openspaces.admin.alert.events.AlertTriggeredEventManager;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConfigureAlertsTest {

    private ConfigureAlerts testInstance;

    @Mock
    private GsMonitorSettings monitorSettings;
    @Mock
    private Admin admin;
    @Mock
    private AlertManager alertManager;
    @Mock
    private AlertTriggeredEventManager alertTriggeredEventManager;

    @Before
    public void setUp() throws Exception {

        testInstance = new ConfigureAlerts(monitorSettings);

        doReturn(alertManager).when(admin).getAlertManager();
        doReturn(alertTriggeredEventManager).when(alertManager).getAlertTriggered();

    }

    @Test
    public void testInvokeConfiguresAlertsWhenAlertsAreEnabled() throws Exception {

        doReturn(true).when(monitorSettings).alertsEnabled();
        doReturn(true).when(monitorSettings).sendAlertsByEmail();

        testInstance.invoke(admin);

        verify(alertManager).configure(Matchers.<AlertConfiguration[]>anyVararg());
        verify(alertTriggeredEventManager).add(Matchers.<AlertTriggeredEventListener>anyObject());

    }

    @Test
    public void testInvokeDoesNotConfigureAlertsWhenAlertsAreEnabled() throws Exception {

        doReturn(false).when(monitorSettings).alertsEnabled();

        testInstance.invoke(admin);

        verify(alertManager, never()).configure(Matchers.<AlertConfiguration[]>anyVararg());

    }

}