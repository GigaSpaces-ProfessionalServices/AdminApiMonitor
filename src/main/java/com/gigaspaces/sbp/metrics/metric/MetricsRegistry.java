package com.gigaspaces.sbp.metrics.metric;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.asList;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 12/16/14
 * Time: 12:31 PM
 * Moves a bunch of shared metric state into a common class.
 */
@Component
public class MetricsRegistry {

    private static final Collection<NamedMetric> METRICS = new ArrayList<NamedMetric>();
    {
        METRICS.addAll(Arrays.asList(GigaSpacesActivity.values()));
        METRICS.addAll(Arrays.asList(GigaSpacesClusterInfo.values()));
        METRICS.addAll(Arrays.asList(GsMirrorInfo.values()));
        METRICS.addAll(Arrays.asList(JvmInfo.values()));
        METRICS.addAll(Arrays.asList(Memory.values()));
        METRICS.addAll(Arrays.asList(OperatingSystemInfo.values()));
        METRICS.addAll(Arrays.asList(InstanceCount.values()));
        METRICS.addAll(Arrays.asList(CacheContentMetric.values()));
        METRICS.addAll(Arrays.asList(AlertsInfo.values()));
    };

    private static final List<NamedMetric> MOVING_AVERAGED_METRICS = asList(new NamedMetric[]{
            GigaSpacesActivity.READ_PER_SEC
            , GigaSpacesActivity.WRITES_PER_SEC
            , GigaSpacesActivity.TAKES_PER_SECOND
            , GigaSpacesActivity.UPDATES_PER_SEC
            , GigaSpacesActivity.EXECUTES_PER_SEC
            , GigaSpacesActivity.TRANSACTION_COUNT
            , GsMirrorInfo.REDO_LOG_SIZE
            , GsMirrorInfo.REDO_LOG_SEND_BYTES_PER_SECOND
            , JvmInfo.THREAD_COUNT
            //, JvmInfo.JVM_CPU_LOAD
            , Memory.TOTAL_BYTES
            , Memory.HEAP_USED_BYTES
            , Memory.HEAP_COMMITTED_BYTES
            , Memory.NON_HEAP_USED_BYTES
            , Memory.NON_HEAP_COMMITTED_BYTES
            , OperatingSystemInfo.LRMI_CONNECTIONS
    });

    /**
     * In some cases, we calculate a per_second value based off of the cumulative value.
     * We keep track of the relationship between cumulative and per_sec value here.
     */
    private static final Map<NamedMetric, NamedMetric> DERIVED_METRICS = new HashMap<NamedMetric, NamedMetric>();

    {
        DERIVED_METRICS.put(GigaSpacesActivity.WRITE_COUNT, GigaSpacesActivity.WRITES_PER_SEC);
        DERIVED_METRICS.put(GigaSpacesActivity.READ_COUNT, GigaSpacesActivity.READ_PER_SEC);
        DERIVED_METRICS.put(GigaSpacesActivity.UPDATE_COUNT, GigaSpacesActivity.UPDATES_PER_SEC);
        DERIVED_METRICS.put(GigaSpacesActivity.EXECUTE_COUNT, GigaSpacesActivity.EXECUTES_PER_SEC);
        DERIVED_METRICS.put(GigaSpacesActivity.TAKE_COUNT, GigaSpacesActivity.TAKES_PER_SECOND);
    }

    private final Map<String, FullMetric> pidMetrics = new LinkedHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> alerts = new ConcurrentHashMap<>();

    /**
     * {@link com.gigaspaces.sbp.metrics.visitor.VisitorAcceptance} calls me
     */
    public Collection<? extends NamedMetric> getMetrics() {
        return new ArrayList<>(METRICS);
    }

    public Map<String, FullMetric> getPidMetrics() {
        return pidMetrics;
    }

    public ConcurrentHashMap<String, AtomicInteger> getAlerts() {
        return alerts;
    }

    public Boolean isMovingAveraged(NamedMetric metric) {
        return MOVING_AVERAGED_METRICS.contains(metric);
    }

    public NamedMetric getDerivedMetric(NamedMetric metric) {
        return DERIVED_METRICS.get(metric);
    }

    public Boolean hasDerivativeMetric(NamedMetric namedMetric) {
        return getDerivedMetric(namedMetric) != null;
    }
}