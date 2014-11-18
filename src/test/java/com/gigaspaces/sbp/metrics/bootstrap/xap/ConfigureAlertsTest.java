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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ConfigureAlertsTest {

    private ConfigureAlerts testInstance;

    @Mock
    private GsMonitorSettings monitorSettings;
    @Mock
    private Admin admin;
    @Mock
    private AlertManager alertManager;

    @Before
    public void setUp() throws Exception {

        testInstance = new ConfigureAlerts(monitorSettings);

        doReturn(alertManager).when(admin).getAlertManager();

    }

    @Test
    public void testInvokeConfiguresAlertsWhenAlertsAreEnabled() throws Exception {

        doReturn(true).when(monitorSettings).alertsEnabled();

        testInstance.invoke(admin);

        verify(alertManager).configure(Matchers.<AlertConfiguration[]>anyVararg());

    }

    @Test
    public void testInvokeDoesNotConfigureAlertsWhenAlertsAreEnabled() throws Exception {

        doReturn(false).when(monitorSettings).alertsEnabled();

        testInstance.invoke(admin);

        verify(alertManager, never()).configure(Matchers.<AlertConfiguration[]>anyVararg());

    }

}