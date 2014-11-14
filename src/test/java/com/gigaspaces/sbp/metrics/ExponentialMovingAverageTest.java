package com.gigaspaces.sbp.metrics;

import com.gigaspaces.sbp.metrics.bootstrap.GsMonitorSettings;
import com.jasonnerothin.testing.Numbers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class ExponentialMovingAverageTest {

    private static final Float TEST_EMA_ALPHA_VALUE = 0.5f;
    private static final Double EPSILON = 0.001d;

    @Mock
    private GsMonitorSettings gsMonitorSettings;

    private ExponentialMovingAverage testInstance;
    private Numbers numbers;

    @Before
    public void setup(){

        numbers = new Numbers();
        testInstance = new ExponentialMovingAverage(gsMonitorSettings);

        doReturn(TEST_EMA_ALPHA_VALUE).when(gsMonitorSettings).emaAlpha();
    }

    @Test
    public void testAverage() throws Exception {

        double testOld = numbers.positiveDouble();
        double testNew = numbers.positiveDouble();

        double expected = testOld + TEST_EMA_ALPHA_VALUE * (testNew - testOld);

        double actual = testInstance.average(testOld,testNew);


        assertEquals(expected, actual, EPSILON);
    }

    @Test
    public void testAverage1() throws Exception {

        double testOld = numbers.positiveDouble();
        long testNew = numbers.positiveLong();

        double expected = testOld + TEST_EMA_ALPHA_VALUE * (testNew - testOld);
        double actual = testInstance.average(testOld, testNew);

        assertEquals(expected, actual, EPSILON);
    }

    @Test
    public void testAverage2() throws Exception {

        float testOld = numbers.positiveDouble().floatValue();
        int testNew = numbers.positiveInteger();

        double expected = testOld + TEST_EMA_ALPHA_VALUE * (testNew - testOld);
        double actual = testInstance.average(testOld, testNew);

        assertEquals(expected, actual, EPSILON);
    }
}