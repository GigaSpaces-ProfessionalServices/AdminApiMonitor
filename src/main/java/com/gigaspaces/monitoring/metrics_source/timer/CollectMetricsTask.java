package com.gigaspaces.monitoring.metrics_source.timer;

import com.gigaspaces.monitoring.metrics_source.adminapi.AdminAPIMonitor;
import org.springframework.beans.factory.annotation.Required;

public class CollectMetricsTask {

    private AdminAPIMonitor adminMonitor;

    public void collectMetrics() {
        System.out.println("Collect metrics");
        adminMonitor.startCollection();
    }

    @Required
    public void setAdminMonitor(AdminAPIMonitor adminMonitor) {
        this.adminMonitor = adminMonitor;
    }
}
