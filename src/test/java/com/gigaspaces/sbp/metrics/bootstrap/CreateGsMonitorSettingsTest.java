package com.gigaspaces.sbp.metrics.bootstrap;

import com.gigaspaces.sbp.metrics.Constants;
import com.gigaspaces.sbp.metrics.bootstrap.cli.CalculateSettingsFromCliArgs;
import com.gigaspaces.sbp.metrics.bootstrap.cli.OutputFormat;
import com.gigaspaces.sbp.metrics.bootstrap.cli.SettingType;
import com.gigaspaces.sbp.metrics.bootstrap.props.MonitorDefaults;
import com.gigaspaces.sbp.metrics.bootstrap.props.XapDefaults;
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
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CreateGsMonitorSettingsTest {

    private CreateGsMonitorSettings testInstance;

    @Mock
    private CalculateSettingsFromCliArgs calculateSettingsFromCliArgs;
    @Mock
    private XapDefaults xapDefaults;
    @Mock
    private ValidateAndCreateFilePath validateAndCreateFilePath;
    @Mock
    private GsMonitorSettingsImpl gsMonitorSettings;
    @Mock
    private MonitorDefaults monitorDefaults;

    @Mock
    private CommandLine testCommandLine;

    private Strings strings = new Strings();

    @Before
    public void setUp() throws Exception {
        testInstance = new CreateGsMonitorSettings(calculateSettingsFromCliArgs, xapDefaults, validateAndCreateFilePath, gsMonitorSettings, monitorDefaults);
    }

    /**
     * Clearly this method is getting a bit too complicated.
     * @throws Exception never
     */
    @Test
    public void testInvokeOrThrow() throws Exception {

        final EnumSet<SettingType> required = EnumSet.of(SettingType.LookupLocators, SettingType.SpaceNames);
        final EnumSet<SettingType> defaulted = EnumSet.of(SettingType.Username, SettingType.Password, SettingType.OutputFile, SettingType.Secured);
        Set<SettingType> requiredOrDefaulted = new HashSet<SettingType>(){{addAll(required);addAll(defaulted);}};

        Set<SettingType> returnMe = new HashSet<>();
        for( SettingType setting : SettingType.values() ) if( !requiredOrDefaulted.contains(setting)) returnMe.add(setting);
        returnMe.addAll(required);

        // build arg list
        List<String> argList = new ArrayList<>();
        for( SettingType setting : SettingType.values() ) if( !setting.hasArgument() ) argList.add(String.format("-%s",setting.getOptionCharacter()));
        argList.add(String.format("-%s", SettingType.LookupLocators.getOptionCharacter()));
        String testLocator = strings.alphabetic();
        argList.add(testLocator);
        argList.add(String.format("-%s", SettingType.SpaceNames.getOptionCharacter()));
        String testSpaceNames = strings.alphabetic();
        argList.add(testSpaceNames);
        argList.add(String.format("-%s", SettingType.Username.getOptionCharacter()));
        argList.add(strings.alphabetic()); // username
        argList.add(String.format("-%s", SettingType.Password.getOptionCharacter()));
        argList.add(strings.alphabetic()); // pw
        argList.add(String.format("-%s", SettingType.Secured.getOptionCharacter()));
        argList.add(Boolean.TRUE.toString());
        String[] testArgs = new String[argList.size()];
        argList.toArray(testArgs);

        doReturn(returnMe).when(calculateSettingsFromCliArgs).invoke(eq(testArgs));
        doReturn(testLocator).when(testCommandLine).getOptionValue(SettingType.LookupLocators.getOptionWord());
        doReturn(testSpaceNames).when(testCommandLine).getOptionValue(SettingType.SpaceNames.getOptionCharacter());
        doReturn(testCommandLine).when(calculateSettingsFromCliArgs).parse(eq(testArgs));
        doReturn(0.5f).when(monitorDefaults).movingAverageAlpha();
        doReturn(OutputFormat.Csv).when(monitorDefaults).outputFormat();
        doReturn(Boolean.FALSE).when(xapDefaults).isSecured();

        Map<SettingType, String> actual = testInstance.invokeOrThrow(testArgs);

        Set<SettingType> set = new HashSet<>(asList(SettingType.values()));
        set.removeAll(required);
        set.removeAll(defaulted);
        for( SettingType setting : set ){
            String value = actual.get(setting);
            String expected = Constants.DEFAULT_FLAG_VALUE;
            assertEquals(String.format("Expected '%s' to be set to '%s'.", setting.name(), expected), expected, value);
        }

    }

    @Test
    public void testProcessOutputFileCreatesNewFileWhenNoOutputFileIsProvidedByCLI() throws Exception {

        String filePath = strings.alphabetic();

        doReturn(null).when(testCommandLine).getOptionValue(eq(SettingType.OutputFile.getOptionCharacter()));
        doReturn(null).when(testCommandLine).getOptionValue(eq(SettingType.OutputFile.getOptionWord()));
        doReturn(filePath).when(monitorDefaults).outputFilename();
        doReturn(filePath).when(validateAndCreateFilePath).invoke(filePath);

        String actual = testInstance.processOutputFile(testCommandLine);

        verify(validateAndCreateFilePath).invoke(eq(filePath));
        assertEquals(filePath, actual);

    }

    @Test(expected = ParseException.class)
    public void testProcessUsernameAndPasswordThrowsWhenNoUname() throws Exception {

        SettingType setting = SettingType.Password;
        String testPw = strings.alphabetic();
        doReturn(testPw).when(testCommandLine).getOptionValue(eq(setting.getOptionCharacter()));
        doReturn(testPw).when(testCommandLine).getOptionValue(eq(setting.getOptionWord()));

        testInstance.processUsernameAndPassword(testCommandLine, EnumSet.of(setting), Collections.<SettingType, String>emptyMap());

    }

    @Test(expected = ParseException.class)
    public void testProcessUsernameAndPasswordThrowsWhenNoPassword() throws Exception {

        SettingType setting = SettingType.Username;
        String testName = strings.alphabetic();

        doReturn(testName).when(testCommandLine).getOptionValue(eq(setting.getOptionCharacter()));
        doReturn(testName).when(testCommandLine).getOptionValue(eq(setting.getOptionWord()));

        testInstance.processUsernameAndPassword(testCommandLine, EnumSet.of(setting), Collections.<SettingType, String>emptyMap());


    }

    @Test
    public void testProcessUsernameAndPassword() throws Exception{

        String testName = strings.alphabetic();
        String testPw = strings.alphabetic();

        doReturn(testName).when(testCommandLine).getOptionValue(eq(SettingType.Username.getOptionCharacter()));
        doReturn(testName).when(testCommandLine).getOptionValue(eq(SettingType.Username.getOptionWord()));
        doReturn(testPw).when(testCommandLine).getOptionValue(eq(SettingType.Password.getOptionCharacter()));
        doReturn(testPw).when(testCommandLine).getOptionValue(eq(SettingType.Password.getOptionWord()));

        String[] actual = testInstance.processUsernameAndPassword(testCommandLine, EnumSet.of(SettingType.Username, SettingType.Password), new HashMap<SettingType, String>());

        assertEquals(testName, actual[0]);
        assertEquals(testPw, actual[1]);

    }

    @Test
    public void testProcessLocators() throws Exception {

        String testLocators = "google:123";

        doReturn(testLocators).when(testCommandLine).getOptionValue(eq(SettingType.LookupLocators.getOptionCharacter()));
        doReturn(testLocators).when(testCommandLine).getOptionValue(eq(SettingType.LookupLocators.getOptionWord()));

        String actual = testInstance.processLocators(testCommandLine, EnumSet.of(SettingType.LookupLocators));

        assertEquals(testLocators, actual);
    }

    @Test
    public void testProcessSpaceNames() throws Exception {

        String testSpaceNames = strings.alphabetic() + "," + strings.alphabetic();

        doReturn(testSpaceNames).when(testCommandLine).getOptionValue(eq(SettingType.SpaceNames.getOptionCharacter()));
        doReturn(testSpaceNames).when(testCommandLine).getOptionValue(eq(SettingType.SpaceNames.getOptionWord()));

        String actual = testInstance.processSpaceNames(testCommandLine, EnumSet.of(SettingType.SpaceNames));

        assertEquals(testSpaceNames, actual);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testProcessOutputFormatThrowsForInflux() throws Exception{

        doReturn(OutputFormat.InfluxDb).when(monitorDefaults).outputFormat();

        testInstance.processOutputFormat(Collections.<SettingType, String>emptyMap());

    }

    @Test(expected = UnsupportedOperationException.class)
    public void testProcessOutputFormatThrowsForCarbon() throws Exception{

        doReturn(OutputFormat.Carbon).when(monitorDefaults).outputFormat();

        testInstance.processOutputFormat(Collections.<SettingType, String>emptyMap());

    }

    @Test
    public void testProcessOutputFormatWorksForLogFormat() throws Exception{

        doReturn(OutputFormat.LogFormat).when(monitorDefaults).outputFormat();

        Map<SettingType, String> actual
                = testInstance.processOutputFormat(new HashMap<SettingType, String>());

        assertNotNull(actual.containsKey(SettingType.LogFormat));

    }

    @Test
    public void testProcessOutputFormatWorksForCsv() throws Exception{

        doReturn(OutputFormat.Csv).when(monitorDefaults).outputFormat();

        Map<SettingType, String> actual
                = testInstance.processOutputFormat(new HashMap<SettingType, String>());

        assertNotNull(actual.containsKey(SettingType.LogFormat));

    }

    @Test(expected = ParseException.class)
    public void testProcessLocatorsThrowsWhenNoLocatorsInSet() throws Exception{

        testInstance.processLocators(mock(CommandLine.class), new HashSet<SettingType>());

    }

    @Test(expected = ParseException.class)
    public void testProcessLocatorsThrowsWhenLocatorsAreSetToEmptyString() throws Exception{
        CommandLine cmdLine = mock(CommandLine.class);
        doReturn("").when(cmdLine).getOptionValue(SettingType.LookupLocators.getOptionCharacter());
        testInstance.processLocators(cmdLine, new HashSet<SettingType>() {{
            add(SettingType.LookupLocators);
        }});
    }

    @Test(expected = ParseException.class)
    public void testProcessLocatorsThrowsWhenNoSpacenamesInSet() throws Exception{

        testInstance.processSpaceNames(mock(CommandLine.class), new HashSet<SettingType>());

    }

    @Test(expected = ParseException.class)
    public void testProcessLocatorsThrowsWhenSpaceNamesAreSetToEmptyString() throws Exception{
        CommandLine cmdLine = mock(CommandLine.class);
        doReturn("").when(cmdLine).getOptionValue(SettingType.SpaceNames.getOptionCharacter());
        testInstance.processSpaceNames(cmdLine, new HashSet<SettingType>() {{
            add(SettingType.SpaceNames);
        }});
    }

}