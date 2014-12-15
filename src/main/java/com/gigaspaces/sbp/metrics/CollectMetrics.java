package com.gigaspaces.sbp.metrics;

import akka.actor.ActorSystem;
import com.gigaspaces.sbp.metrics.bootstrap.GsMonitorSettings;
import com.gigaspaces.sbp.metrics.visitor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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
    private static final String VISITOR_FACTORY_REQUIRED = "Need a source of statistics visitors.";

    @Resource
    private final ActorSystemEden actorSystemEden;
    @Resource
    private final GsMonitorSettings settings;
    @Resource
    private final VisitorFactory visitorFactory;

    private ActorSystem system;
    private StatsVisitor visitor;

    @Autowired
    public CollectMetrics(ActorSystemEden actorSystemEden,
                          GsMonitorSettings settings,
                          VisitorFactory visitorFactory) {
        assert actorSystemEden != null : ACTOR_SYSTEM_INIT_REQUIRED;
        assert settings != null : SETTINGS_REQUIRED;
        assert visitorFactory != null : VISITOR_FACTORY_REQUIRED;

        this.visitorFactory = visitorFactory;
        this.actorSystemEden = actorSystemEden;
        this.settings = settings;
        this.system = actorSystemEden.getSystem();
    }

    public void readySetGo() {
        visitor = visitorFactory.build();
    }

}
