package com.gigaspaces.sbp.metrics;

import com.gigaspaces.sbp.metrics.metric.*;
import com.gigaspaces.sbp.metrics.visitor.CsvVisitor;
import com.gigaspaces.sbp.metrics.visitor.PrintVisitor;
import com.gigaspaces.sbp.metrics.visitor.StatsVisitor;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;

public class CollectPeriodicMetricsVisitorTask {

    private AdminApiMonitorRunner adminMonitor;

    private String spaceName;

    private boolean headersSaved = false;

    private boolean csv;

    private Map<String, FullMetric> pidMetricMap = new LinkedHashMap<>();

    private ExponentialMovingAverage exponentialMovingAverage;

    private Long period;

    public void collectMetrics(){
        List<NamedMetric> metrics = new ArrayList<>();
        metrics.addAll(Arrays.asList(GigaSpacesActivity.values()));
        metrics.addAll(Arrays.asList(GigaSpacesClusterInfo.values()));
        metrics.addAll(Arrays.asList(GsMirrorInfo.values()));
        metrics.addAll(Arrays.asList(JvmInfo.values()));
        metrics.addAll(Arrays.asList(Memory.values()));
        metrics.addAll(Arrays.asList(OperatingSystemInfo.values()));
        metrics.addAll(Arrays.asList(CacheContentMetric.values()));
        List<String> spaceNames = new ArrayList<>();
        for (String name : Arrays.asList(spaceName.split(","))){
            spaceNames.add(name.trim());
        }
        if (csv){
            CsvVisitor visitor = new CsvVisitor(adminMonitor.getAdmin(), spaceNames, pidMetricMap, exponentialMovingAverage, period);
            if (!headersSaved){
                visitor.setSaveHeaders(true);
                headersSaved = true;
            }
            for (NamedMetric metric : metrics){
                metric.accept(visitor);
            }
            visitor.printCsvMetrics();
        }   else {
            StatsVisitor visitor = new PrintVisitor(adminMonitor.getAdmin(), spaceNames, pidMetricMap, exponentialMovingAverage, period);
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
