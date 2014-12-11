package com.gigaspaces.sbp.metrics.bootstrap.xap;

import com.gigaspaces.sbp.metrics.bootstrap.GsMonitorSettings;
import org.openspaces.admin.Admin;
import org.openspaces.admin.gsc.GridServiceContainers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 12/11/14
 * Time: 2:46 PM
 */
class ConnectToGscs implements Runnable {

    private static final String ADMIN_REQUIRED = "Admin required.";
    private static final String SETTINGS_REQUIRED = "Settings are required.";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Admin admin;
    private final GsMonitorSettings settings;

    public ConnectToGscs(Admin admin, GsMonitorSettings settings) {
        assert admin != null : ADMIN_REQUIRED;
        assert settings != null : SETTINGS_REQUIRED;
        this.admin = admin;
        this.settings = settings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        GridServiceContainers GSCs = admin.getGridServiceContainers();
        int gscCount = settings.gscCount();
        long start = System.currentTimeMillis();
        logger.info(String.format("Waiting indefinitely to connect to %d GSCs.", gscCount));
        GSCs.waitFor(gscCount);
        long stop = System.currentTimeMillis();
        logger.info(String.format("Successfully contacted %d GSCs in %d milliseconds.", gscCount, (stop - start)));
    }
}
