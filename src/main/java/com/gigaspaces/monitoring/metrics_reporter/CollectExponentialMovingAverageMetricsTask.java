package com.gigaspaces.monitoring.metrics_reporter;

import com.gigaspaces.monitoring.metrics_source.adminapi.AdminAPIMonitor;
import com.gigaspaces.monitoring.metrics_source.space_proxy.SpaceProxyCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


public class CollectExponentialMovingAverageMetricsTask {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private AdminAPIMonitor adminMonitor;

    private SpaceProxyCounter spaceProxyCounter;

    public void collectMetrics() {
        logger.trace("Collect exponential moving average metrics");
        logger.trace("Space proxy statistics = " + spaceProxyCounter);
        logger.trace("AdminAPI statistics = " + adminMonitor.startCollection());
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
