package com.gigaspaces.sbp.metrics.bootstrap.xap;

import com.gigaspaces.sbp.metrics.bootstrap.GsMonitorSettings;
import org.openspaces.admin.Admin;
import org.openspaces.admin.machine.Machines;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 12/11/14
 * Time: 2:14 PM
 */
class ConnectToMachines implements Runnable {

    private static final String ADMIN_REQUIRED = "Admin required.";
    private static final String SETTINGS_REQUIRED = "Settings are required.";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Admin admin;
    private final GsMonitorSettings settings;

    ConnectToMachines(Admin admin, GsMonitorSettings settings) {
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
        int machineCount = settings.machineCount();
        logger.debug(String.format("Waiting indefinitely to connect to %d machines.", machineCount));
        Machines machines = admin.getMachines();
        long start = System.currentTimeMillis();
        machines.waitFor(machineCount);
        long stop = System.currentTimeMillis();
        logger.debug(String.format("Successfully contacted %d machines in %d milliseconds.", machineCount, (stop - start)));
    }
}
