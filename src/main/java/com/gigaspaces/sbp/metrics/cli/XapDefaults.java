package com.gigaspaces.sbp.metrics.cli;

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

    /**
     * Logs a message that a default output file is being used (since -o is available
     * as a setting on the CLI).
     *
     * @return the default output file
     */
    String outputFile();

    Boolean isSecured();

}
