package com.gigaspaces.sbp.metrics.metric;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
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

    private Collection<NamedMetric> metrics = new ArrayList<NamedMetric>(){{
        addAll(Arrays.asList(GigaSpacesActivity.values()));
        addAll(Arrays.asList(GigaSpacesClusterInfo.values()));
        addAll(Arrays.asList(GsMirrorInfo.values()));
        addAll(Arrays.asList(JvmInfo.values()));
        addAll(Arrays.asList(Memory.values()));
        addAll(Arrays.asList(OperatingSystemInfo.values()));
        addAll(Arrays.asList(InstanceCount.values()));
        addAll(Arrays.asList(CacheContentMetric.values()));
        addAll(Arrays.asList(AlertsInfo.values()));
    }};

    /**
     * {@link com.gigaspaces.sbp.metrics.visitor.VisitorAcceptance} calls me
     */
    public Collection<? extends NamedMetric> getMetrics() {
        return new ArrayList<>(metrics);
    }

}