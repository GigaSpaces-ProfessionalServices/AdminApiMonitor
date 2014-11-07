package com.gigaspaces.sbp.metrics;

import com.gigaspaces.sbp.metrics.cli.OptionLike;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 7/29/14
 * Time: 6:41 PM
 */
public enum Settings implements OptionLike {

    AllMetrics("a", "all-metrics", "Print all metrics.", false)
    , Csv("c", "csv", "Emit metrics on a single line, separated by commas.", false)
    , Secured("s", "secured", "If GigaSpaces security is enabled on the XAP grid.", false)
    , LogFormat("f", "log", "Emit metrics in log format (one metric per line).", false)

    , LookupLocators("l","locators", "XAP Lookup locators", true)
    , LookupGroups("g", "groups", "XAP lookup groups", true)
    , SpaceNames("z","spaces", "XAP Space names (comma-delimited list)", true)
    , OutputFile("o","file", "Output file (relative location)", true);
    ;

    private final String optionCharacter;
    private final String optionWord;
    private final String optionDescription;
    private final boolean isParameter;

    Settings(String optionCharacter, String optionWord, String optionDescription, boolean isParameter) {
        this.optionCharacter = optionCharacter;
        this.optionWord = optionWord;
        this.optionDescription = optionDescription;
        this.isParameter = isParameter;
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
    public boolean isParameter(){ return isParameter; }

}
