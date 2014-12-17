package com.gigaspaces.sbp.metrics.visitor;

import com.gigaspaces.sbp.metrics.Constants;
import com.gigaspaces.Factory;
import com.gigaspaces.sbp.metrics.metric.MetricsRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 12/16/14
 * Time: 10:39 PM
 */
@Component
public class VisitorAcceptanceFactory implements Factory<VisitorAcceptance> {

    @Resource
    private final VisitorFactory visitorFactory;
    @Resource
    private final MetricsRegistry metricsRegistry;

    @Autowired
    public VisitorAcceptanceFactory(VisitorFactory visitorFactory, MetricsRegistry metricsRegistry) {
        assert visitorFactory != null : String.format(Constants.THING_REQUIRED, VisitorFactory.class.getSimpleName());
        assert metricsRegistry != null : String.format(Constants.THINGS_REQUIRED, MetricsRegistry.class.getSimpleName());
        this.visitorFactory = visitorFactory;
        this.metricsRegistry = metricsRegistry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VisitorAcceptance build(){
        return new VisitorAcceptance(visitorFactory, metricsRegistry);
    }

}