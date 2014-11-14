package com.gigaspaces.sbp.metrics.bootstrap.props;

import com.gigaspaces.sbp.metrics.bootstrap.cli.OutputFormat;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 11/10/14
 * Time: 2:52 PM
 */
class MonitorDefaultsImpl extends PropertyFileValues implements MonitorDefaults{

//    alerts.config=alerts-config.xml

    private static final String EMAIL_ENABLED = "alerts.email.reporting";
    private static final String EMAIL_INTERVAL_IN_MILLISECONDS = "email.periodic.sample.interval";
    private static final String EMAIL_DELAY_IN_MILLISECONDS = "email.periodic.sample.delay";

    private static final String METRIC_INTERVAL_IN_MILLISECONDS = "stat.periodic.sample.interval";
    private static final String METRIC_DELAY_IN_MILLISECONDS = "stat.periodic.sample.delay";
    private static final String METRIC_ALPHA = "stat.sample.alpha";
    private static final String OUTPUT_FILENAME = "spaceMonitor.outputFile";
    private static final String OUTPUT_FORMAT = "output.format";

    MonitorDefaultsImpl(String propertyFileName) {
        super(propertyFileName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isEmailAlertingEnabled() {
        return Boolean.TRUE.toString().equals(getPropOrThrow(EMAIL_ENABLED));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long emailAlertIntervalInMs() {
        return Long.valueOf(getPropOrThrow(EMAIL_INTERVAL_IN_MILLISECONDS));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long emailAlertDelayInMs() {
        return Long.valueOf(getPropOrThrow(EMAIL_DELAY_IN_MILLISECONDS));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long metricIntervalInMs() {
        return Long.valueOf(getPropOrThrow(METRIC_INTERVAL_IN_MILLISECONDS));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long metricDelayInMs() {
        return Long.valueOf(getPropOrThrow(METRIC_DELAY_IN_MILLISECONDS));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Float movingAverageAlpha() {
        return Float.valueOf(getPropOrThrow(METRIC_ALPHA));
    }

    @Override
    public String outputFilename(){
        return getPropOrThrow(OUTPUT_FILENAME);
    }

    @Override
    public OutputFormat outputFormat() {
        return OutputFormat.valueOf(getPropOrThrow(OUTPUT_FORMAT));
    }

}