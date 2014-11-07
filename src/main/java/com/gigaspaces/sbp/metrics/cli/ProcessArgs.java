package com.gigaspaces.sbp.metrics.cli;

import com.gigaspaces.sbp.metrics.Settings;
import org.apache.commons.cli.*;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 7/29/14
 * Time: 6:40 PM
 * Makes our settings handling a little more type safe.
 */
public class ProcessArgs {

    private static final int TERMINAL_WIDTH = 130;

    private static final String MULTIPLE_FORMAT_ERROR = String.format("CSV and log formats cannot be used at the same time ('-%s' and '-%s').", Settings.Csv.getOptionCharacter(), Settings.LogFormat.getOptionCharacter());
    private static final String LOOKUP_LOCATORS_REQUIRED = String.format("Lookup locators must be supplied ('-%s').", Settings.LookupLocators.getOptionCharacter());
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
        ensureSpaces(commandLine);

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
        if ((commandLine.hasOption(Settings.Csv.getOptionCharacter()) || commandLine.hasOption(Settings.Csv.getOptionWord())) &&
                (commandLine.hasOption(Settings.LogFormat.getOptionCharacter()) || commandLine.hasOption(Settings.LogFormat.getOptionWord()))) {
            throw new ParseException(MULTIPLE_FORMAT_ERROR);
        }
    }

    private void ensureLocators(CommandLine commandLine) throws ParseException {
        if (!commandLine.hasOption(Settings.LookupLocators.getOptionCharacter()) && !commandLine.hasOption(Settings.LookupLocators.getOptionWord()))
            throw new ParseException(LOOKUP_LOCATORS_REQUIRED);
    }

    private void ensureSpaces(CommandLine commandLine) throws ParseException {
        if (!commandLine.hasOption(Settings.SpaceNames.getOptionCharacter()) && !commandLine.hasOption(Settings.SpaceNames.getOptionWord()))
            throw new ParseException(SPACE_NAMES_REQUIRED);
    }

    private EnumSet<Settings> addOptionIfPresent(CommandLine line, EnumSet<Settings> result, Settings setting) {
        if (line.hasOption(setting.getOptionCharacter()) || line.hasOption(setting.getOptionWord()))
            result = addTo(result, setting);
        return result;
    }

    private EnumSet<Settings> addTo(EnumSet<Settings> result, Settings setting) {
        if (result == null) return EnumSet.of(setting); // NPE waiting to happen if setting == null???
        else if (setting != null) result.add(setting);
        return result;
    }

    public Options allOptions() {

        final Options options = requiredOptions();

        addParamByReference(options, Settings.LookupGroups);
        addParamByReference(options, Settings.OutputFile);

        addOptionByReference(options, Settings.Csv);
        addOptionByReference(options, Settings.Secured);
        addOptionByReference(options, Settings.LogFormat);

        return options;
    }

    public Options requiredOptions() {

        final Options options = new Options();
        addParamByReference(options, Settings.LookupLocators);
        addParamByReference(options, Settings.SpaceNames);
        return options;

    }

    private void addOptionByReference(Options options, Settings anOption) {
        options.addOption(asOption(anOption));
    }

    private void addParamByReference(Options options, Settings param) {
        options.addOption(asOption(param));
    }

    private Option asOption(Settings setting) {
        Option opt = new Option(setting.getOptionCharacter(), setting.hasArgument(), setting.getOptionDescription());
        opt.setLongOpt(setting.getOptionWord());
        opt.setDescription(setting.getOptionDescription());
        opt.setRequired(setting.isRequired());
        return opt;
    }

    public EnumSet<Settings> invokeOrDie(String[] args) {

        try {

            return invoke(args);

        } catch (ParseException e) {

            PrintStream out = System.err;
            out.println(e.getMessage());
            final PrintWriter writer = new PrintWriter(out);

            String headsAndTails = "\n.\n\n\n";

            final HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.setSyntaxPrefix("usage:\n\n");
            String cmdLineSyntax = "java -jar gs-monitor.jar";
            Options allOpts = allOptions();
            helpFormatter.printHelp(writer, TERMINAL_WIDTH, cmdLineSyntax, headsAndTails, allOpts, 5, 5, headsAndTails, true);
            writer.flush();

            System.exit(666);

            return null;

        }

    }

}