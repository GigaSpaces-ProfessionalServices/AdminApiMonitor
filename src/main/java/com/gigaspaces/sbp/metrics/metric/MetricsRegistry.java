package com.gigaspaces.sbp.metrics.metric;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 12/16/14
 * Time: 12:31 PM
 * Moves a bunch of shared metric state into a common class.
 */
@Component
public class MetricsRegistry {

    private Collection<? extends NamedMetric> metrics = new ArrayList<>();

    /**
     * {@link com.gigaspaces.sbp.metrics.visitor.VisitorAcceptance} calls me
     */
    public Collection<? extends NamedMetric> getMetrics() {
        return metrics;
    }

}