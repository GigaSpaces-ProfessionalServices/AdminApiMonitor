package com.gigaspaces.sbp.metrics.cli;

import org.apache.commons.cli.ParseException;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 7/29/14
 * Time: 8:45 PM
 */
public final class ProcessArgsTest {

    // ONE OF EACH OF THESE IS REQUIRED...
    private static final String LOCATORS = String.format("-%s", Settings.LookupLocators.getOptionCharacter());
    private static final String LOOKUP_GROUPS = String.format("-%s", Settings.LookupGroups.getOptionCharacter());
    private static final String SPACES = String.format("-%s", Settings.SpaceNames.getOptionCharacter());
    // (possibly as a word)...
    private static final String LOCATORS_WORD = String.format("-%s", Settings.LookupLocators.getOptionWord());
    private static final String LOOKUP_GROUPS_WORD = String.format("-%s", Settings.LookupGroups.getOptionWord());
    private static final String SPACES_WORD = String.format("-%s", Settings.SpaceNames.getOptionWord());
    // with an argument something like one of these...
    private static final String GOOD_LOCATOR_ARG = "google.com:4174";
    private static final String GOOD_GROUP_VAL = "monsters";
    private static final String GOOD_SPACE_NAMES = "space1,space3";
    private static final String GOOD_FILENAME = "robots.txt";

    private static final String[] MIN_GOOD_CMDLINE = new String[]{
            LOCATORS, GOOD_LOCATOR_ARG,
            SPACES_WORD, GOOD_SPACE_NAMES
    };

    // OPTIONAL COMMANDS
    private static final String OUTPUT = String.format("-%s", Settings.OutputFile.getOptionCharacter());
    private static final String OUTPUT_WORD = String.format("-%s", Settings.OutputFile.getOptionWord());
    private static final String USERNAME = String.format("-%s", Settings.Username.getOptionCharacter());
    private static final String PASSWORD = String.format("-%s", Settings.Password.getOptionCharacter());
    private static final String USERNAME_WORD = String.format("-%s", Settings.Username.getOptionWord());
    private static final String PASSWORD_WORD = String.format("-%s", Settings.Password.getOptionWord());

    private static final String GOOD_USERNAME = "nerothin";
    private static final String GOOD_PASSWORD = "secret";

    private static final String CSV_OPTION = String.format("-%s", Settings.Csv.getOptionCharacter());
    private static final String CSV_OPTION_WORD = String.format("-%s", Settings.Csv.getOptionWord());
    private static final String SECURED_OPTION = String.format("-%s", Settings.Secured.getOptionCharacter());
    private static final String SECURED_OPTION_WORD = String.format("-%s", Settings.Secured.getOptionWord());
    private static final String LOG_FORMAT_OPTION = String.format("-%s", Settings.LogFormat.getOptionCharacter());
    private static final String LOG_FORMAT_OPTION_WORD = String.format("-%s", Settings.LogFormat.getOptionWord());

    private String[] makeTestCommandLine(String[] moreCommands){
        if( moreCommands == null || moreCommands.length == 0 ) return MIN_GOOD_CMDLINE;
        String[] arr = new String[MIN_GOOD_CMDLINE.length + moreCommands.length];
        int i=0;
        for(String s : MIN_GOOD_CMDLINE) arr[i++] = s;
        for(String c: moreCommands) arr[i++] = c;
        return arr;
    }

    private ProcessArgs testInstance;

    @Before
    public void setUp() throws Exception {
        testInstance = new ProcessArgs();
    }

    @Test
    public void testInvokeSingleOptions() throws Exception {

        Set<Settings> actual = testInstance.invoke(makeTestCommandLine(new String[]{CSV_OPTION}));
        assertTrue(actual.contains(Settings.Csv));

        actual = testInstance.invoke(makeTestCommandLine(new String[]{SECURED_OPTION}));
        assertTrue(actual.contains(Settings.Secured));

        actual = testInstance.invoke(makeTestCommandLine(new String[]{LOG_FORMAT_OPTION}));
        assertTrue(actual.contains(Settings.LogFormat));

    }

    @Test
    public void testSingleParamsWithCharacter() throws Exception {

        Set<Settings> actual = testInstance.invoke(makeTestCommandLine(new String[]{LOCATORS, GOOD_LOCATOR_ARG}));
        assertTrue(actual.contains(Settings.LookupLocators));

        actual = testInstance.invoke(makeTestCommandLine(new String[]{LOOKUP_GROUPS, GOOD_GROUP_VAL}));
        assertTrue(actual.contains(Settings.LookupGroups));

        actual = testInstance.invoke(makeTestCommandLine(new String[]{SPACES, GOOD_SPACE_NAMES}));
        assertTrue(actual.contains(Settings.SpaceNames));

        actual = testInstance.invoke(makeTestCommandLine(new String[]{OUTPUT, GOOD_FILENAME}));
        assertTrue(actual.contains(Settings.OutputFile));

    }

    @Test
    public void testUsernameAndPasswordTogetherWithCharacter() throws Exception{

        Set<Settings> actual = testInstance.invoke(makeTestCommandLine(new String[]{USERNAME, GOOD_USERNAME, PASSWORD, GOOD_PASSWORD}));
        assertTrue(actual.contains(Settings.Username));
        assertTrue(actual.contains(Settings.Password));

    }

    @Test
    public void testUsernameAndPasswordTogetherWithWord() throws Exception{

        Set<Settings> actual = testInstance.invoke(makeTestCommandLine(new String[]{USERNAME_WORD, GOOD_USERNAME, PASSWORD_WORD, GOOD_PASSWORD}));
        assertTrue(actual.contains(Settings.Username));
        assertTrue(actual.contains(Settings.Password));

    }

    @Test(expected = ParseException.class)
    public void testUsernameRequiresArgument() throws ParseException {
        testInstance.invoke(makeTestCommandLine(new String[]{USERNAME, PASSWORD_WORD, GOOD_PASSWORD}));
    }

    @Test(expected = ParseException.class)
    public void testPasswordRequiresArgument() throws ParseException {
        testInstance.invoke(makeTestCommandLine(new String[]{PASSWORD, USERNAME_WORD, GOOD_USERNAME}));
    }

    @Test(expected = ParseException.class)
    public void testPasswordRequiresUsername() throws Exception{
        testInstance.invoke(makeTestCommandLine(new String[]{PASSWORD, GOOD_PASSWORD}));
    }

    @Test(expected = ParseException.class)
    public void testUsernameRequiresPassword() throws Exception{
        testInstance.invoke(makeTestCommandLine(new String[]{USERNAME, GOOD_USERNAME}));
    }

    @Test(expected = ParseException.class)
    public void testLookupLocatorsRequired() throws Exception{

        final String[] testCmdLine = new String[]{
                SPACES_WORD, GOOD_SPACE_NAMES
        };

        testInstance.invoke(testCmdLine);
    }

    @Test(expected = ParseException.class)
    public void testSpaceNamesRequired() throws Exception{

        final String[] testCmdLine = new String[]{
                LOCATORS, GOOD_LOCATOR_ARG
        };

        testInstance.invoke(testCmdLine);
    }

    @Test
    public void testInvokeSingleOptionsWithWords() throws Exception {

        Set<Settings> actual = testInstance.invoke(makeTestCommandLine(new String[]{CSV_OPTION_WORD}));
        assertTrue(actual.contains(Settings.Csv));

        actual = testInstance.invoke(makeTestCommandLine(new String[]{SECURED_OPTION_WORD}));
        assertTrue(actual.contains(Settings.Secured));

        actual = testInstance.invoke(makeTestCommandLine(new String[]{LOG_FORMAT_OPTION_WORD}));
        assertTrue(actual.contains(Settings.LogFormat));

    }

    @Test
    public void testParamsWithWords() throws Exception {

        Set<Settings> actual = testInstance.invoke(makeTestCommandLine(new String[]{LOCATORS_WORD, GOOD_LOCATOR_ARG}));
        assertTrue(actual.contains(Settings.LookupLocators));

        actual = testInstance.invoke(makeTestCommandLine(new String[]{LOOKUP_GROUPS_WORD, GOOD_GROUP_VAL}));
        assertTrue(actual.contains(Settings.LookupGroups));

        actual = testInstance.invoke(makeTestCommandLine(new String[]{SPACES_WORD, GOOD_SPACE_NAMES}));
        assertTrue(actual.contains(Settings.SpaceNames));

        actual = testInstance.invoke(makeTestCommandLine(new String[]{OUTPUT_WORD, GOOD_FILENAME}));
        assertTrue(actual.contains(Settings.OutputFile));

    }

    @Test
    public void testInvokeMultipleOptions() throws Exception {

        Set<Settings> actual = testInstance.invoke(makeTestCommandLine(new String[]{SECURED_OPTION, CSV_OPTION}));
        assertTrue(actual.contains(Settings.Secured));
        assertTrue(actual.contains(Settings.Csv));

    }

    @Test(expected = ParseException.class)
    public void testInvokeThrowsWithCsvAndLogFormat0() throws Exception {
        testInstance.invoke(makeTestCommandLine(new String[]{CSV_OPTION, LOG_FORMAT_OPTION}));
    }

    @Test(expected = ParseException.class)
    public void testInvokeThrowsWithCsvAndLogFormat1() throws Exception {
        testInstance.invoke(makeTestCommandLine(new String[]{CSV_OPTION_WORD, LOG_FORMAT_OPTION}));
    }

    @Test(expected = ParseException.class)
    public void testInvokeThrowsWithCsvAndLogFormat2() throws Exception {
        testInstance.invoke(makeTestCommandLine(new String[]{CSV_OPTION, LOG_FORMAT_OPTION_WORD}));
    }

    @Test(expected = ParseException.class)
    public void testInvokeThrowsWithCsvAndLogFormat3() throws Exception {
        testInstance.invoke(makeTestCommandLine(new String[]{CSV_OPTION_WORD, LOG_FORMAT_OPTION_WORD}));
    }

    @Test
    public void testInvokeRequiresHyphens() throws Exception {

        Set<Settings> actual = testInstance.invoke(makeTestCommandLine(new String[]{Settings.Secured.getOptionCharacter()}));
        assertNotNull(actual);

        assertEquals(actual.size(), 2, 0);
    }

    @Test(expected = ParseException.class)
    public void testInvokeThrowsForBadOptions() throws Exception {
        testInstance.invoke(makeTestCommandLine(new String[]{"-xyz", "-pdq"}));
    }

}