package com.gigaspaces.sbp.metrics.metric;

import com.gigaspaces.sbp.metrics.MetricContext;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 8/4/14
 * Time: 5:24 PM
 */
public abstract class GigaSpaceMetric implements NamedMetric, MetricContext {

    private final GigaSpaceProcess gsProcess;

    public GigaSpaceMetric(GigaSpaceProcess gsProcess) {
        assert gsProcess != null : "need a process boundary for this metric.";
        this.gsProcess = gsProcess;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GigaSpaceProcess over() {
        return gsProcess;
    }

}