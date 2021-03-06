package com.gigaspaces.sbp.metrics.visitor;

import com.gigaspaces.sbp.metrics.ExponentialMovingAverage;
import com.gigaspaces.sbp.metrics.metric.FullMetric;
import com.gigaspaces.sbp.metrics.metric.MetricsRegistry;
import com.gigaspaces.sbp.metrics.metric.NamedMetric;
import org.openspaces.admin.Admin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PrintVisitor extends AbstractStatsVisitor {

    public static final String VALUE_SEPARATOR = " :: ";
    public static final String EMPTY_VALUE = " - ";
    private Logger logger = LoggerFactory.getLogger("file");

    private Set<NamedMetric> savedOnceMetrics = new HashSet<>();

    public PrintVisitor(
            Admin admin
            , List<String> spaceName
            , ExponentialMovingAverage average
            , Map<String, AtomicInteger> alerts
            , Long derivedMetricsPeriodInMs
            , MetricsRegistry metricsRegistry){
        super(admin, spaceName, average, alerts, derivedMetricsPeriodInMs, metricsRegistry);
    }

    @Override
    public void saveStat(FullMetric fullMetric) {
        List<FullMetric> fullMetrics = prepareMetric(fullMetric);
        for (FullMetric metric : fullMetrics){
            logger.info(formatMetrics(metric));
        }
    }

    private String formatMetrics(FullMetric fullMetric) {
        String spaceInstanceID = (fullMetric.getSpaceInstanceID() != null) ? fullMetric.getSpaceInstanceID() : EMPTY_VALUE;
        String gscPid = (fullMetric.getPid() != null && fullMetric.getPid() != 0l) ? fullMetric.getPid().toString() : EMPTY_VALUE;
        String hostName = (fullMetric.getHostName() != null) ? fullMetric.getHostName() : EMPTY_VALUE;
        String spaceMode = (fullMetric.getSpaceMode() != null) ? fullMetric.getSpaceMode() : EMPTY_VALUE;
        String gridComponent = (fullMetric.getGridComponentName() != null) ? fullMetric.getGridComponentName() : EMPTY_VALUE;
        return "\n" + dateFormat.format(new Date()) + VALUE_SEPARATOR +  hostName + VALUE_SEPARATOR + gridComponent + VALUE_SEPARATOR + spaceInstanceID + VALUE_SEPARATOR + gscPid +
                VALUE_SEPARATOR + fullMetric.getMetricFullName() + VALUE_SEPARATOR + fullMetric.getMetricValue() + VALUE_SEPARATOR + spaceMode;
    }

    @Override
    public boolean isSavedOnce(NamedMetric metric) {
        return savedOnceMetrics.contains(metric);
    }

    @Override
    public void saveOnce(FullMetric fullMetric) {
        savedOnceMetrics.add(fullMetric.getMetric());
        logger.info(formatMetrics(fullMetric));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSaveHeaders(Boolean saveHeaders) {
        // TODO code smell: refused bequest
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void printCsvMetrics() {
        // TODO code smell: refused bequest
    }

}
