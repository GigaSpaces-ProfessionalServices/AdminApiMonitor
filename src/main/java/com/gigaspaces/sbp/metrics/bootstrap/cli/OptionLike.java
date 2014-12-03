package com.gigaspaces.sbp.metrics.bootstrap.cli;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 7/29/14
 * Time: 7:08 PM
 */
public interface OptionLike {

    String getOptionCharacter();

    String getOptionWord();

    String getOptionDescription();

    /**
     * @return if this setting takes an argument
     */
    boolean hasArgument();

    boolean isRequired();

    boolean hasDefault();

    boolean isUsedByCli();

}
