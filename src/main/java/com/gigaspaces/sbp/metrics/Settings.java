package com.gigaspaces.sbp.metrics;

import com.gigaspaces.sbp.metrics.cli.OptionLike;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 7/29/14
 * Time: 6:41 PM
 */
public enum Settings implements OptionLike {


    AllMetrics("a", "all-metrics", "Print all metrics.")
    , Csv("c", "csv", "Emit metrics on a single line, separated by commas.")
    , Secured("s", "secured", "If GigaSpaces security is enabled.")
    ;

    private final String optionCharacter;
    private final String optionWord;
    private final String optionDescription;

    Settings(String optionCharacter, String optionWord, String optionDescription) {
        this.optionCharacter = optionCharacter;
        this.optionWord = optionWord;
        this.optionDescription = optionDescription;
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

}
