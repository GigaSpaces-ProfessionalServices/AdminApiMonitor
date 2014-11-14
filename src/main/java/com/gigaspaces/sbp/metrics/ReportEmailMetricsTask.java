package com.gigaspaces.sbp.metrics;

import com.gigaspaces.sbp.metrics.metric.FullMetric;
import com.gigaspaces.sbp.metrics.metric.NamedMetric;
import com.gigaspaces.sbp.metrics.visitor.CsvVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ReportEmailMetricsTask extends AbstractPeriodicVisitorTask {

    private Logger logger = LoggerFactory.getLogger("email_reports");

    public void reportMetrics(){
        List<String> spaceNames = new ArrayList<>();
        for (String name : Arrays.asList(spaceName.split(","))){
            spaceNames.add(name.trim());
        }
        CsvVisitor visitor = null;//new CsvVisitor(adminMonitor.getAdmin(), spaceNames, pidMetricMap, exponentialMovingAverage, alerts, period);
        for (NamedMetric metric : metrics){
            metric.accept(visitor);
        }
        Map<String, FullMetric> visitorMetrics = visitor.getMetrics();
        StringBuilder buffer = new StringBuilder("Metrics: \n");
        for (Map.Entry<String, FullMetric> pair : visitorMetrics.entrySet()){
            buffer.append(pair.getValue().getMetricFullName()).append(" = ").append(pair.getValue().getMetricValue()).append("\n");
        }
        logger.error(buffer.toString());
    }

}
