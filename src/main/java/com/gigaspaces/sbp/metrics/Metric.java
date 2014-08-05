package com.gigaspaces.sbp.metrics;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 8/4/14
 * Time: 5:24 PM
 */
public abstract class Metric implements Displayable, MetricContext {

    private final GigaSpaceProcess gsProcess;

    public Metric(GigaSpaceProcess gsProcess) {
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