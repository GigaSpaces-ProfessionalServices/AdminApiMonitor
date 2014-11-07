package com.gigaspaces.sbp.metrics.cli;

import com.gigaspaces.sbp.metrics.Settings;
import org.apache.commons.cli.*;

import java.util.EnumSet;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 7/29/14
 * Time: 6:40 PM
 * Makes our settings handling a little more type safe.
 */
public class ProcessArgs {

    private static final String MULTIPLE_FORMAT_ERROR = String.format("CSV and log formats cannot be used at the same time ('-%s' and '-%s').", Settings.Csv.getOptionCharacter(), Settings.LogFormat.getOptionCharacter());
    private static final String LOOKUP_LOCATORS_REQUIRED = String.format("Lookup locators must be supplied ('-%s').", Settings.LookupLocators.getOptionCharacter());
    private static final String OUTPUT_FILE_REQUIRED = String.format("Output file is a required parameter ('-%s').", Settings.OutputFile.getOptionCharacter());
    private static final String SPACE_NAMES_REQUIRED = String.format("Spaces is a required parameter ('-%s').", Settings.SpaceNames);

    /**
     * A set of settings
     *
     * @param args args, as passed on the command line
     * @return such a beast
     */
    public EnumSet<Settings> invoke(String[] args) throws ParseException {
        EnumSet<Settings> settings = EnumSet.noneOf(Settings.class);

        CommandLine commandLine = new GnuParser().parse(allOptions(), args);

        ensureOnlyOneOutputFormat(commandLine);
        ensureLocators(commandLine);
        ensureOutputFile(commandLine);
        ensureSpaces(commandLine);

        settings = addOptionIfPresent(commandLine, settings, Settings.AllMetrics);
        settings = addOptionIfPresent(commandLine, settings, Settings.Csv);
        settings = addOptionIfPresent(commandLine, settings, Settings.Secured);
        settings = addOptionIfPresent(commandLine, settings, Settings.LogFormat);

        settings = addOptionIfPresent(commandLine, settings, Settings.LookupLocators);
        settings = addOptionIfPresent(commandLine, settings, Settings.LookupGroups);
        settings = addOptionIfPresent(commandLine, settings, Settings.SpaceNames);
        settings = addOptionIfPresent(commandLine, settings, Settings.OutputFile);

        return settings;
    }

    private void ensureOnlyOneOutputFormat(CommandLine commandLine) throws ParseException {
        if((commandLine.hasOption(Settings.Csv.getOptionCharacter()) || commandLine.hasOption(Settings.Csv.getOptionWord())) &&
                (commandLine.hasOption(Settings.LogFormat.getOptionCharacter()) || commandLine.hasOption(Settings.LogFormat.getOptionWord()))){
            throw new ParseException(MULTIPLE_FORMAT_ERROR);
        }
    }

    private void ensureLocators(CommandLine commandLine) throws ParseException {
        if( !commandLine.hasOption(Settings.LookupLocators.getOptionCharacter()) && !commandLine.hasOption(Settings.LookupLocators.getOptionWord()))
            throw new ParseException(LOOKUP_LOCATORS_REQUIRED);
    }

    private void ensureOutputFile(CommandLine commandLine) throws ParseException {
        if( !commandLine.hasOption(Settings.OutputFile.getOptionCharacter()) && !commandLine.hasOption(Settings.OutputFile.getOptionWord()))
            throw new ParseException(OUTPUT_FILE_REQUIRED);
    }

    private void ensureSpaces(CommandLine commandLine) throws ParseException{
        if( !commandLine.hasOption(Settings.SpaceNames.getOptionCharacter()) && !commandLine.hasOption(Settings.SpaceNames.getOptionWord()))
            throw new ParseException(SPACE_NAMES_REQUIRED);
    }

    private EnumSet<Settings> addOptionIfPresent(CommandLine line, EnumSet<Settings> result, Settings setting){
        if( line.hasOption(setting.getOptionCharacter())|| line.hasOption(setting.getOptionWord()))
            result = addTo(result, setting);
        return result;
    }

    private EnumSet<Settings> addTo(EnumSet<Settings> result, Settings setting) {
        if (result == null) return EnumSet.of(setting); // NPE waiting to happen if setting == null???
        else if (setting != null) result.add(setting);
        return result;
    }

    public Options allOptions() {
        final Options options = new Options();
        addOptionByReference(options, Settings.AllMetrics);
        addOptionByReference(options, Settings.Csv);
        addOptionByReference(options, Settings.Secured);
        addOptionByReference(options, Settings.LogFormat);

        addParamByReference(options, Settings.LookupLocators);
        addParamByReference(options, Settings.LookupGroups);
        addParamByReference(options, Settings.SpaceNames);
        addParamByReference(options, Settings.OutputFile);

        return options;
    }

    private void addOptionByReference(Options options, Settings anOption) {
        options.addOption(anOption.getOptionCharacter(), anOption.getOptionWord(), false, anOption.getOptionDescription());
    }

    private void addParamByReference(Options options, Settings param){
        options.addOption(param.getOptionCharacter(), param.getOptionWord(), true, param.getOptionDescription());
    }

}
