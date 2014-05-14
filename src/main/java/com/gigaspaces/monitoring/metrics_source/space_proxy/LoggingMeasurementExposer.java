package com.gigaspaces.monitoring.metrics_source.space_proxy;

import org.springframework.beans.factory.annotation.Required;

import java.util.logging.Logger;

public class LoggingMeasurementExposer implements MeasurementExposer {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private SpaceProxyCounter spaceProxyCounter;

    @Override
    public void expose(SimplePerformanceItem performanceItem) throws Exception {
        spaceProxyCounter.count(performanceItem);
        logger.info("Performance for " + performanceItem.getSourceClassName() + "."
                + performanceItem.getSourceMethodName() + ": " + performanceItem.getElapsedTime() + "ms. Statistics : " + spaceProxyCounter);
    }

    @Override
    public boolean isGoodConfiguration() {
        return false;
    }

    @Required
    public void setSpaceProxyCounter(SpaceProxyCounter spaceProxyCounter) {
        this.spaceProxyCounter = spaceProxyCounter;
    }
}
