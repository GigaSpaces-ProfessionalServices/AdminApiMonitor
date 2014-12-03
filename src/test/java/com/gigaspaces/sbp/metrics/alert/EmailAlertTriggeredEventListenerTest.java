package com.gigaspaces.sbp.metrics.alert;

import com.jasonnerothin.testing.Numbers;
import com.jasonnerothin.testing.Strings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openspaces.admin.alert.Alert;
import org.openspaces.admin.alert.AlertSeverity;
import org.openspaces.admin.alert.AlertStatus;

import java.util.Properties;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class EmailAlertTriggeredEventListenerTest {

    @Mock
    private Alert alert;
    @Mock
    private Properties alertProperties;
    @Mock
    private AlertStatus alertStatus;
    @Mock
    private AlertSeverity alertSeverity;

    private Numbers numbers;
    private Strings strings;

    private EmailAlertTriggeredEventListener testInstance;

    @Before
    public void setUp() throws Exception {

        strings = new Strings();
        numbers = new Numbers();
        testInstance = new EmailAlertTriggeredEventListener();

    }

    @Test
    public void testAlertTriggered() throws Exception {

        doReturn(alertProperties).when(alert).getProperties();
        doReturn(strings.alphabetic()).when(alertProperties).getProperty(eq(EmailAlertTriggeredEventListener.HOSTNAME_PROP_KEY));
        doReturn(strings.alphabetic()).when(alertProperties).getProperty(eq(EmailAlertTriggeredEventListener.HOST_ADDRESS_PROP_KEY));
        doReturn(String.valueOf(numbers.positiveDouble() / 100d)).when(alertProperties).getProperty(eq(EmailAlertTriggeredEventListener.HIGH_THRESHOLD_PCT_KEY));
        doReturn(numbers.positiveLong()).when(alert).getTimestamp();
        doReturn(strings.alphabetic()).when(alert).getComponentDescription();
        doReturn(alertStatus).when(alert).getStatus();
        doReturn(alertSeverity).when(alert).getSeverity();

        testInstance.alertTriggered(alert);

    }

}