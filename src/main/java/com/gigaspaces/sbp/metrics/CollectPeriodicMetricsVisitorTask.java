package com.gigaspaces.sbp.metrics;

import com.gigaspaces.sbp.metrics.visitor.CsvVisitor;
import com.gigaspaces.sbp.metrics.visitor.PrintVisitor;
import com.gigaspaces.sbp.metrics.visitor.StatsVisitor;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectPeriodicMetricsVisitorTask {

    private AdminApiMonitorRunner adminMonitor;

    private String spaceName;

    private boolean headersSaved = false;

    private boolean csv;

    private void collectMetrics(){
        List<NamedMetric> metrics = new ArrayList<>();
        metrics.addAll(Arrays.asList(GigaSpacesActivity.values()));
        metrics.addAll(Arrays.asList(GigaSpacesClusterInfo.values()));
        metrics.addAll(Arrays.asList(GsMirrorInfo.values()));
        metrics.addAll(Arrays.asList(JvmInfo.values()));
        metrics.addAll(Arrays.asList(Memory.values()));
        metrics.addAll(Arrays.asList(OperatingSystemInfo.values()));
        if (csv){
            CsvVisitor visitor = new CsvVisitor(adminMonitor.getAdmin(), spaceName);
            if (!headersSaved){
                visitor.setSaveHeaders(true);
                headersSaved = true;
            }
            for (NamedMetric metric : metrics){
                metric.accept(visitor);
            }
            visitor.printCsvMetrics();
        }   else {
            StatsVisitor visitor = new PrintVisitor(adminMonitor.getAdmin(), spaceName);
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

}
