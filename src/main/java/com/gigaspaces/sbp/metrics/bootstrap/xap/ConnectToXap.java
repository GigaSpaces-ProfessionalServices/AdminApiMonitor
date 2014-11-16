package com.gigaspaces.sbp.metrics.bootstrap.xap;

import com.gigaspaces.sbp.metrics.bootstrap.GsMonitorSettings;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.gsc.GridServiceContainers;
import org.openspaces.admin.machine.Machines;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 11/12/14
 * Time: 6:36 PM
 * <p/>
 *
 * Global connection service. Creates individual processes to connect to different Spaces.
 */
@Component
public class ConnectToXap {

    private static final String SETTINGS_REQUIRED_ERROR = "Application settings '%s' are required.";
    private static final String MISSING_PASSWORD_ERROR = "Non-empty password required.";
    private static final String MISSING_USERNAME_ERROR = "Non-empty username required.";
    private static final String NON_NULL_INSTANCE_REQUIRED = "Require non-null instance of '%s'.";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private final GsMonitorSettings settings;
    @Resource
    private final ConfigureAlerts configureAlerts;

    private Admin admin;

    @Autowired
    public ConnectToXap(GsMonitorSettings settings, ConfigureAlerts configureAlerts) {
        assert settings != null : String.format(SETTINGS_REQUIRED_ERROR, GsMonitorSettings.class.getSimpleName());
        assert configureAlerts != null : String.format(NON_NULL_INSTANCE_REQUIRED, ConfigureAlerts.class.getSimpleName());
        this.settings = settings;
        this.configureAlerts = configureAlerts;
    }

    /**
     * Create an Admin instance and prepare framework for individual {@link com.gigaspaces.sbp.metrics.bootstrap.xap.ConnectToSpace}
     * instances.
     */
    public Admin invoke() {

        AdminFactory factory = new AdminFactory();
        establishLookups(factory);
        enableSecurity(factory);
        factory.discoverUnmanagedSpaces();

        admin = factory.createAdmin();

        if (settings.alertsEnabled())
            configureAlerts.invoke(admin);

        connectToMachines(); // TODO Thread it
        connectToGSCs(); // TODO Thread it

        for (String spaceName : settings.spaceNames()) {
            new ConnectToSpace(spaceName).invoke(admin); // TODO Thread it through a factory
        }

        return admin;

    }

    private void connectToGSCs() {
        GridServiceContainers GSCs = admin.getGridServiceContainers();
        int gscCount = settings.gscCount();
        long start = System.currentTimeMillis();
        logger.info("Waiting indefinitely to connect to %d GSCs.", gscCount);
        GSCs.waitFor(gscCount);
        long stop = System.currentTimeMillis();
        logger.info("Successfully contacted %d GSCs in %d milliseconds.", gscCount, (stop - start));
    }

    private void connectToMachines() {
        Machines machines = admin.getMachines();
        long start = System.currentTimeMillis();
        int machineCount = settings.machineCount();
        machines.waitFor(machineCount);
        long stop = System.currentTimeMillis();
        logger.info("Successfully contacted %d machines in %d milliseconds.", machineCount, (stop - start));
    }

    private void establishLookups(AdminFactory factory) {
        String lookupLocators = settings.lookupLocators();
        factory.addLocators(lookupLocators);
        logger.info("Connecting to XAP using locators = '%s'.", lookupLocators);
        String lookupGroups = settings.lookupGroups();
        if (lookupGroups != null) lookupGroups = lookupGroups.trim();
        if (lookupGroups != null && lookupGroups.length() > 0) {
            logger.info("Connecting to XAP using lookup groups = '%s'.", lookupGroups);
            factory.addGroups(lookupGroups);
        } else logger.info("Connecting to XAP using default lookup groups.");

    }

    private void enableSecurity(AdminFactory factory) {
        if (settings.xapSecurityEnabled()) {
            logger.info("Connecting to XAP with security enabled.");
            ensureCredentials();
            factory.credentials(settings.username(), settings.password());
        } else logger.info("Connecting to XAP without security enabled.");
    }

    void ensureCredentials() {
        String username = settings.username();
        String password = settings.password();
        if (username == null || username.trim().length() == 0)
            throw new IllegalStateException(MISSING_USERNAME_ERROR);
        if (password == null || password.trim().length() == 0)
            throw new IllegalStateException(MISSING_PASSWORD_ERROR);
    }

}