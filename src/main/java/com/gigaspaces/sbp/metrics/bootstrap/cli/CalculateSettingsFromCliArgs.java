package com.gigaspaces.sbp.metrics.bootstrap.cli;

import org.apache.commons.cli.*;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 7/29/14
 * Time: 6:40 PM
 * Makes our settings handling a little more type safe.
 */
@Component
public class CalculateSettingsFromCliArgs {

    private static final String MULTIPLE_FORMAT_ERROR = String.format("CSV and log formats cannot be used at the same time ('-%s' and '-%s').", SettingType.Csv.getOptionCharacter(), SettingType.LogFormat.getOptionCharacter());
    private static final String LOOKUP_LOCATORS_REQUIRED = String.format("Lookup locators must be supplied ('-%s').", SettingType.LookupLocators.getOptionCharacter());
    private static final String SPACE_NAMES_REQUIRED = String.format("Spaces is a required parameter ('-%s').", SettingType.SpaceNames);
    private static final String ONE_REQUIRES_TWO_ERROR = "Using '%s', means that '%s' is required also.";

    Parser parser = new GnuParser();

    /**
     * A set of settings
     *
     * @param args args, as passed on the command line
     * @return such a beast
     */
    public Set<SettingType> invoke(String[] args) throws ParseException {

        EnumSet<SettingType> settings = EnumSet.noneOf(SettingType.class);

        CommandLine commandLine = parser.parse(allOptions(), args);

        ensureOnlyOneOutputFormat(commandLine);
        ensureLocators(commandLine);
        ensureSpaces(commandLine);

        settings = addOptionIfPresent(commandLine, settings, SettingType.Csv);
        settings = addOptionIfPresent(commandLine, settings, SettingType.Secured);
        settings = addOptionIfPresent(commandLine, settings, SettingType.LogFormat);

        settings = addOptionIfPresent(commandLine, settings, SettingType.LookupLocators);
        settings = addOptionIfPresent(commandLine, settings, SettingType.LookupGroups);
        settings = addOptionIfPresent(commandLine, settings, SettingType.SpaceNames);
        settings = addOptionIfPresent(commandLine, settings, SettingType.OutputFile);

        settings = addOptionIfPresent(commandLine, settings, SettingType.Username);
        settings = addOptionIfPresent(commandLine, settings, SettingType.Password);

        ensureUsernameXandPassword(settings);

        return settings;

    }

    private void ensureUsernameXandPassword(EnumSet<SettingType> settings) throws ParseException {
        boolean username = settings.contains(SettingType.Username);
        boolean password = settings.contains(SettingType.Password);
        if (username && !password)
            throw new ParseException(String.format(ONE_REQUIRES_TWO_ERROR, SettingType.Username.name(), SettingType.Password.name()));
        else if (password && !username)
            throw new ParseException(String.format(ONE_REQUIRES_TWO_ERROR, SettingType.Password.name(), SettingType.Username.name()));
    }

    public CommandLine parse(String[] args) throws ParseException {
        return parser.parse(allOptions(), args);
    }

    private void ensureOnlyOneOutputFormat(CommandLine commandLine) throws ParseException {
        if ((commandLine.hasOption(SettingType.Csv.getOptionCharacter()) || commandLine.hasOption(SettingType.Csv.getOptionWord())) &&
                (commandLine.hasOption(SettingType.LogFormat.getOptionCharacter()) || commandLine.hasOption(SettingType.LogFormat.getOptionWord()))) {
            throw new ParseException(MULTIPLE_FORMAT_ERROR);
        }
    }

    private void ensureLocators(CommandLine commandLine) throws ParseException {
        if (!commandLine.hasOption(SettingType.LookupLocators.getOptionCharacter()) && !commandLine.hasOption(SettingType.LookupLocators.getOptionWord()))
            throw new ParseException(LOOKUP_LOCATORS_REQUIRED);
    }

    private void ensureSpaces(CommandLine commandLine) throws ParseException {
        if (!commandLine.hasOption(SettingType.SpaceNames.getOptionCharacter()) && !commandLine.hasOption(SettingType.SpaceNames.getOptionWord()))
            throw new ParseException(SPACE_NAMES_REQUIRED);
    }

    private EnumSet<SettingType> addOptionIfPresent(CommandLine line, EnumSet<SettingType> result, SettingType setting) {
        if (line.hasOption(setting.getOptionCharacter()) || line.hasOption(setting.getOptionWord()))
            result = addTo(result, setting);
        return result;
    }

    private EnumSet<SettingType> addTo(EnumSet<SettingType> result, SettingType setting) {
        if (result == null) return EnumSet.of(setting); // NPE waiting to happen if setting == null???
        else if (setting != null) result.add(setting);
        return result;
    }

    public Options allOptions() {

        final Options options = requiredOptions();

        addOptionByReference(options, SettingType.LookupGroups);

        addOptionByReference(options, SettingType.OutputFile);
        addOptionByReference(options, SettingType.Csv);
        addOptionByReference(options, SettingType.LogFormat);

        addOptionByReference(options, SettingType.Secured);
        addOptionByReference(options, SettingType.Username);
        addOptionByReference(options, SettingType.Password);

        addOptionByReference(options, SettingType.GscCount);
        addOptionByReference(options, SettingType.MachineCount);

        addOptionByReference(options, SettingType.AlertsEnabled);

        return options;
    }

    public Options requiredOptions() {

        final Options options = new Options();
        addOptionByReference(options, SettingType.LookupLocators);
        addOptionByReference(options, SettingType.SpaceNames);
        return options;

    }

    private void addOptionByReference(Options options, SettingType anOption) {
        options.addOption(asOption(anOption));
    }

    private Option asOption(SettingType setting) {
        Option opt = new Option(setting.getOptionCharacter(), setting.hasArgument(), setting.getOptionDescription());
        opt.setLongOpt(setting.getOptionWord());
        opt.setDescription(setting.getOptionDescription());
        opt.setRequired(setting.isRequired());
        return opt;
    }

}