package com.gigaspaces.sbp.metrics.bootstrap;

import com.gigaspaces.sbp.metrics.cli.Settings;
import com.gigaspaces.sbp.metrics.cli.ProcessArgs;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 11/6/14
 * Time: 10:17 PM
 */
@Component
public class CreateGsMonitorSettings {

    private static final String LOOKUP_LOCATOR_INVARIANT
            = "lookupLocators are required. Should be enforced by ParseArgs.invoke(args)";
    private static final String SPACE_NAMES_INVARIANT
            = "spaceNames are required. Should be enforced by ParseArgs.invoke(args)";
    private static final String USERNAME_PASSWORD_INVARIANT
            = "Username and password must be provided together.";

    @Resource
    private final ProcessArgs processArgs;
    @Resource
    private final XapDefaults xapDefaults;
    @Resource
    private final ValidateAndCreateFilePath validateAndCreateFilePath;
    @Resource
    private final GsMonitorSettings gsMonitorSettings;

    public CreateGsMonitorSettings(ProcessArgs processArgs,
                                   XapDefaults xapDefaults,
                                   ValidateAndCreateFilePath validateAndCreateFilePath,
                                   GsMonitorSettings gsMonitorSettings) {

        assert xapDefaults != null : "need defaults";
        assert processArgs != null : "need a read argument processor";
        assert validateAndCreateFilePath != null : "need a file validator";
        assert gsMonitorSettings != null : "need application settings";

        this.xapDefaults = xapDefaults;
        this.processArgs = processArgs;
        this.validateAndCreateFilePath = validateAndCreateFilePath;
        this.gsMonitorSettings = gsMonitorSettings;

    }

    public Map<Settings, String> invoke(String[] args) throws ParseException {

        CommandLine commandLine = processArgs.parse(args);
        Set<Settings> set = processArgs.invoke(args);

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

        gsMonitorSettings.initialize(map);

        return map;
    }

    /**
     * Returns the value passed on the command line, or the one in provided
     * by the {@link XapDefaults} instance
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

        assert setting != null : "need a non-null setting to continue processing";

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
    String[] processUsernameAndPassword(CommandLine commandLine, Set<Settings> set) throws ParseException{

        assert set != null : "need a non-null set to process";

        Settings un = Settings.Username;
        Settings pw = Settings.Password;

        boolean unSet = set.contains(un);
        boolean pwSet = set.contains(pw);

        if(( unSet || pwSet ) && ( !pwSet || !unSet ))
            throw new ParseException(USERNAME_PASSWORD_INVARIANT);

        String username = getStringOrNull(commandLine, un);
        String password = getStringOrNull(commandLine, pw);

        return new String[]{username, password};

    }

    /**
     * @param commandLine the command line from CLI
     * @param set calculated set of Settings (including a {@link Settings#LookupLocators})
     * @return if no locator was provided
     * @throws ParseException if set doesn't have a {@link Settings#LookupLocators} or commandLine
     * passed a bad value
     */
    String processLocators(CommandLine commandLine, Set<Settings> set) throws ParseException {

        assert set != null : "need a non-null set to process";

        Settings setting = Settings.LookupLocators;
        if( !set.contains(setting) )
            throw new ParseException(LOOKUP_LOCATOR_INVARIANT);

        String lookupLocators = getStringOrNull(commandLine, setting);
        if( lookupLocators == null || lookupLocators.trim().length() == 0 )
            throw new ParseException(LOOKUP_LOCATOR_INVARIANT);

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
    String processSpaceNames(CommandLine commandLine, Set<Settings> set) throws ParseException {

        assert set != null : "need a non-null set to process";

        if( !set.contains(Settings.SpaceNames ) )
            throw new ParseException(SPACE_NAMES_INVARIANT);

        String spaceNames = commandLine.getOptionValue(Settings.SpaceNames.getOptionCharacter());
        if (spaceNames == null || spaceNames.trim().length() == 0)
            spaceNames = commandLine.getOptionValue(Settings.SpaceNames.getOptionWord());
        if( spaceNames == null || spaceNames.trim().length() == 0 )
            throw new ParseException(SPACE_NAMES_INVARIANT);

        // validate or throw ParseException

        return spaceNames.trim();

    }

}