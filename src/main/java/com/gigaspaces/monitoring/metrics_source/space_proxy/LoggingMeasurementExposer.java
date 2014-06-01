package com.gigaspaces.monitoring.metrics_source.space_proxy;

import org.springframework.beans.factory.annotation.Required;

public class LoggingMeasurementExposer implements MeasurementExposer {

    private SpaceProxyCounter spaceProxyCounter;

    @Override
    public void expose(SimplePerformanceItem performanceItem) throws Exception {
        spaceProxyCounter.count(performanceItem);
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
