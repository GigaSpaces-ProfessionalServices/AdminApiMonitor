package com.gigaspaces.sbp.metrics;

import com.gigaspaces.sbp.metrics.metric.*;
import com.gigaspaces.sbp.metrics.visitor.CsvVisitor;
import com.gigaspaces.sbp.metrics.visitor.PrintVisitor;
import com.gigaspaces.sbp.metrics.visitor.StatsVisitor;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;

public class CollectPeriodicMetricsVisitorTask extends AbstractPeriodicVisitorTask {

    public void collectMetrics(){

        List<String> spaceNames = new ArrayList<>();
        for (String name : Arrays.asList(spaceName.split(","))){
            spaceNames.add(name.trim());
        }
        if (csv){
            CsvVisitor visitor = new CsvVisitor(adminMonitor.getAdmin(), spaceNames, pidMetricMap, exponentialMovingAverage, alerts, period);
            if (!headersSaved){
                visitor.setSaveHeaders(true);
                headersSaved = true;
            }
            for (NamedMetric metric : metrics){
                metric.accept(visitor);
            }
            visitor.printCsvMetrics();
        }   else {
            StatsVisitor visitor = new PrintVisitor(adminMonitor.getAdmin(), spaceNames, pidMetricMap, exponentialMovingAverage, alerts, period);
            for (NamedMetric metric : metrics){
                metric.accept(visitor);
            }
        }
    }

    @Required
    public void setCsv(Boolean csv) {
        this.csv = csv;
    }

    @Required
    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    @Required
    public void setAdminMonitor(AdminApiMonitorRunner adminMonitor) {
        this.adminMonitor = adminMonitor;
    }

    @Required
    public void setExponentialMovingAverage(ExponentialMovingAverage exponentialMovingAverage) {
        this.exponentialMovingAverage = exponentialMovingAverage;
    }

    @Required
    public void setPeriod(Long period) {
        this.period = period;
    }
}
