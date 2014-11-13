package com.gigaspaces.sbp.metrics.bootstrap;

import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 11/10/14
 * Time: 3:30 PM
 */
@Component
public class XapDefaultsFromPropertyFile extends XapDefaultsImpl {

    private static final String GS_MONITOR_PROPERTIES_FILENAME = "properties/xap.default.properties";

    XapDefaultsFromPropertyFile() { super(GS_MONITOR_PROPERTIES_FILENAME); }

}
