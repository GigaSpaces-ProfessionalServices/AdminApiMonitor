package com.gigaspaces.sbp.metrics.bootstrap.xap;

import com.gigaspaces.sbp.metrics.Constants;
import com.gigaspaces.sbp.metrics.alert.EmailAlertTriggeredEventListener;
import com.gigaspaces.sbp.metrics.bootstrap.GsMonitorSettings;
import org.openspaces.admin.Admin;
import org.openspaces.admin.alert.AlertManager;
import org.openspaces.admin.alert.config.parser.XmlAlertConfigurationParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 11/15/14
 * Time: 7:44 PM
 *
 * Alert configurer.
 */
@Component
public class ConfigureAlerts {

    private static final String SETTINGS_REQUIRED_ERROR = "Non-null application settings '%s' are required.";
    private static final String ADMIN_REQUIRED_ERROR = "Required non-null admin.";
    private static final String ALERTS_CONFIGURATION_RESOURCE = "alerts-config.xml";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private final GsMonitorSettings settings;

    @Autowired
    public ConfigureAlerts(GsMonitorSettings settings) {
        assert settings != null : String.format(SETTINGS_REQUIRED_ERROR, GsMonitorSettings.class.getSimpleName());
        this.settings = settings;
    }

    /**
     * Configures alerts for the given admin.
     * @param admin on which to configure an alert manager
     */
    public void invoke(Admin admin) {
        assert admin != null : ADMIN_REQUIRED_ERROR;
        if (!settings.alertsEnabled()){
            logger.debug("Alerts are disabled.");
            return;
        }
        AlertManager alertManager = admin.getAlertManager();
        alertManager.configure(new XmlAlertConfigurationParser(ALERTS_CONFIGURATION_RESOURCE).parse());
        logger.debug(String.format("Configuring alerts using classpath resource '%s'.", ALERTS_CONFIGURATION_RESOURCE));
        if (settings.sendAlertsByEmail()) {
            String email = System.getProperty(Constants.ALERTS_EMAIL_ADDRESS_SYSTEM_PROPERTY_NAME);
            logger.debug(String.format("Configuring alerts to be sent my email to '%s'.", email));
            alertManager.getAlertTriggered().add(new EmailAlertTriggeredEventListener());
        }
    }

}