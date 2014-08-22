package com.gigaspaces.sbp.metrics;

import com.gigaspaces.sbp.metrics.metric.GigaSpaceProcess;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 8/5/14
 * Time: 4:16 PM
 */
public abstract class NowGigaSpaceMetricOnHost extends MetricOnHost {

    private final CollectionPeriod during;

    NowGigaSpaceMetricOnHost(GigaSpaceProcess gsProcess, Collection<String> hostNames){
        super(gsProcess, hostNames);
        this.during = new PointInTime();
    }

    /**
     * This field is nullable
     *
     * @return interval over which a metric applies - as for an average
     */
    @Override
    public CollectionPeriod during() {
        return during;
    }

}
