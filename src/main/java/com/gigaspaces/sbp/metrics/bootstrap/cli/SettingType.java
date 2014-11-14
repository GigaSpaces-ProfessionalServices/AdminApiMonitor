package com.gigaspaces.sbp.metrics.bootstrap.cli;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 7/29/14
 * Time: 6:41 PM
 */
public enum SettingType implements OptionLike {

    Csv("c", "csv-format", "Optional: Print multiple metrics per line (comma-separated). May not be used in combination with log-format.", false, false, false, true)
    , LogFormat("f", "log-format", "Optional: Print metrics in log format (one metric per line). May not be used in combination with csv-format.", false, false, false, true)

    , Secured("s", "secured", "Optional: When XAP security is enabled. format= true or false", false, false, true, true)
    , Username("u", "username", "Optional: When XAP security is enabled, username can be provided here.", true, false, false, true)
    , Password("p", "password", "Optional: When XAP security is enabled, password can be provided here.", true, false, false, true)

    , LookupLocators("l","locators", "Required: XAP lookup locators. format= host1:portnum,host2:portnum", true, true, false, true)
    , LookupGroups("g", "groups", "Optional: XAP lookup groups. format= group1,group2", true, false, true, true)
    , SpaceNames("z","spaces", "Required: XAP space names. Mirror space name must be provided for mirror statistics to be reported. format= space1,space2", true, true, false, true)
    , OutputFile("o","output-file", "Optional: Output file. format= fully-qualified or relative file path", true, false, true, true)
    , MovingAverageAlpha("a","ema-alpha", "Not meant for CLI", false , false , true, false )
    , AlertsEnabled("e","alerts","Not meant for CLI",false,false,true,true)
    , SendAlertsByEmail("m", "email-alerts", "When alerts occur, send a notification by email.", false, false, false, true), CollectMetricsIntervalInMs("i","metrics-interval","Interval at which metrics are collected (ms)." ,true, false, true, true)

    ;

    // private final static String USED_CHARS = "acefgilopsuz";

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

}
