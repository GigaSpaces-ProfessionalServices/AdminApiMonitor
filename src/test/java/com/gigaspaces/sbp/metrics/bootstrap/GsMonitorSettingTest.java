package com.gigaspaces.sbp.metrics.bootstrap;

import com.gigaspaces.sbp.metrics.Constants;
import com.gigaspaces.sbp.metrics.bootstrap.cli.OutputFormat;
import com.gigaspaces.sbp.metrics.bootstrap.cli.SettingType;
import com.jasonnerothin.testing.Numbers;
import com.jasonnerothin.testing.Strings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class GsMonitorSettingTest {

    private GsMonitorSettingsImpl testInstance;
    private Strings strings = new Strings();
    private Numbers numbers = new Numbers();

    @Before
    public void setUp() throws Exception {
        testInstance = new GsMonitorSettingsImpl();
    }

    @Test(expected = IllegalStateException.class)
    public void testOutputFilePathThrowsWithoutInitialization() throws Exception {
        testInstance.outputFilePath();
    }

    @Test
    public void testOutputFilePath() throws Exception {

        final String testFilename = strings.alphabetic();

        HashMap<SettingType, String> map = new HashMap<SettingType, String>() {{
            put(SettingType.OutputFile, testFilename);
        }};
        testInstance.initialize(map);

        assertEquals(testFilename, testInstance.outputFilePath());
    }

    @Test(expected = IllegalStateException.class)
    public void testOutputFilePathThrowsWhenNotSpecificallyInitialized() throws Exception {

        testInstance.initialize(new HashMap<SettingType, String>() {{
            put(SettingType.Csv, Constants.DEFAULT_FLAG_VALUE);
        }});

        testInstance.outputFilePath();

    }

    @Test(expected = IllegalStateException.class)
    public void testOutputFormatThrowsWhenNotInitializedAtAll() {

        testInstance.outputFormat();

    }

    @Test
    public void testOutputFormatCsv() {

        testInstance.initialize(new HashMap<SettingType, String>() {{
            put(SettingType.Csv, Constants.DEFAULT_FLAG_VALUE);
        }});

        OutputFormat actual = testInstance.outputFormat();

        assertEquals(OutputFormat.Csv, actual);
    }

    @Test
    public void testOutputFormatLogFile() {

        testInstance.initialize(new HashMap<SettingType, String>() {{
            put(SettingType.LogFormat, Constants.DEFAULT_FLAG_VALUE);
        }});

        OutputFormat actual = testInstance.outputFormat();

        assertEquals(OutputFormat.LogFormat, actual);

    }

    @Test(expected = IllegalStateException.class)
    public void testOutputFormatThrowsIfNeitherCsvNorLogFormatHaveBeenSet() {

        testInstance.initialize(new HashMap<SettingType, String>() {{
            put(SettingType.Secured, Constants.DEFAULT_FLAG_VALUE);
        }});

        testInstance.outputFormat();

    }

    @Test
    public void testUsername() {

        final String testUsername = strings.alphabetic();
        testInstance.initialize(new HashMap<SettingType, String>() {{
            put(SettingType.Username, testUsername);
        }});

        assertEquals(testUsername, testInstance.username());

    }

    @Test(expected = IllegalStateException.class)
    public void testUsernameThrowsIfNotInitialized() {

        testInstance.username();

    }

    @Test
    public void testUsernameIsOptional() {

        testInstance.initialize(new HashMap<SettingType, String>() {{
            put(SettingType.LookupGroups, strings.alphabetic());
        }});

        assertNull(testInstance.username());

    }

    @Test
    public void testPassword() {

        final String testPassword = strings.alphabetic();
        testInstance.initialize(new HashMap<SettingType, String>() {{
            put(SettingType.Password, testPassword);
        }});

        assertEquals(testPassword, testInstance.password());
    }

    @Test(expected = IllegalStateException.class)
    public void testPasswordThrowsIfNotInitializedAtAll() {

        testInstance.password();

    }

    @Test
    public void testPasswordIsOptional() {

        testInstance.initialize(new HashMap<SettingType, String>() {{
            put(SettingType.Csv, Constants.DEFAULT_FLAG_VALUE);
        }});

        assertNull(testInstance.password());
    }

    @Test
    public void testLookupLocators() throws Exception {

        final String testLocators = strings.alphabetic() + ":" + numbers.positiveInteger();

        testInstance.initialize(new HashMap<SettingType, String>() {{
            put(SettingType.LookupLocators, testLocators);
        }});

        assertEquals(testLocators, testInstance.lookupLocators());

    }

    @Test(expected = IllegalStateException.class)
    public void testLookupLocatorsThrowsIfNotInitializedAtAll() {

        testInstance.lookupLocators();

    }

    @Test(expected = IllegalStateException.class)
    public void testLookupLocatorsThrowsIfNotInitializedCorrectly() {

        testInstance.initialize(new HashMap<SettingType, String>() {{
            put(SettingType.Password, strings.alphabetic());
        }});

        testInstance.lookupLocators();

    }

    @Test
    public void testLookupGroups() {

        final String testGroups = strings.alphabetic() + "," + strings.alphabetic();
        testInstance.initialize(new HashMap<SettingType, String>() {{
            put(SettingType.LookupGroups, testGroups);
        }});

        assertEquals(testGroups, testInstance.lookupGroups());
    }

    @Test(expected = IllegalStateException.class)
    public void testLookupGroupsThrowsIfNotInitializedAtAll() {

        testInstance.lookupGroups();

    }

    @Test
    public void testLookupGroupsIsOptional() {

        testInstance.initialize(new HashMap<SettingType, String>() {{
            put(SettingType.MovingAverageAlpha, "0.1");
        }});

        assertNull(testInstance.lookupGroups());

    }

    @Test(expected = IllegalStateException.class)
    public void testSpaceNamesThrowsWhenNotInitializedAtAll() {

        testInstance.spaceNames();

    }

    @Test(expected = IllegalStateException.class)
    public void testSpaceNamesThrowsWhenNotSpecificallyInitialized() {

        testInstance.initialize(new HashMap<SettingType, String>() {{
            put(SettingType.OutputFile, strings.alphabetic() + ".txt");
        }});

        testInstance.spaceNames();

    }

    @Test
    public void testSpaceNamesOneName() {

        final String name = strings.alphabetic();

        testInstance.initialize(new HashMap<SettingType, String>() {{
            put(SettingType.SpaceNames, name);
        }});

        assertEquals(name, testInstance.spaceNames()[0]);

    }

    @Test
    public void testSpaceNamesTwoNames() {

        String name1 = strings.alphabetic();
        String name2 = strings.alphabetic();
        final String names = name1 + Constants.LIST_ITEM_SEPARATOR + name2;

        testInstance.initialize(new HashMap<SettingType, String>() {{
            put(SettingType.SpaceNames, names);
        }});

        String[] actual = testInstance.spaceNames();

        assertEquals(name1, actual[0]);
        assertEquals(name2, actual[1]);

    }

    @Test(expected = IllegalStateException.class)
    public void testAlertsEnabledThrowsWhenNotInitializedAtAll() {

        testInstance.alertsEnabled();

    }

    @Test
    public void testAlertsEnabled() {

        testInstance.initialize(new HashMap<SettingType, String>() {{
            put(SettingType.AlertsEnabled, Constants.DEFAULT_FLAG_VALUE);
        }});

        assertTrue(testInstance.alertsEnabled());

    }

    @Test(expected = IllegalStateException.class)
    public void testAlertsEnabledThrowsIfNotSpecificallyEnabled() {

        testInstance.initialize(new HashMap<SettingType, String>() {{
            put(SettingType.LookupLocators, strings.alphabetic());
        }});

        testInstance.alertsEnabled();

    }

    @Test
    public void testSendAlertsByEmail() {

        testInstance.initialize(new HashMap<SettingType, String>() {{
            put(SettingType.SendAlertsByEmail, Constants.DEFAULT_FLAG_VALUE);
        }});

        assertTrue(testInstance.sendAlertsByEmail());

    }

    @Test(expected = IllegalStateException.class)
    public void testCollectMetricsIntervalInMsThrowsWhenNotInitializedAtAll() {

        testInstance.collectMetricsIntervalInMs();

    }

    @Test(expected = IllegalStateException.class)
    public void testCollectMetricsIntervalInMsThrowsWhenNotSpecificallyInitialized() {

        testInstance.initialize(new HashMap<SettingType, String>() {{
            put(SettingType.LookupLocators, strings.alphabetic());
        }});

        testInstance.collectMetricsIntervalInMs();

    }

    @Test
    public void testCollectMetricsIntervalInMs() {

        final Integer testInterval = numbers.positiveInteger();

        testInstance.initialize(new HashMap<SettingType, String>() {{
            put(SettingType.CollectMetricsIntervalInMs, testInterval.toString());
        }});

        assertEquals(testInterval.toString(), testInstance.collectMetricsIntervalInMs().toString());

    }

    @Test
    public void testCollectionMetricsInitialDelayInMs(){

        final Integer testDelay = numbers.positiveInteger();

        testInstance.initialize(new HashMap<SettingType, String>(){{
            put(SettingType.CollectMetricsInitialDelayInMs, testDelay.toString());
        }});

        assertEquals(testDelay.toString(), testInstance.collectMetricsInitialDelayInMs().toString());

    }

    @Test(expected = IllegalStateException.class)
    public void testCollectionMetricsInitialDelayInMsThrowsIfNotInitializedAtAll(){

        testInstance.collectMetricsInitialDelayInMs();

    }

    @Test(expected = IllegalStateException.class)
    public void testCollectionMetricInitialDelayInMsThrowsIfNotSpecificallyInitialized(){

        testInstance.initialize(new HashMap<SettingType, String>() {{
            put(SettingType.CollectMetricsIntervalInMs, numbers.positiveDouble().toString());
        }});

        testInstance.collectMetricsInitialDelayInMs();

    }

}