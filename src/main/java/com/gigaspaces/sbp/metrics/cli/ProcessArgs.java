package com.gigaspaces.sbp.metrics.cli;

import com.gigaspaces.sbp.metrics.AdminApiMonitor;
import com.gigaspaces.sbp.metrics.Settings;
import org.apache.commons.cli.*;

import java.io.PrintWriter;
import java.util.EnumSet;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 7/29/14
 * Time: 6:40 PM
 * Makes our settings handling a little more type safe.
 */
public class ProcessArgs {

    private static final int TERMINAL_WIDTH = 110;

    /**
     * A set of settings
     * @param args args, as passed on the command line
     * @return such a beast
     */
    public EnumSet<Settings> invoke(String[] args) {
        EnumSet<Settings> settings = EnumSet.noneOf(Settings.class);
        try {
            CommandLine commandLine = new GnuParser().parse(getOptions(), args);
            if (commandLine.hasOption(Settings.AllMetrics.getOptionCharacter())) {
                settings = addTo(settings, Settings.AllMetrics);
            }
            if (commandLine.hasOption(Settings.Csv.getOptionCharacter())) {
                settings = addTo(settings, Settings.Csv);
            }
        } catch (ParseException e) {
            final PrintWriter writer = new PrintWriter(System.err);
            final HelpFormatter usageFormatter = new HelpFormatter();
            usageFormatter.printUsage(writer, TERMINAL_WIDTH, AdminApiMonitor.class.getSimpleName(), getOptions());
            writer.flush();
        }
        return settings;
    }

    private EnumSet<Settings> addTo(EnumSet<Settings> result, Settings setting) {
        if (result == null) return EnumSet.of(setting);
        else if (setting != null) result.add(setting);
        return result;
    }

    private Options getOptions() {
        final Options options = new Options();
        updateReference(options, Settings.AllMetrics);
        updateReference(options, Settings.Csv);
        return options;
    }

    private void updateReference(Options options, Settings settings) {
        options.addOption(settings.getOptionCharacter(), settings.getOptionWord(), false, settings.getOptionDescription());
    }

}