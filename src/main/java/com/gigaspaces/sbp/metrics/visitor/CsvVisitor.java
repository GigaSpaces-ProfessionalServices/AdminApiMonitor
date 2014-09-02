package com.gigaspaces.sbp.metrics.visitor;

import com.gigaspaces.sbp.metrics.ExponentialMovingAverage;
import com.gigaspaces.sbp.metrics.FullMetric;
import com.gigaspaces.sbp.metrics.metric.NamedMetric;
import org.openspaces.admin.Admin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CsvVisitor extends AbstractStatsVisitor{

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Set<NamedMetric> savedOnce = new HashSet<>();

    private boolean saveHeaders = false;

    public CsvVisitor(Admin admin, List<String> spaceName, Map<String, FullMetric> pidMetricMap, ExponentialMovingAverage average, Long period){
        super(admin, spaceName, pidMetricMap, average, period);
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
            StringBuilder headers = new StringBuilder();
            for (String metric : metricMap.keySet()){
                headers.append(metric).append(", ");
            }
            logger.info(headers.toString().substring(0, headers.length()-2));
        }
        StringBuilder values = new StringBuilder();
        for (FullMetric metric : metricMap.values()){
            values.append(metric.getMetricValue()).append(", ");
        }
        logger.info(values.toString().substring(0, values.length()-2));
    }

    public void setSaveHeaders(boolean saveHeaders) {
        this.saveHeaders = saveHeaders;
    }
}
