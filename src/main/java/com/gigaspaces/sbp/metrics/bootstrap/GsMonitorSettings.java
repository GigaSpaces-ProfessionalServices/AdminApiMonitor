package com.gigaspaces.sbp.metrics.bootstrap;

import com.gigaspaces.sbp.metrics.bootstrap.cli.OutputFormat;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 11/15/14
 * Time: 6:58 PM
 *
 * Provides application-wide settings.
 */
public interface GsMonitorSettings {

    /**
     * @return fully-qualified path for the location of the output file (log).
     */
    String outputFilePath();

    /**
     * The decay rate of the moving average calculation. A value of 1.0 would mean that only the newest
     * arriving data point would be incorporated into the average. Values near zero weight the
     * calculation toward bygone values.
     * @return a number in the range [1.0,0)
     */
    Float emaAlpha();

    /**
     * @return The output format that the monitor will log in.
     */
    OutputFormat outputFormat();

    /**
     * @return XAP security username, if any (nullable)
     */
    String username();

    /**
     * @return XAP security password, if any (nullable)
     */
    String password();

    /**
     * {@see http://wiki.gigaspaces.com/wiki/display/XAP9/How+to+Configure+Unicast+Discovery}
     * @return XAP lookup locators. comma-separated
     */
    String lookupLocators();

    /**
     * {@see http://wiki.gigaspaces.com/wiki/display/XAP91/Lookup+Service+Configuration}
     * This optional configuration can be null, in which case the implemented proxy will
     * choose an appropriate (the correct) default value.
     * @return XAP lookup groups
     */
    String lookupGroups();

    /**
     * @return comma-delimited list of XAP Spaces to monitor
     */
    String[] spaceNames();

    /**
     * @return whether the XAP Alerts API is enabled
     */
    Boolean alertsEnabled();

    /**
     * @return if XAP Alerts are to be sent by email relay
     */
    Boolean sendAlertsByEmail();

    /**
     * @return number of milliseconds between metric collection attempts
     */
    Integer collectMetricsIntervalInMs();

    /**
     * @return number of milliseconds after startup to wait before the first metric collection attempt
     */
    Integer collectMetricsInitialDelayInMs();

    Boolean xapSecurityEnabled();

    Integer machineCount();

    Integer gscCount();

    /**
     * @return number of milliseconds to allow to pass between calculation of "derived metrics"
     */
    Long derivedMetricsPeriodInMs();

    String alertRecipientEmailAddress();
}
