package com.gigaspaces.sbp.metrics.alert;

import org.openspaces.admin.alert.Alert;
import org.openspaces.admin.alert.events.AlertTriggeredEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class EmailAlertTriggeredEventListener implements AlertTriggeredEventListener {

    private final Logger logger = LoggerFactory.getLogger("email_reports");

    static final String HOSTNAME_PROP_KEY = "host-name";
    static final String HOST_ADDRESS_PROP_KEY = "host-address";
    static final String HIGH_THRESHOLD_PCT_KEY = "high-threshold-perc";

    @Override
    public void alertTriggered(Alert alert) {
        String alertLevel = alert.getSeverity().getName();
        String name = alert.getName();
        String componentDescription = alert.getComponentDescription();
        String longMessage = alert.getDescription();
        String hostName = alert.getProperties().get(HOSTNAME_PROP_KEY);
        String ipInfo = alert.getProperties().get(HOST_ADDRESS_PROP_KEY);
        String status = alert.getStatus().getName();
        Date date = new Date(alert.getTimestamp());
        String threshold = alert.getConfig().get(HIGH_THRESHOLD_PCT_KEY);

        String message = "{} \n" +
                "Host info: {} \n" +
                "IP info: {} \n" +
                "Alert name: {} \n" +
                "Alert status: {} \n" +
                "Time: {} \n" +
                "Component Name: {} \n" +
                "Metric Name: {} \n" +
                "Metric Graph: {} \n" +
                "Previous Alert State: {} \n" +
                "Has alert state changed: {} \n" +
                "Threshold: {} \n" +
                "Current Value: {} \n" +
                "Short Message: {} \n" +
                "Long Message: {} \n";
        logger.error(message, alertLevel, hostName, ipInfo, name, status, date, componentDescription, name, null, alert.getAlertUid(), null, threshold, null, null, longMessage);
    }
}
