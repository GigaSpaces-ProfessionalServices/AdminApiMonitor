package com.gigaspaces.sbp.metrics.bootstrap.props;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 11/7/14
 * Time: 8:51 AM
 * Provides a source of default XAP settings.
 */
public interface XapDefaults {

    String lookupLocators();

    String spaceNames();

    Boolean isSecured();

    Integer hostMachineCount();

    Integer gscCount();

}
