package com.gigaspaces.sbp.metrics.bootstrap;

import com.gigaspaces.sbp.metrics.Constants;
import com.gigaspaces.sbp.metrics.bootstrap.cli.OutputFormat;
import com.gigaspaces.sbp.metrics.bootstrap.cli.SettingType;
import com.gigaspaces.sbp.metrics.bootstrap.cli.CalculateSettingsFromCliArgs;
import com.gigaspaces.sbp.metrics.bootstrap.props.MonitorDefaults;
import com.gigaspaces.sbp.metrics.bootstrap.props.XapDefaults;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 11/6/14
 * Time: 10:17 PM
 * <p/>
 * Processes CLI input and property file defaults and aggregates that information into
 * an authoritative, type-safe {@link GsMonitorSettingsImpl}
 * instance. Since all values can't be known until after application start-up (CLI input),
 * this class is the one that processes CLI input and brings in defaults, when necessary.
 */
@Component
public class CreateGsMonitorSettings {

    private static final String LOOKUP_LOCATOR_INVARIANT
            = "LookupLocators are required. Should be enforced by ParseArgs.invoke(args)";
    private static final String SPACE_NAMES_INVARIANT
            = "spaceNames are required. Should be enforced by ParseArgs.invoke(args)";
    private static final String USERNAME_PASSWORD_INVARIANT
            = "Username and password must be provided together.";
    private static final String UNSUPPORTED_OUTPUT_FORMAT
            = "Output format '%s' is unsupported by this build of the monitor.";
    private static final String INCONSISTENT_ALERT_SETTINGS
            = "Alerts are enabled, but not email address is provided.";

    @Resource
    private final XapDefaults xapDefaults;
    @Resource
    private final MonitorDefaults monitorDefaults;
    @Resource
    private final CalculateSettingsFromCliArgs calculateSettingsFromCliArgs;
    @Resource
    private final ValidateAndCreateFilePath validateAndCreateFilePath;
    @Resource
    private final GsMonitorSettingsImpl gsMonitorSettings;

    @Autowired
    public CreateGsMonitorSettings(CalculateSettingsFromCliArgs calculateSettingsFromCliArgs,
                                   XapDefaults xapDefaults,
                                   ValidateAndCreateFilePath validateAndCreateFilePath,
                                   GsMonitorSettingsImpl gsMonitorSettings,
                                   MonitorDefaults monitorDefaults) {

        assert xapDefaults != null : "need defaults";
        assert calculateSettingsFromCliArgs != null : "need a read argument processor";
        assert validateAndCreateFilePath != null : "need a file validator";
        assert gsMonitorSettings != null : "need application settings";
        assert monitorDefaults != null : "need monitor defaults";

        this.xapDefaults = xapDefaults;
        this.calculateSettingsFromCliArgs = calculateSettingsFromCliArgs;
        this.validateAndCreateFilePath = validateAndCreateFilePath;
        this.gsMonitorSettings = gsMonitorSettings;
        this.monitorDefaults = monitorDefaults;

    }

    /**
     * Processes the arguments passed to it (using {@link com.gigaspaces.sbp.metrics.bootstrap.cli.CalculateSettingsFromCliArgs#invoke(String[])}.
     *
     * @param args CLI input
     * @return a map from each interpreted setting to
     * @throws ParseException In the case that the user has passed some non-compliant input
     */
    public Map<SettingType, String> invokeOrThrow(String[] args) throws ParseException {

        CommandLine commandLine = calculateSettingsFromCliArgs.parse(args);
        Set<SettingType> set = calculateSettingsFromCliArgs.invoke(args);

        Map<SettingType, String> map = new HashMap<>();

        map.put(SettingType.LookupLocators, processLocators(commandLine, set));
        map.put(SettingType.SpaceNames, processSpaceNames(commandLine, set));
        map.put(SettingType.OutputFile, processOutputFile(commandLine));
        map.put(SettingType.Secured, processSecuritySetting(commandLine, set));

        processUsernameAndPassword(commandLine, set, map);
        processOutputFormat(map);
        processAlerting(commandLine, set, map);
        processMetricsIntervals(commandLine, set, map);
        processAlpha(map);
        processHostAndGscCount(commandLine, set, map);
        processDerivedMetricsPeriod(commandLine, set, map);

        processFlags(set, map);

        gsMonitorSettings.initialize(map);

        return map;
    }

    void processDerivedMetricsPeriod(CommandLine commandLine, Set<SettingType> set, Map<SettingType, String> map) throws ParseException {

        ensureArgs(commandLine, set, map);

        SettingType derivedPeriodMs = SettingType.DerivedMetricsPeriodInMs;
        String periodStr = getStringOrNull(commandLine, derivedPeriodMs);
        if( periodStr == null) map.put(derivedPeriodMs, monitorDefaults.derivedPeriodInMs().toString());
        else{
            validateNumber(periodStr, derivedPeriodMs);
            map.put(derivedPeriodMs, periodStr);
        }

    }

    void processMetricsIntervals(CommandLine commandLine, Set<SettingType> set, Map<SettingType, String> map) throws ParseException {

        ensureArgs(commandLine, set, map);

        SettingType metricDelay = SettingType.CollectMetricsInitialDelayInMs;
        String delayInMs = getStringOrNull(commandLine, metricDelay);

        if (delayInMs == null) map.put(metricDelay, monitorDefaults.metricDelayInMs().toString());
        else {
            validateNumber(delayInMs, metricDelay);
            map.put(metricDelay, delayInMs);
        }

        SettingType metricInterval = SettingType.CollectMetricsIntervalInMs;
        String interval = getStringOrNull(commandLine, metricInterval);

        if (interval == null) map.put(metricInterval, monitorDefaults.metricIntervalInMs().toString());
        else {
            validateNumber(interval, metricInterval);
            map.put(metricInterval, interval);
        }

    }

    void processHostAndGscCount(CommandLine commandLine, Set<SettingType> set, Map<SettingType, String> map) throws ParseException {

        ensureArgs(commandLine, set, map);

        SettingType gscCount = SettingType.GscCount;
        String gscStr = getStringOrNull(commandLine, gscCount);

        if (gscStr == null) map.put(gscCount, xapDefaults.gscCount().toString());
        else {
            validateNumber(gscStr, gscCount);
            map.put(gscCount, gscStr);
        }

        SettingType machineCount = SettingType.MachineCount;
        String machinesStr = getStringOrNull(commandLine, machineCount);

        if (machinesStr == null) map.put(machineCount, xapDefaults.hostMachineCount().toString());
        else {
            validateNumber(machinesStr, machineCount);
            map.put(machineCount, machinesStr.trim());
        }

    }

    private void ensureArgs(CommandLine commandLine, Set<SettingType> set, Map<SettingType, String> map) {
        assert set != null && set.size() > 0 : "Need a real set.";
        assert map != null && map.size() > 0 : "Need a real map.";
        assert commandLine != null : "Require a command line.";
    }

    private void validateNumber(String string, SettingType setting) throws ParseException {
        try {
            Integer.valueOf(string.trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new ParseException(String.format("Illegal number for %s [%s].", setting.name(), string));
        }
    }

    void processAlerting(CommandLine commandLine, Set<SettingType> set, Map<SettingType, String> map) throws ParseException {

        SettingType enabled = SettingType.AlertsEnabled;
        if (set.contains(enabled)) {
            map.put(enabled, Constants.DEFAULT_FLAG_VALUE);
        } else map.put(enabled, monitorDefaults.isEmailAlertingEnabled().toString());

        boolean enabledIndeed = map.get(enabled).equalsIgnoreCase(Boolean.TRUE.toString());

        if (!enabledIndeed) return;

        SettingType emailSetting = SettingType.SendAlertsByEmail;
        if (!set.contains(emailSetting)) throw new ParseException(INCONSISTENT_ALERT_SETTINGS);

        String emailAddy = getStringOrNull(commandLine, emailSetting);
        if (emailAddy == null || emailAddy.trim().length() == 0) throw new ParseException(INCONSISTENT_ALERT_SETTINGS);
        emailAddy = emailAddy.trim();

        map.put(emailSetting, emailAddy);

    }

    String processSecuritySetting(CommandLine commandLine, Set<SettingType> set) throws ParseException {

        assert set != null : "need a non-null set to process";

        SettingType secured = SettingType.Secured;

        if (!set.contains(secured)) return xapDefaults.isSecured().toString();

        String value = getStringOrNull(commandLine, secured);

        assert value != null : "ParseArgs apparently failed.";

        return value;

    }

    private void processFlags(Set<SettingType> set, Map<SettingType, String> map) {
        EnumSet<SettingType> ignore = EnumSet.of(
                SettingType.LookupLocators
                , SettingType.SpaceNames
                , SettingType.OutputFile
                , SettingType.Username
                , SettingType.Password
        );
        for (SettingType setting : set) if (!ignore.contains(setting)) map.put(setting, Constants.DEFAULT_FLAG_VALUE);
    }

    Map<SettingType, String> processOutputFormat(Map<SettingType, String> map) {
        OutputFormat fmt = monitorDefaults.outputFormat();
        switch (fmt) {
            case Csv:
                map.put(SettingType.Csv, Constants.DEFAULT_FLAG_VALUE);
                break;
            case LogFormat:
                map.put(SettingType.LogFormat, Constants.DEFAULT_FLAG_VALUE);
                break;
            default:
                throw new UnsupportedOperationException(String.format(UNSUPPORTED_OUTPUT_FORMAT, fmt.name()));
        }
        return map;
    }

    private void processAlpha(Map<SettingType, String> map) {
        map.put(SettingType.MovingAverageAlpha, monitorDefaults.movingAverageAlpha().toString());
    }

    /**
     * Returns the value passed on the command line, or the one in provided
     * by the {@link XapDefaults} instance
     *
     * @param commandLine command line
     * @return output file: full path
     * @throws ParseException if calculated file does not exist and cannot
     *                        be created (full path OR relative to the current working directory)
     */
    String processOutputFile(CommandLine commandLine) throws ParseException {

        String outputFile = getStringOrNull(commandLine, SettingType.OutputFile);
        if (outputFile == null) outputFile = monitorDefaults.outputFilename();

        return validateAndCreateFilePath.invoke(outputFile);

    }

    private String getStringOrNull(CommandLine commandLine, SettingType setting) {

        assert setting != null : "need a non-null setting to continue processing";

        String value = commandLine.getOptionValue(setting.getOptionCharacter());
        if (value == null || value.trim().length() == 0)
            value = commandLine.getOptionValue(setting.getOptionWord());

        if (value == null) return null;
        if (value.trim().length() == 0) return null;

        return value.trim();

    }

    /**
     * @param commandLine the command line from CLI
     * @param set         calculated set of Settings (including a {@link com.gigaspaces.sbp.metrics.bootstrap.cli.SettingType#LookupLocators})
     */
    String[] processUsernameAndPassword(CommandLine commandLine, Set<SettingType> set, Map<SettingType, String> map) throws ParseException {

        assert set != null : "need a non-null set to process";

        SettingType un = SettingType.Username;
        SettingType pw = SettingType.Password;

        boolean unSet = set.contains(un);
        boolean pwSet = set.contains(pw);

        if ((unSet || pwSet) && (!pwSet || !unSet))
            throw new ParseException(USERNAME_PASSWORD_INVARIANT);

        String username = getStringOrNull(commandLine, un);
        String password = getStringOrNull(commandLine, pw);

        if (username != null && username.trim().length() > 0) map.put(SettingType.Username, username.trim());
        if (password != null && password.trim().length() > 0) map.put(SettingType.Password, password.trim());

        return new String[]{username, password};

    }

    /**
     * @param commandLine the command line from CLI
     * @param set         calculated set of Settings (including a {@link com.gigaspaces.sbp.metrics.bootstrap.cli.SettingType#LookupLocators})
     * @return if no locator was provided
     * @throws ParseException if set doesn't have a {@link com.gigaspaces.sbp.metrics.bootstrap.cli.SettingType#LookupLocators} or commandLine
     *                        passed a bad value
     */
    String processLocators(CommandLine commandLine, Set<SettingType> set) throws ParseException {

        assert set != null : "need a non-null set to process";

        SettingType setting = SettingType.LookupLocators;
        if (!set.contains(setting))
            throw new ParseException(LOOKUP_LOCATOR_INVARIANT);

        String lookupLocators = getStringOrNull(commandLine, setting);
        if (lookupLocators == null || lookupLocators.trim().length() == 0)
            throw new ParseException(LOOKUP_LOCATOR_INVARIANT);

        // validate or throw ParseException

        return lookupLocators;
    }

    /**
     * @param commandLine the command line from CLI
     * @param set         calculated set of Settings (including a {@link com.gigaspaces.sbp.metrics.bootstrap.cli.SettingType#SpaceNames})
     * @return space names (we asked user to make them comma-delimited on the CLI)
     * @throws ParseException if set doesn't have a {@link com.gigaspaces.sbp.metrics.bootstrap.cli.SettingType#SpaceNames} or commandLine
     *                        passed a bad value
     */
    String processSpaceNames(CommandLine commandLine, Set<SettingType> set) throws ParseException {

        assert set != null : "need a non-null set to process";

        if (!set.contains(SettingType.SpaceNames))
            throw new ParseException(SPACE_NAMES_INVARIANT);

        String spaceNames = commandLine.getOptionValue(SettingType.SpaceNames.getOptionCharacter());
        if (spaceNames == null || spaceNames.trim().length() == 0)
            spaceNames = commandLine.getOptionValue(SettingType.SpaceNames.getOptionWord());
        if (spaceNames == null || spaceNames.trim().length() == 0)
            throw new ParseException(SPACE_NAMES_INVARIANT);

        // validate or throw ParseException

        return spaceNames.trim();

    }

}