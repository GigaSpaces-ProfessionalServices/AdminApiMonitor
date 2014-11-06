package com.gigaspaces.sbp.metrics;

import com.gigaspaces.sbp.metrics.metric.*;
import org.openspaces.admin.Admin;
import org.openspaces.admin.alert.Alert;
import org.openspaces.admin.alert.AlertManager;
import org.openspaces.admin.alert.config.parser.XmlAlertConfigurationParser;
import org.openspaces.admin.alert.events.AlertTriggeredEventListener;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AbstractPeriodicVisitorTask {

    protected AdminApiMonitorRunner adminMonitor;

    protected String spaceName;

    protected boolean headersSaved = false;

    protected boolean csv;

    protected Map<String, FullMetric> pidMetricMap = new LinkedHashMap<>();

    protected ExponentialMovingAverage exponentialMovingAverage;

    protected ConcurrentHashMap<String, AtomicInteger> alerts = new ConcurrentHashMap<>();

    protected Long period;

    protected List<NamedMetric> metrics;

    public void init(){
        Admin admin = adminMonitor.getAdmin();
        AlertManager alertManager = admin.getAlertManager();
        alertManager.getAlertTriggered().add(new AlertTriggeredEventListener() {
            @Override
            public void alertTriggered(Alert alert) {
                String alertName = alert.getName();
                alerts.putIfAbsent(alertName, new AtomicInteger(0));
                alerts.get(alertName).incrementAndGet();
            }
        });

        metrics = new ArrayList<>();
        metrics.addAll(Arrays.asList(GigaSpacesActivity.values()));
        metrics.addAll(Arrays.asList(GigaSpacesClusterInfo.values()));
        metrics.addAll(Arrays.asList(GsMirrorInfo.values()));
        metrics.addAll(Arrays.asList(JvmInfo.values()));
        metrics.addAll(Arrays.asList(Memory.values()));
        metrics.addAll(Arrays.asList(OperatingSystemInfo.values()));
//        metrics.addAll(Arrays.asList(MonitoringToolInfo.values()));
        metrics.addAll(Arrays.asList(InstanceCount.values()));
        metrics.addAll(Arrays.asList(CacheContentMetric.values()));
        metrics.addAll(Arrays.asList(AlertsInfo.values()));

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
