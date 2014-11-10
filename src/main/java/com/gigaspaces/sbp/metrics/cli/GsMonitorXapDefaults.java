package com.gigaspaces.sbp.metrics.cli;

import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 11/10/14
 * Time: 3:30 PM
 */
@Component
public class GsMonitorXapDefaults extends XapPropertyFileDefaults {

    private static final String GS_MONITOR_PROPERTIES_FILENAME = "properties/gs.monitor.properties";

    GsMonitorXapDefaults() { super(GS_MONITOR_PROPERTIES_FILENAME); }

}
