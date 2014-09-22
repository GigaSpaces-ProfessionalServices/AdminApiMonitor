package com.gigaspaces.sbp.metrics.visitor;

import com.gigaspaces.sbp.metrics.Constants;
import com.gigaspaces.sbp.metrics.ExponentialMovingAverage;
import com.gigaspaces.sbp.metrics.FullMetric;
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

    public CsvVisitor(Admin admin, List<String> spaceName, Map<String, FullMetric> pidMetricMap, ExponentialMovingAverage average, Map<String, AtomicInteger> alerts, Long period){
        super(admin, spaceName, pidMetricMap, average, alerts, period);
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

    public void printCsvMetrics(){
        if (saveHeaders){
            StringBuilder headers = new StringBuilder("timestamp, ");
            for (String metric : metricMap.keySet()){
                headers.append(metric).append(", ");
            }
            logger.info(headers.toString().substring(0, headers.length()-2));
        }
        StringBuilder values = new StringBuilder(dateFormat.format(new Date())).append(", ");
        for (FullMetric metric : metricMap.values()){
            values.append(metric.getMetricValue()).append(", ");
        }
        logger.info(values.toString().substring(0, values.length()-2));
    }

    public Map<String, FullMetric> getMetrics(){
        return metricMap;
    }

    public void setSaveHeaders(boolean saveHeaders) {
        this.saveHeaders = saveHeaders;
    }
}
