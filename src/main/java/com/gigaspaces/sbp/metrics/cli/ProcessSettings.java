package com.gigaspaces.sbp.metrics.cli;

import com.gigaspaces.sbp.metrics.Settings;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 11/6/14
 * Time: 10:17 PM
 */
@Component
public class ProcessSettings {

    private static final String LOOKUP_LOCATOR_INVARIANT
            = "lookupLocators are required. Should be enforced by ParseArgs.invoke(args)";
    private static final String SPACE_NAMES_INVARIANT
            = "spaceNames are required. Should be enforced by ParseArgs.invoke(args)";
    private static final String USERNAME_PASSWORD_INVARIANT
            = "Username and password must be provided together.";

    private final ProcessArgs processArgs;
    private final XapDefaults xapDefaults;
    private final ValidateAndCreateFilePath validateAndCreateFilePath;

    public ProcessSettings(ProcessArgs processArgs,
                           XapDefaults xapDefaults,
                           ValidateAndCreateFilePath validateAndCreateFilePath) {
        assert xapDefaults != null : "need defaults";
        assert processArgs != null : "need a read argument processor";
        assert validateAndCreateFilePath != null : "need a file validator";
        this.xapDefaults = xapDefaults;
        this.processArgs = processArgs;
        this.validateAndCreateFilePath = validateAndCreateFilePath;

    }

    public Map<Settings, String> invoke(String[] args) throws ParseException {

        CommandLine commandLine = processArgs.parse(args);
        EnumSet<Settings> set = processArgs.invoke(args);

        Map<Settings, String> map = new HashMap<>();

        map.put(Settings.LookupLocators, processLocators(commandLine, set));
        map.put(Settings.SpaceNames, processSpaceNames(commandLine, set));
        map.put(Settings.OutputFile, processOutputFile(commandLine));

        EnumSet<Settings> ignore = EnumSet.of(
            Settings.LookupLocators
            , Settings.SpaceNames
            , Settings.OutputFile
            , Settings.Username
            , Settings.Password
        );

        String[] up = processUsernameAndPassword(commandLine, set);
        if( up[0] != null ) map.put(Settings.Username, up[0]);
        if( up[1] != null ) map.put(Settings.Password, up[1]);

        for( Settings setting : set ) if(!ignore.contains(setting)) map.put(setting, "true");

        return map;

    }

    /**
     * Returns the value passed on the command line, or the one in provided
     * by the {@link com.gigaspaces.sbp.metrics.cli.XapDefaults} instance
     * @param commandLine command line
     * @return output file: full path
     *
     * @throws ParseException if calculated file does not exist and cannot
     * be created (full path OR relative to the current working directory)
     */
    String processOutputFile(CommandLine commandLine) throws ParseException {

        String outputFile = getStringOrNull(commandLine, Settings.OutputFile);
        if (outputFile == null) outputFile = xapDefaults.outputFile();

        return validateAndCreateFilePath.invoke(outputFile);

    }


    private String getStringOrNull(CommandLine commandLine, Settings setting) {

        String value = commandLine.getOptionValue(setting.getOptionCharacter());
        if (value == null || value.trim().length() == 0)
            value = commandLine.getOptionValue(setting.getOptionWord());

        if (value == null) return null;
        if (value.trim().length() == 0) return null;

        return value.trim();

    }

    /**
     * @param commandLine the command line from CLI
     * @param set calculated set of Settings (including a {@link Settings#LookupLocators})
     */
    String[] processUsernameAndPassword(CommandLine commandLine, EnumSet<Settings> set) throws ParseException{

        Settings un = Settings.Username;
        Settings pw = Settings.Password;

        ParseException parseException = new ParseException(USERNAME_PASSWORD_INVARIANT);
        if( !set.contains(un) | !set.contains(pw) ) throw parseException;

        String username = getStringOrNull(commandLine, un);
        String password = getStringOrNull(commandLine, pw);

        if( username != null | password != null ) throw parseException;

        return new String[]{username, password};

    }

    /**
     * @param commandLine the command line from CLI
     * @param set calculated set of Settings (including a {@link Settings#LookupLocators})
     * @return if no locator was provided
     * @throws ParseException if set doesn't have a {@link Settings#LookupLocators} or commandLine
     * passed a bad value
     */
    String processLocators(CommandLine commandLine, EnumSet<Settings> set) throws ParseException {

        Settings setting = Settings.LookupLocators;
        ParseException parseException = new ParseException(LOOKUP_LOCATOR_INVARIANT);
        if( !set.contains(setting) ) throw parseException;

        String lookupLocators = getStringOrNull(commandLine, setting);
        if( lookupLocators == null || lookupLocators.trim().length() > 0 )
            throw parseException;

        // validate or throw ParseException

        return lookupLocators;
    }

    /**
     * @param commandLine the command line from CLI
     * @param set calculated set of Settings (including a {@link Settings#SpaceNames})
     * @return space names (we asked user to make them comma-delimited on the CLI)
     * @throws ParseException if set doesn't have a {@link Settings#SpaceNames} or commandLine
     * passed a bad value
     */
    String processSpaceNames(CommandLine commandLine, EnumSet<Settings> set) throws ParseException {

        ParseException parseException = new ParseException(SPACE_NAMES_INVARIANT);
        if( !set.contains(Settings.SpaceNames ) )
            throw parseException;

        String spaceNames = commandLine.getOptionValue(Settings.SpaceNames.getOptionCharacter());
        if (spaceNames == null || spaceNames.trim().length() == 0)
            spaceNames = commandLine.getOptionValue(Settings.SpaceNames.getOptionWord());
        if( spaceNames == null || spaceNames.trim().length() > 0 )
            throw parseException;

        // validate or throw ParseException

        return spaceNames.trim();

    }

}