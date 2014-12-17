package com.gigaspaces.sbp.metrics;

import akka.actor.ActorSystem;
import akka.actor.Scheduler;
import com.gigaspaces.sbp.metrics.bootstrap.GsMonitorSettings;
import com.gigaspaces.sbp.metrics.visitor.VisitorAcceptance;
import com.gigaspaces.sbp.metrics.visitor.VisitorAcceptanceFactory;
import com.gigaspaces.sbp.metrics.visitor.VisitorFactory;
import com.jasonnerothin.testing.Numbers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class CollectMetricsTest {

    private CollectMetrics testInstance;
    @Mock
    private ActorSystemEden eden;
    @Mock
    private ActorSystem actorSystem;
    @Mock
    private GsMonitorSettings settings;
    @Mock
    private VisitorFactory visitorFactory;
    @Mock
    private VisitorAcceptanceFactory visitorAcceptanceFactory;
    @Mock
    private VisitorAcceptance visitorAcceptance;
    @Mock
    private scala.concurrent.ExecutionContextExecutor dispatcher;
    @Mock
    private Scheduler scheduler;

    private Numbers numbers;

    @Before
    public void setUp() throws Exception {

        numbers = new Numbers();

        doReturn(actorSystem).when(eden).getSystem();
        doReturn(visitorAcceptance).when(visitorAcceptanceFactory).build();
        doReturn(numbers.positiveInteger()).when(settings).collectMetricsInitialDelayInMs();
        doReturn(numbers.positiveInteger()).when(settings).collectMetricsIntervalInMs();
        doReturn(dispatcher).when(actorSystem).dispatcher();
        doReturn(scheduler).when(actorSystem).scheduler();

        testInstance = new CollectMetrics(eden, settings, visitorFactory, visitorAcceptanceFactory);
    }

    @Test
    public void testReadySetGo() throws Exception {

        testInstance.readySetGo();


    }
}