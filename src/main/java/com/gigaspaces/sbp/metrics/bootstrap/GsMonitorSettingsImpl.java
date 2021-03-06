package com.gigaspaces.sbp.metrics.bootstrap;

import com.gigaspaces.sbp.metrics.Constants;
import com.gigaspaces.sbp.metrics.bootstrap.cli.OutputFormat;
import com.gigaspaces.sbp.metrics.bootstrap.cli.SettingType;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 11/10/14
 * Time: 7:32 PM
 * A central application component for all application settings, irrespective of whether
 * they happen to come from the CLI or a properties file.
 *
 * This class is not very smart - it doesn't implement defaulting, for example - but it
 * does check that certain properties have been initialized correctly (by the caller of
 * initialize). This may be a design problem - invariants of another class being enforced
 * here...
 *
 * The details are in the test, of course...
 */
@Component
public class GsMonitorSettingsImpl implements GsMonitorSettings {

    private static final String NOT_INITIALIZED_ERROR
            = String.format("%s have not been initialized yet. Initialization occurs when #initialize(Map<Settings,String>) is called after CLI input is interpreted.", GsMonitorSettingsImpl.class.getSimpleName());
    private static final String SHOULD_BE_INITIALIZED_ERROR
            = "%s have been initialized, but incorrectly. Missing required setting '%s'.";

    private Map<SettingType, String> settingsMap = new ConcurrentHashMap<>();
    private boolean initialized = false;
    private OutputFormat outputFormat;

    public void initialize(Map<SettingType, String> map) {
        settingsMap.clear();
        settingsMap.putAll(map);
        initialized = true;
    }

    @Override
    public String outputFilePath() {
        return getRequiredSettingOrThrow(SettingType.OutputFile);
    }

    private void ensureInitialization() {
        if (!initialized) throw new IllegalStateException(NOT_INITIALIZED_ERROR);
    }

    @Override
    public Float emaAlpha() {
        return Float.valueOf(getRequiredSettingOrThrow(SettingType.MovingAverageAlpha));
    }

    @Override
    public OutputFormat outputFormat(){

        ensureInitialization();

        if (outputFormat != null ) return outputFormat;

        String csv = getOptionalSettingOrNull(SettingType.Csv);
        String logFormat = getOptionalSettingOrNull(SettingType.LogFormat);

        if( csv == null && logFormat == null )
            throw new IllegalStateException(String.format(SHOULD_BE_INITIALIZED_ERROR, GsMonitorSettingsImpl.class.getSimpleName(), "OutputFormat"));

        if( csv != null ) outputFormat = OutputFormat.Csv;
        if( logFormat != null ) outputFormat = OutputFormat.LogFormat;

        return outputFormat;
    }

    /**
     * This method is used to retrieve settings that are guaranteed by the implementation (users of this class!)
     * to be initialized by calling initialize
     * @param setting the one we expect to be able to evaluate
     * @return the setting's string value
     * @throws java.lang.IllegalStateException if there's a programming error in the code that calls
     * {@link #initialize(java.util.Map)}
     */
    private String getRequiredSettingOrThrow(SettingType setting) {
        ensureInitialization();
        String value = settingsMap.get(setting);
        if( value == null )
            throw new IllegalStateException(String.format(SHOULD_BE_INITIALIZED_ERROR, GsMonitorSettingsImpl.class.getSimpleName(), setting.name()));
        return value;
    }

    private String getOptionalSettingOrNull(SettingType setting){
        ensureInitialization();
        return settingsMap.get(setting);
    }

    @Override
    public String username() {
        return getOptionalSettingOrNull(SettingType.Username);
    }

    @Override
    public String password() {
        return getOptionalSettingOrNull(SettingType.Password);
    }

    @Override
    public String lookupLocators() {
        return getRequiredSettingOrThrow(SettingType.LookupLocators);
    }

    @Override
    public String lookupGroups() {
        return getOptionalSettingOrNull(SettingType.LookupGroups);
    }

    @Override
    public String[] spaceNames() {
        String names = getRequiredSettingOrThrow(SettingType.SpaceNames);
        names = names.trim();
        return names.split(Constants.LIST_ITEM_SEPARATOR);
    }

    @Override
    public Boolean alertsEnabled() {
        ensureInitialization();
        return Boolean.valueOf(getRequiredSettingOrThrow(SettingType.AlertsEnabled));
    }

    @Override
    public Boolean sendAlertsByEmail() {
        ensureInitialization();
        String val = getOptionalSettingOrNull(SettingType.SendAlertsByEmail);
        return val != null ? Boolean.valueOf(val) : false;
    }

    @Override
    public Integer collectMetricsIntervalInMs() {
        ensureInitialization();
        return Integer.valueOf(getRequiredSettingOrThrow(SettingType.CollectMetricsIntervalInMs));
    }

    @Override
    public Integer collectMetricsInitialDelayInMs(){
        ensureInitialization();
        return Integer.valueOf(getRequiredSettingOrThrow(SettingType.CollectMetricsInitialDelayInMs));
    }

    @Override
    public Boolean xapSecurityEnabled() {
        ensureInitialization();
        return Boolean.valueOf(getRequiredSettingOrThrow(SettingType.Secured));
    }

    @Override
    public Integer machineCount() {
        ensureInitialization();
        return Integer.valueOf(getRequiredSettingOrThrow(SettingType.MachineCount));
    }

    @Override
    public Integer gscCount() {
        ensureInitialization();
        return Integer.valueOf(getRequiredSettingOrThrow(SettingType.GscCount));
    }

    /**
     * @return number of milliseconds to allow to pass between calculation of "derived metrics"
     */
    @Override
    public Long derivedMetricsPeriodInMs() {
        ensureInitialization();
        return Long.valueOf(getRequiredSettingOrThrow(SettingType.DerivedMetricsPeriodInMs));
    }

    @Override
    public String alertRecipientEmailAddress(){
        ensureInitialization();
        return getOptionalSettingOrNull(SettingType.SendAlertsByEmail);
    }

}