package com.gigaspaces.sbp.metrics;

import akka.actor.ActorSystem;
import com.gigaspaces.sbp.metrics.bootstrap.GsMonitorSettings;
import com.gigaspaces.sbp.metrics.visitor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scala.concurrent.duration.FiniteDuration;

import javax.annotation.Resource;
import javax.xml.datatype.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 11/12/14
 * Time: 6:37 PM
 */
@Component
public class CollectMetrics {

    private static final String ACTOR_SYSTEM_INIT_REQUIRED = "We need an ActorSystem provider.";
    private static final String SETTINGS_REQUIRED = "Settings are required.";

    @Resource
    private final GsMonitorSettings settings;
    @Resource
    private final VisitorAcceptanceFactory visitorAcceptanceFactory;

    private ActorSystem system;

    @Autowired
    public CollectMetrics(ActorSystemEden actorSystemEden,
                          GsMonitorSettings settings,
                          VisitorFactory visitorFactory,
                          VisitorAcceptanceFactory visitorAcceptanceFactory) {
        this.visitorAcceptanceFactory = visitorAcceptanceFactory;
        assert actorSystemEden != null : ACTOR_SYSTEM_INIT_REQUIRED;
        assert settings != null : SETTINGS_REQUIRED;

        this.settings = settings;
        this.system = actorSystemEden.getSystem();
    }

    public void readySetGo() {
        FiniteDuration delay = new FiniteDuration(settings.collectMetricsInitialDelayInMs(), TimeUnit.MILLISECONDS);
        FiniteDuration interval = new FiniteDuration(settings.collectMetricsIntervalInMs(), TimeUnit.MILLISECONDS);
        system.scheduler().schedule(delay, interval, visitorAcceptanceFactory.build(), system.dispatcher());
    }

}
