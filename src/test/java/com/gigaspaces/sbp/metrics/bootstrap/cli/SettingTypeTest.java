package com.gigaspaces.sbp.metrics.bootstrap.cli;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.junit.Assert.*;

public class SettingTypeTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testGetOptionCharacter() throws Exception {
        assertEquals('e', SettingType.AlertsEnabled.getOptionCharacter().charAt(0));
    }

    @Test
    public void testGetOptionWord() throws Exception {
        assertEquals("metrics-interval", SettingType.CollectMetricsIntervalInMs.getOptionWord());
    }

    @Test
    public void testGetOptionDescription() throws Exception {
        for(SettingType type : SettingType.values() ){
            String desc = type.getOptionDescription();
            assertNotNull(desc);
            assertTrue(desc.trim().length() > 0);
        }

    }

    @Test
    public void testHasArgument() throws Exception {
        assertFalse(SettingType.AlertsEnabled.hasArgument());
    }

    @Test
    public void testIsRequired() throws Exception {
        assertTrue(SettingType.LookupLocators.isRequired());
    }

    @Test
    public void testHasDefault() throws Exception {
        assertTrue(SettingType.LookupGroups.hasDefault());
    }

    @Test
    public void testIsUsedByCli() throws Exception{
        assertFalse(SettingType.MovingAverageAlpha.isUsedByCli());
    }

    @Test
    public void testCommandCharsAreUnique() throws Exception{

        Set<String> set = new TreeSet<>();
        List<String> list = new LinkedList<>();

        for( SettingType type : SettingType.values() ){
            set.add(type.getOptionCharacter());
            list.add(type.getOptionCharacter());
            Collections.sort(list);
        }
//        System.out.println(strings);
//        System.out.println(list);

        assertEquals(list.size(), set.size(), 0);

    }

    @Test
    public void testCommandWordsAreUnique() throws Exception{

        Set<String> set = new TreeSet<>();
        List<String> list = new LinkedList<>();

        for( SettingType type : SettingType.values() ){
            String word = type.getOptionWord();
            set.add(word);
            list.add(word);
            Collections.sort(list);
        }
//        System.out.println(strings);
        logger.trace(list.toString());

        assertEquals(list.size(), set.size(), 0);

    }

}