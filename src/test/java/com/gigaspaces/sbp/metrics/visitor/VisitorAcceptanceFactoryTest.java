package com.gigaspaces.sbp.metrics.visitor;

import com.gigaspaces.sbp.metrics.metric.MetricsRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;


@RunWith(MockitoJUnitRunner.class)
public class VisitorAcceptanceFactoryTest {

    private VisitorAcceptanceFactory testInstance;
    @Mock
    private VisitorFactory visitorFactory;
    @Mock
    private MetricsRegistry metricsRegistry;

    @Before
    public void setUp() throws Exception {

        testInstance = new VisitorAcceptanceFactory(visitorFactory, metricsRegistry);
    }

    @Test
    public void testBuild() throws Exception {
        assertNotNull(testInstance.build());
    }

}