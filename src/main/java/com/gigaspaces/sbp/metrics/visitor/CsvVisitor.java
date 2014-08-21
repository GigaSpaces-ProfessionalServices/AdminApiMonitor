package com.gigaspaces.sbp.metrics.visitor;

import com.gigaspaces.sbp.metrics.ExponentialMovingAverage;
import com.gigaspaces.sbp.metrics.FullMetric;
import com.gigaspaces.sbp.metrics.NamedMetric;
import org.openspaces.admin.Admin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CsvVisitor extends AbstractStatsVisitor{

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, String> metrics = new LinkedHashMap<>();

    private boolean saveHeaders = false;

    public CsvVisitor(Admin admin, String spaceName, Map<Long, Map<NamedMetric, String>> pidMetricMap, ExponentialMovingAverage average){
        super(admin, spaceName, pidMetricMap, average);
    }

    @Override
    public void saveStat(FullMetric fullMetric) {
        Long gscPid = fullMetric.getGscPid();
        NamedMetric namedMetric = fullMetric.getMetric();
        String metricName = (gscPid == 0) ? namedMetric.displayName() : namedMetric.displayName() + "-" + gscPid;
        Map<NamedMetric, String> map = pidMetricMap.get(gscPid);
        if (map == null){
            map = new HashMap<>();
            pidMetricMap.put(gscPid, map);
        }
        prepareMetric(fullMetric);
        metrics.put(metricName, fullMetric.getMetricValue());

    }

    @Override
    public boolean isSavedOnce(NamedMetric metric) {
        return metrics.containsKey(metric);
    }

    @Override
    public void saveOnce(FullMetric fullMetric) {
        //TODO
    }

    public void printCsvMetrics(){
        if (saveHeaders){
            StringBuilder headers = new StringBuilder();
            for (String metric : metrics.keySet()){
                headers.append(metric).append(", ");
            }
            logger.info(headers.toString().substring(0, headers.length()-2));
        }
        StringBuilder values = new StringBuilder();
        for (String metricValue : metrics.values()){
            values.append(metricValue).append(", ");
        }
        logger.info(values.toString().substring(0, values.length()-2));
    }

    public void setSaveHeaders(boolean saveHeaders) {
        this.saveHeaders = saveHeaders;
    }
}
