package com.gigaspaces.sbp.metrics.bootstrap.xap;

import com.gigaspaces.sbp.metrics.ActorSystemEden;
import com.gigaspaces.sbp.metrics.bootstrap.GsMonitorSettings;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
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
    private static final String SPACE_CONNECTION_REQUIRED = "Need to be able to connect to spaces.";
    private static final String FACTORY_REQUIRED = "Need a mechanism for creating AdminFactories.";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private final GsMonitorSettings settings;
    @Resource
    private final ConfigureAlerts configureAlerts;
    @Resource
    private final SpaceConnections spaceConnections;
    @Resource
    private final AdminFactoryFactory adminFactoryFactory;
    @Resource
    private final ActorSystemEden contextHolder;

    private Admin admin;

    @Autowired
    public ConnectToXap(GsMonitorSettings settings
            , ConfigureAlerts configureAlerts
            , SpaceConnections spaceConnections
            , AdminFactoryFactory adminFactoryFactory
            , ActorSystemEden contextHolder) {
        assert spaceConnections != null : SPACE_CONNECTION_REQUIRED;
        assert settings != null : String.format(SETTINGS_REQUIRED_ERROR, GsMonitorSettings.class.getSimpleName());
        assert configureAlerts != null : String.format(NON_NULL_INSTANCE_REQUIRED, ConfigureAlerts.class.getSimpleName());
        assert adminFactoryFactory != null : FACTORY_REQUIRED;
        this.adminFactoryFactory = adminFactoryFactory;
        this.spaceConnections = spaceConnections;
        this.settings = settings;
        this.configureAlerts = configureAlerts;
        this.contextHolder = contextHolder;
    }

    /**
     * Create an Admin instance and prepare framework for individual {@link com.gigaspaces.sbp.metrics.bootstrap.xap.ConnectToSpace}
     * instances.
     */
    public Admin invoke() {

        AdminFactory factory = adminFactoryFactory.build();
        establishLookups(factory);
        enableSecurity(factory);
        factory.discoverUnmanagedSpaces();

        admin = factory.createAdmin();

        if (settings.alertsEnabled())
            configureAlerts.invoke(admin);

        for (String spaceName : settings.spaceNames()) {
            dispatch(spaceConnections.connect(spaceName, admin));
        }

        connectToMachines();
        connectToGSCs();

        return admin;

    }

    private void dispatch(Runnable runnable){
        contextHolder.getSystem().dispatcher().execute(runnable);
    }

    private void connectToGSCs() {
        dispatch(new ConnectToGscs(admin, settings));
    }

    private void connectToMachines() {
        dispatch(new ConnectToMachines(admin, settings));
    }

    void establishLookups(AdminFactory factory) {
        String lookupLocators = settings.lookupLocators();
        if( lookupLocators.indexOf(",") > 0 )
            factory.addLocators(lookupLocators);
        else
            factory.addLocator(lookupLocators);
        logger.debug(String.format("Connecting to XAP using locators = '%s'.", lookupLocators));
        String lookupGroups = settings.lookupGroups();
        if (lookupGroups != null) lookupGroups = lookupGroups.trim();
        if (lookupGroups != null && lookupGroups.length() > 0) {
            logger.debug(String.format("Connecting to XAP using lookup groups = '%s'.", lookupGroups));
            if( lookupGroups.indexOf(",") > 0 )
                factory.addGroups(lookupGroups);
            else
                factory.addGroup(lookupGroups);
        } else logger.debug("Connecting to XAP using default lookup groups.");

    }

    void enableSecurity(AdminFactory factory) {
        if (settings.xapSecurityEnabled()) {
            logger.debug("Connecting to XAP with security enabled.");
            ensureCredentials();
            factory.credentials(settings.username(), settings.password());
        } else logger.debug("Connecting to XAP without security enabled.");
    }

    void ensureCredentials() {
        String username = settings.username();
        String password = settings.password();
        if (username == null || username.trim().length() == 0)
            throw new IllegalStateException(MISSING_USERNAME_ERROR);
        if (password == null || password.trim().length() == 0)
            throw new IllegalStateException(MISSING_PASSWORD_ERROR);
    }

    public Admin getAdmin() {
        if( admin == null) admin = adminFactoryFactory.build().createAdmin();
        return admin;
    }
}