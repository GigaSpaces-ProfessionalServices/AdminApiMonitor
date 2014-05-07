package com.gigaspaces.monitoring.metrics_reporter;

import com.gigaspaces.monitoring.metrics_source.space_proxy.SpaceProxyCounter;
import org.springframework.beans.factory.annotation.Required;

import java.util.logging.Logger;

public class CollectPeriodicAverageMetricsTask {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private SpaceProxyCounter spaceProxyCounter;

    private void collectMetrics(){
        logger.info("Collect timed moving average metrics");
        SpaceProxyCounter.PeriodicMetricsCounter periodicMetricsCounter = spaceProxyCounter.refreshTimedMetrics();
        logger.info("PERIODIC_METRICS = " + periodicMetricsCounter);
    }

    @Required
    public void setSpaceProxyCounter(SpaceProxyCounter spaceProxyCounter) {
        this.spaceProxyCounter = spaceProxyCounter;
    }
}
