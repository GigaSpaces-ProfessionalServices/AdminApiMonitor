package com.gigaspaces.monitoring.metrics_source.space_proxy;

import java.util.logging.Logger;

/**
 * Created by ubuntu on 5/14/14.
 */
public class LoggingMeasurementExposer implements MeasurementExposerInterface {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void expose(SimplePerformanceItem performanceItem) throws Exception {
        String message = "Performance for " + performanceItem.getSourceClassName() + "."
                + performanceItem.getSourceMethodName() + ": " + performanceItem.getElapsedTime();
        logger.info(message);
    }

    @Override
    public boolean isGoodConfiguration() {
        return false;
    }
}
