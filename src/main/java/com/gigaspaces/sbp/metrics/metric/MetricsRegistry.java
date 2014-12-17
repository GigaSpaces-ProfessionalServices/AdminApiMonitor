package com.gigaspaces.sbp.metrics.metric;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 12/16/14
 * Time: 12:31 PM
 * Moves a bunch of shared metric state into a common class.
 */
@Component
public class MetricsRegistry {

    private static final Collection<NamedMetric> METRICS = new ArrayList<NamedMetric>(){{
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
        return new ArrayList<>(METRICS);
    }

    private final Map<String, FullMetric> pidMetricMap = new LinkedHashMap<>();

    private final ConcurrentHashMap<String, AtomicInteger> alerts = new ConcurrentHashMap<>();

    public Map<String, FullMetric> getPidMetrics() {
        return pidMetricMap;
    }

    public ConcurrentHashMap<String, AtomicInteger> getAlerts() {
        return alerts;
    }

}