package com.gigaspaces.sbp.metrics.bootstrap;

import com.gigaspaces.sbp.metrics.cli.Settings;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 11/10/14
 * Time: 7:32 PM
 */
@Component
public class GsMonitorSettings {

    private static final String NOT_INITIALIZED_ERROR
            = "GsMonitorSettings have not been initialized yet. " +
            "Initialization occurs when #initialize(Map<Settings,String>) is called after CLI " +
            "input is interpreted.";


    private Map<Settings, String> settingsMap = new ConcurrentHashMap<>();
    private boolean initialized = false;

    public void initialize(Map<Settings, String> map) {
        settingsMap.clear();
        settingsMap.putAll(map);
        initialized = true;
    }

    public String getOutputFilePath() {
        ensureInitialization();
        return settingsMap.get(Settings.OutputFile);
    }

    private void ensureInitialization() {
        if (!initialized) throw new IllegalStateException(NOT_INITIALIZED_ERROR);
    }
}
