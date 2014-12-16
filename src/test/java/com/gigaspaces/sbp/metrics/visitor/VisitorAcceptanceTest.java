package com.gigaspaces.sbp.metrics.visitor;

import com.gigaspaces.sbp.metrics.metric.*;
import com.jasonnerothin.testing.Numbers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class VisitorAcceptanceTest {

    @Mock
    private VisitorFactory visitorFactory;
    @Mock
    private StatsVisitor statsVisitor;
    @Mock
    private MetricsRegistry metricsRegistry;

    private VisitorAcceptance testInstance;

    private Numbers numbers;

    @Before
    public void setUp() throws Exception {

        doReturn(statsVisitor).when(visitorFactory).build();

        testInstance = new VisitorAcceptance(visitorFactory, metricsRegistry);

        numbers = new Numbers();

    }

    @Test
    public void testRunSavesHeadersIfNotSaved() throws Exception {

        testInstance.run();

        verify(statsVisitor).setSaveHeaders(eq(Boolean.TRUE));

    }

    @Test
    public void testRunDoesNotSaveHeadersIfTheyAreAlreadySaved() throws Exception {

        Class<?> clazz = VisitorAcceptance.class;
        Field headersSavedField = clazz.getDeclaredField("headersSaved");
        headersSavedField.setAccessible(true);
        headersSavedField.set(testInstance, Boolean.TRUE);

        testInstance.run();

        verify(statsVisitor, never()).setSaveHeaders(anyBoolean());

    }

    @Test
    public void testRunCallsPrintCsvMetrics() throws Exception{

        testInstance.run();

        verify(statsVisitor).printCsvMetrics();

    }

    @Test
    public void testRunCallsAcceptForAllRegisteredMetrics() throws Exception{
        int len = numbers.positiveInteger() % 5 + 1;

        List<NamedMetric> list = new ArrayList<>();
        for( int i=0; i<len; i++){
            list.add(mock(NamedMetric.class));
        }

        doReturn(list).when(metricsRegistry).getMetrics();

        testInstance.run();

        for( NamedMetric metric : list )
            verify(metric).accept(same(statsVisitor));

    }

    @Test
    public void testInitVisitorIfNecessary() throws Exception {

        testInstance.initVisitorIfNecessary();

        verify(visitorFactory).build();
    }
}