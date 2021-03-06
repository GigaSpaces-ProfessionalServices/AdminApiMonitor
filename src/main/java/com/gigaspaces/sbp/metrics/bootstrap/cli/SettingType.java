package com.gigaspaces.sbp.metrics.bootstrap.cli;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 7/29/14
 * Time: 6:41 PM
 */
public enum SettingType implements OptionLike {

    Csv("c", "csv-format", "Optional: Print multiple metrics per line (comma-separated). May not be used in combination with log-format. (default: true)"
            , false, false, false, true)
    , LogFormat("f", "log-format", "Optional: Print metrics in log format (one metric per line). May not be used in combination with csv-format. (default: false)"
            , false, false, false, true)

    , Secured("s", "secured", "Optional: When XAP security is enabled. format = true (default: false)"
            , false, false, true, true)
    , Username("u", "username", "Optional: When XAP security is enabled, username can be provided here. arg format = joeschmo (default: none)"
            , true, false, false, true)
    , Password("p", "password", "Optional: When XAP security is enabled, password can be provided here. arg format = jozsecrt (default: none)"
            , true, false, false, true)

    , LookupLocators("l","locators", "Required: XAP lookup locators. arg format = host1:portnum,host2:portnum (default: none)"
            , true, true, false, true)
    , LookupGroups("g", "groups", "Optional: XAP lookup groups. arg format = group1,group2 (default: supplied by XAP)"
            , true, false, true, true)
    , SpaceNames("z","spaces", "Required: XAP space names. Mirror space name must be provided for mirror statistics to be reported. arg format = space1,space2 (default: none)"
            , true, true, false, true)
    , OutputFile("o","output-file", "Optional: Output file. arg format = fully-qualified or relative file path (default: gs-monitor.csv)"
            , true, false, true, true)
    , MovingAverageAlpha("a","ema-alpha", "Not meant for CLI"
            , false , false , true, false )
    , AlertsEnabled("e","alerts","Optional: Whether to collect alerts. (default: false)"
            , false, false, true, true)
    , SendAlertsByEmail("m", "send-email-alerts", "Optional: When alerts occur, send a notification by email. arg format = some.address@mx.yourhost.com (default: none, turned off)"
            , true, false, false, true)
    , CollectMetricsIntervalInMs("i","metrics-interval","Optional: Interval at which metrics are collected (ms). arg format = 10000 (default: 120000)"
            , true, false, true, true)
    , CollectMetricsInitialDelayInMs("d", "initial-delay-ms", "Not meant for CLI"
            , true, true, true, false)
    , DerivedMetricsPeriodInMs("v", "derived-metric-period", "Not meant for CLI"
            , true , true, true, false )
    , MachineCount("h", "machine-count", "Optional: Number of machines in cluster. arg format = 12 (default: 1)"
            , true, false, true, true)
    , GscCount("n", "gsc-count", "Optional: Number of GSCs in cluster. arg format = 6 (default: 2)"
            , true, false, true, true)
    ;

    // private final static String USED_CHARS = "acdefghilmnopsuvz";

    private final String optionCharacter;
    private final String optionWord;
    private final String optionDescription;
    private final boolean hasArgument;
    private final boolean isRequired;
    private final boolean hasDefault;
    private final boolean isUsedByCli;

    SettingType(String optionCharacter, String optionWord, String optionDescription, boolean hasArgument, boolean isRequired, boolean hasDefault, boolean usedByCli) {
        this.optionCharacter = optionCharacter;
        this.optionWord = optionWord;
        this.optionDescription = optionDescription;
        this.hasArgument = hasArgument;
        this.isRequired = isRequired;
        this.hasDefault = hasDefault;
        this.isUsedByCli = usedByCli;
    }

    @Override
    public String getOptionCharacter() {
        return optionCharacter;
    }

    @Override
    public String getOptionWord() {
        return optionWord;
    }

    @Override
    public String getOptionDescription() {
        return optionDescription;
    }

    @Override
    public boolean hasArgument(){ return hasArgument; }

    @Override
    public boolean isRequired(){ return isRequired; }

    @Override
    public boolean hasDefault() { return hasDefault; }

    @Override
    public boolean isUsedByCli(){ return isUsedByCli; }

}
