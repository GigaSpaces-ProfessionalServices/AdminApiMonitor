package com.gigaspaces.sbp.metrics.bootstrap.props;

import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 11/10/14
 * Time: 3:27 PM
 */
@Component
public class GsMonitorProperties extends MonitorDefaultsImpl {

    private static final String GS_MONITOR_PROPERTY_FILE_NAME = "properties/monitor.default.properties";

    GsMonitorProperties() {
        super(GS_MONITOR_PROPERTY_FILE_NAME);
    }

}