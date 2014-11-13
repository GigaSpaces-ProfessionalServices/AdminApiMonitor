package com.gigaspaces.sbp.metrics.cli;

import com.gigaspaces.sbp.metrics.cli.OptionLike;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 7/29/14
 * Time: 6:41 PM
 */
public enum Settings implements OptionLike {

    Csv("c", "csv-format", "Optional: Print multiple metrics per line (comma-separated). May not be used in combination with log-format.", false, false, false)
    , LogFormat("f", "log-format", "Optional: Print metrics in log format (one metric per line). May not be used in combination with csv-format.", false, false, false)

    , Secured("s", "secured", "Optional: When XAP security is enabled. format= true or false", false, false, true)
    , Username("u", "username", "Optional: When XAP security is enabled, username can be provided here.", true, false, false)
    , Password("p", "password", "Optional: When XAP security is enabled, password can be provided here.", true, false, false)

    , LookupLocators("l","locators", "Required: XAP lookup locators. format= host1:portnum,host2:portnum", true, true, false)
    , LookupGroups("g", "groups", "Optional: XAP lookup groups. format= group1,group2", true, false, true)
    , SpaceNames("z","spaces", "Required: XAP space names. Mirror space name must be provided for mirror statistics to be reported. format= space1,space2", true, true, false)
    , OutputFile("o","output-file", "Optional: Output file. format= fully-qualified or relative file path", true, false, true);
    ;

    private final String optionCharacter;
    private final String optionWord;
    private final String optionDescription;
    private final boolean hasArgument;
    private final boolean isRequired;
    private final boolean hasDefault;

    Settings(String optionCharacter, String optionWord, String optionDescription, boolean hasArgument, boolean isRequired, boolean hasDefault) {
        this.optionCharacter = optionCharacter;
        this.optionWord = optionWord;
        this.optionDescription = optionDescription;
        this.hasArgument = hasArgument;
        this.isRequired = isRequired;
        this.hasDefault = hasDefault;
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
