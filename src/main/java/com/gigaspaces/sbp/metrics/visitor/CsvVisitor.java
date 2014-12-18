package com.gigaspaces.sbp.metrics.visitor;

import com.gigaspaces.sbp.metrics.Constants;
import com.gigaspaces.sbp.metrics.ExponentialMovingAverage;
import com.gigaspaces.sbp.metrics.metric.FullMetric;
import com.gigaspaces.sbp.metrics.metric.MetricsRegistry;
import com.gigaspaces.sbp.metrics.metric.NamedMetric;
import org.openspaces.admin.Admin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CsvVisitor extends AbstractStatsVisitor{

    private Logger logger = LoggerFactory.getLogger("file");

    private Set<NamedMetric> savedOnce = new HashSet<>();

    private boolean saveHeaders = false;

    public CsvVisitor(
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
         prepareMetric(fullMetric);
    }

    @Override
    public boolean isSavedOnce(NamedMetric metric) {
        return savedOnce.contains(metric);
    }

    @Override
    public void saveOnce(FullMetric fullMetric) {
        savedOnce.add(fullMetric.getMetric());
        saveStat(fullMetric);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void printCsvMetrics(){
        if (saveHeaders){
            StringBuilder headers = new StringBuilder("timestamp, ");
            for (String metric : metricsRegistry.getPidMetrics().keySet()){
                headers.append(metric);
                appendHostName(headers, metric);
                headers.append(", ");
            }
            logger.info(headers.toString().substring(0, headers.length()-2));
            saveHeaders = false;
        }
        StringBuilder values = new StringBuilder(Constants.DATE_FORMAT).append(" ").append(dateFormat.format(new Date())).append(", ");
        for (FullMetric metric : metricsRegistry.getPidMetrics().values()){
            values.append(metric.getMetricValue()).append(", ");
        }
        logger.info(values.toString().substring(0, values.length() - 2));
    }

    private void appendHostName(StringBuilder headers, String metric) {
        String hostName = metricsRegistry.getPidMetrics().get(metric).getHostName();
        if (hostName != null){
            headers.append("_").append(hostName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSaveHeaders(Boolean saveHeaders) {
        this.saveHeaders = saveHeaders;
    }

}
