package com.gigaspaces.sbp.metrics.visitor;

import com.gigaspaces.sbp.metrics.Constants;
import com.gigaspaces.sbp.metrics.metric.MetricsRegistry;
import com.gigaspaces.sbp.metrics.metric.NamedMetric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 12/15/14
 * Time: 12:02 PM
 */
@Component
public class VisitorAcceptance implements Runnable {

    @Resource
    private final VisitorFactory visitorFactory;
    @Resource
    private final MetricsRegistry metricsRegistry;

    private Boolean headersSaved = false;
    private StatsVisitor visitor;

    @Autowired
    public VisitorAcceptance(VisitorFactory visitorFactory, MetricsRegistry metricsRegistry) {
        assert visitorFactory != null : String.format(Constants.THING_REQUIRED, VisitorFactory.class.getSimpleName());
        assert metricsRegistry != null : String.format(Constants.THINGS_REQUIRED, MetricsRegistry.class.getSimpleName());
        this.visitorFactory = visitorFactory;
        this.metricsRegistry = metricsRegistry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        initVisitorIfNecessary();
        if (!headersSaved) {
            visitor.setSaveHeaders(true);
            headersSaved = true;
        }
        for (NamedMetric metric : metricsRegistry.getMetrics()) {
            metric.accept(visitor);
        }
        visitor.printCsvMetrics();
    }

    void initVisitorIfNecessary() {
        if (visitor == null) visitor = visitorFactory.build();
    }

}
