package com.gigaspaces.sbp.metrics.bootstrap.props;

import com.gigaspaces.sbp.metrics.bootstrap.cli.OutputFormat;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 11/10/14
 * Time: 2:48 PM
 * Provides a source of application (Gs Monitor) defaults.
 */
public interface MonitorDefaults {

    Boolean isEmailAlertingEnabled();

    Long emailAlertIntervalInMs();

    Long emailAlertDelayInMs();

    /**
     * Amount of time between metric samples are taken.
     * @return the value
     */
    Long metricIntervalInMs();

    /**
     * Delay is the initial delay, in milliseconds, before the first sample is taken.
     * @return the value
     */
    Long metricDelayInMs();

    /**
     * alpha is the decay rate of the metric data. It must be between
     * 0 and 1. 1 means only pay attention to the last data point for
     * a given metric. Not all metrics are affected.
     * @return the value
     */
    Float movingAverageAlpha();

    String outputFilename();

    OutputFormat outputFormat();

}
