package com.gigaspaces.sbp.metrics.visitor;

import com.gigaspaces.sbp.metrics.FullMetric;
import com.gigaspaces.sbp.metrics.NamedMetric;
import org.openspaces.admin.Admin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class CsvVisitor extends AbstractStatsVisitor{

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, String> metrics = new LinkedHashMap<>();

    private boolean saveHeaders = false;

    public CsvVisitor(Admin admin, String spaceName){
        super(admin, spaceName);
    }

    @Override
    public void saveStat(FullMetric fullMetric) {
        String metricName = (fullMetric.getGscPid() == null) ? fullMetric.getMetric().displayName() : fullMetric.getMetric().displayName() + "-" + fullMetric.getGscPid();
        metrics.put(metricName, fullMetric.getMetricValue());
    }

    @Override
    public boolean isSavedOnce(NamedMetric metric) {
        return metrics.containsKey(metric);
    }

    @Override
    public void saveOnce(FullMetric fullMetric) {

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
