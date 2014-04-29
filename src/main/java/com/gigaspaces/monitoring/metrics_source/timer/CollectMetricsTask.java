package com.gigaspaces.monitoring.metrics_source.timer;

import com.gigaspaces.monitoring.metrics_source.adminapi.AdminAPIMonitor;
import com.gigaspaces.monitoring.metrics_source.space_proxy.SpaceProxyCounter;
import com.gigaspaces.monitoring.metrics_source.space_proxy.SpaceProxyNotificationListener;
import org.springframework.beans.factory.annotation.Required;

public class CollectMetricsTask {

    private AdminAPIMonitor adminMonitor;

    //TODO needs refactor
    private SpaceProxyNotificationListener listener;

    public void collectMetrics() {
        System.out.println("Collect metrics");
        SpaceProxyCounter spaceProxyCounter = listener.getSpaceProxyCounter();
        System.out.println("Space proxy counter = " + spaceProxyCounter);
        adminMonitor.startCollection();
    }

    @Required
    public void setAdminMonitor(AdminAPIMonitor adminMonitor) {
        this.adminMonitor = adminMonitor;
    }

    @Required
    public void setListener(SpaceProxyNotificationListener listener) {
        this.listener = listener;
    }

}
