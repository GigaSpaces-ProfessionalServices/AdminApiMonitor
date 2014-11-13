package com.gigaspaces.sbp.metrics.bootstrap;

import com.gigaspaces.sbp.metrics.cli.Settings;
import com.gigaspaces.sbp.metrics.cli.ProcessArgs;
import com.jasonnerothin.testing.Strings;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CreateGsMonitorSettingsTest {

    private CreateGsMonitorSettings testInstance;

    @Mock
    private ProcessArgs processArgs;
    @Mock
    private XapDefaults xapDefaults;
    @Mock
    private ValidateAndCreateFilePath validateAndCreateFilePath;
    @Mock
    private GsMonitorSettings gsMonitorSettings;

    @Mock
    private CommandLine testCommandLine;

    private Strings strings = new Strings();

    @Before
    public void setUp() throws Exception {
        testInstance = new CreateGsMonitorSettings(processArgs, xapDefaults, validateAndCreateFilePath, gsMonitorSettings);
    }


    @Test
    public void testInvoke() throws Exception {

        final EnumSet<Settings> required = EnumSet.of(Settings.LookupLocators, Settings.SpaceNames);
        final EnumSet<Settings> defaulted = EnumSet.of(Settings.Username, Settings.Password, Settings.OutputFile);
        Set<Settings> requiredAndDefaulted = new HashSet<Settings>(){{addAll(required);addAll(defaulted);}};

        Set<Settings> returnMe = new HashSet<>();
        for( Settings setting : Settings.values() ) if( !requiredAndDefaulted.contains(setting)) returnMe.add(setting);
        returnMe.addAll(required);

        // build arg list
        List<String> argList = new ArrayList<>();
        for( Settings setting : Settings.values() ) if( !setting.hasArgument() ) argList.add(String.format("-%s",setting.getOptionCharacter()));
        argList.add(String.format("-%s", Settings.LookupLocators.getOptionCharacter()));
        String testLocator = strings.alphabetic();
        argList.add(testLocator);
        argList.add(String.format("-%s", Settings.SpaceNames.getOptionCharacter()));
        String testSpaceNames = strings.alphabetic();
        argList.add(testSpaceNames);
        String[] testArgs = new String[argList.size()];
        argList.toArray(testArgs);

        doReturn(returnMe).when(processArgs).invoke(eq(testArgs));
        doReturn(testLocator).when(testCommandLine).getOptionValue(Settings.LookupLocators.getOptionWord());
        doReturn(testSpaceNames).when(testCommandLine).getOptionValue(Settings.SpaceNames.getOptionCharacter());
        doReturn(testCommandLine).when(processArgs).parse(eq(testArgs));

        Map<Settings, String> actual = testInstance.invoke(testArgs);

        Set<Settings> set = new HashSet<Settings>(asList(Settings.values()));
        set.removeAll(required);
        set.removeAll(defaulted);
        for( Settings setting : set ){
            String value = actual.get(setting);
            assertEquals(value, "true");
        }

    }

    @Test
    public void testProcessOutputFileCreatesNewFileWhenNoOutputFileIsProvidedByCLI() throws Exception {

        String filePath = strings.alphabetic();

        doReturn(null).when(testCommandLine).getOptionValue(eq(Settings.OutputFile.getOptionCharacter()));
        doReturn(null).when(testCommandLine).getOptionValue(eq(Settings.OutputFile.getOptionWord()));
        doReturn(filePath).when(xapDefaults).outputFile();
        doReturn(filePath).when(validateAndCreateFilePath).invoke(filePath);

        String actual = testInstance.processOutputFile(testCommandLine);

        verify(validateAndCreateFilePath).invoke(eq(filePath));
        assertEquals(filePath, actual);

    }

    @Test(expected = ParseException.class)
    public void testProcessUsernameAndPasswordThrowsWhenNoUname() throws Exception {

        Settings setting = Settings.Password;
        String testPw = strings.alphabetic();
        doReturn(testPw).when(testCommandLine).getOptionValue(eq(setting.getOptionCharacter()));
        doReturn(testPw).when(testCommandLine).getOptionValue(eq(setting.getOptionWord()));

        testInstance.processUsernameAndPassword(testCommandLine, EnumSet.of(setting));

    }

    @Test(expected = ParseException.class)
    public void testProcessUsernameAndPasswordThrowsWhenNoPassword() throws Exception {

        Settings setting = Settings.Username;
        String testName = strings.alphabetic();

        doReturn(testName).when(testCommandLine).getOptionValue(eq(setting.getOptionCharacter()));
        doReturn(testName).when(testCommandLine).getOptionValue(eq(setting.getOptionWord()));

        testInstance.processUsernameAndPassword(testCommandLine, EnumSet.of(setting));


    }

    @Test
    public void testProcessUsernameAndPassword() throws Exception{

        String testName = strings.alphabetic();
        String testPw = strings.alphabetic();

        doReturn(testName).when(testCommandLine).getOptionValue(eq(Settings.Username.getOptionCharacter()));
        doReturn(testName).when(testCommandLine).getOptionValue(eq(Settings.Username.getOptionWord()));
        doReturn(testPw).when(testCommandLine).getOptionValue(eq(Settings.Password.getOptionCharacter()));
        doReturn(testPw).when(testCommandLine).getOptionValue(eq(Settings.Password.getOptionWord()));

        String[] actual = testInstance.processUsernameAndPassword(testCommandLine, EnumSet.of(Settings.Username, Settings.Password));

        assertEquals(testName, actual[0]);
        assertEquals(testPw, actual[1]);

    }

    @Test
    public void testProcessLocators() throws Exception {

        String testLocators = "google:123";

        doReturn(testLocators).when(testCommandLine).getOptionValue(eq(Settings.LookupLocators.getOptionCharacter()));
        doReturn(testLocators).when(testCommandLine).getOptionValue(eq(Settings.LookupLocators.getOptionWord()));

        String actual = testInstance.processLocators(testCommandLine, EnumSet.of(Settings.LookupLocators));

        assertEquals(testLocators, actual);
    }

    @Test
    public void testProcessSpaceNames() throws Exception {

        String testSpaceNames = strings.alphabetic() + "," + strings.alphabetic();

        doReturn(testSpaceNames).when(testCommandLine).getOptionValue(eq(Settings.SpaceNames.getOptionCharacter()));
        doReturn(testSpaceNames).when(testCommandLine).getOptionValue(eq(Settings.SpaceNames.getOptionWord()));

        String actual = testInstance.processSpaceNames(testCommandLine, EnumSet.of(Settings.SpaceNames));

        assertEquals(testSpaceNames, actual);
    }

}