package com.gigaspaces.monitoring.metrics_reporter;

import com.gigaspaces.monitoring.metrics_source.adminapi.AdminAPIMonitor;
import com.gigaspaces.monitoring.metrics_source.space_proxy.SpaceProxyCounter;
import org.springframework.beans.factory.annotation.Required;

import java.util.logging.Logger;

public class CollectExponentialMovingAverageMetricsTask {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private AdminAPIMonitor adminMonitor;

    private SpaceProxyCounter spaceProxyCounter;

    public void collectMetrics() {
//        logger.info("Collect exponential moving average metrics");
//        logger.info("Space proxy statistics = " + spaceProxyCounter);
//        logger.info("AdminAPI statistics = " + adminMonitor.startCollection());
    }

    @Required
    public void setAdminMonitor(AdminAPIMonitor adminMonitor) {
        this.adminMonitor = adminMonitor;
    }

    @Required
    public void setSpaceProxyCounter(SpaceProxyCounter spaceProxyCounter) {
        this.spaceProxyCounter = spaceProxyCounter;
    }
}
