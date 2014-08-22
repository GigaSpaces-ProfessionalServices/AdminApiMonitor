package com.gigaspaces.sbp.metrics;

import com.gigaspaces.sbp.metrics.metric.NamedMetric;
import org.openspaces.admin.gsc.GridServiceContainer;

import java.util.Date;

public class FullMetric {

    private NamedMetric metric;

    private String metricValue;

    private Date time;

    private String hostName;

    private Integer spaceInstanceID;

    private Long gscPid;

    private String qualifier;

    public FullMetric(NamedMetric metric, String metricValue, GridServiceContainer gridServiceContainer) {
        this.metric = metric;
        this.metricValue = metricValue;
        this.time = new Date();
        this.hostName = gridServiceContainer.getMachine().getHostName();
        this.spaceInstanceID = gridServiceContainer.getMachine().getSpaceInstances()[0].getInstanceId();
        this.gscPid = gridServiceContainer.getVirtualMachine().getDetails().getPid();
    }

    public FullMetric(NamedMetric metric, String metricValue, Integer spaceInstanceID) {
        this.metric = metric;
        this.metricValue = metricValue;
        this.time = new Date();
        this.gscPid = 0l;
        this.spaceInstanceID = spaceInstanceID;
    }

    public FullMetric(NamedMetric metric, String metricValue) {
        this.metric = metric;
        this.metricValue = metricValue;
        this.time = new Date();
        this.gscPid = 0l;
    }

    public FullMetric(NamedMetric metric, String metricValue, String qualifier) {
        this.metric = metric;
        this.metricValue = metricValue;
        this.qualifier = qualifier;
        this.gscPid = 0l;
    }

    public NamedMetric getMetric() {
        return metric;
    }

    public String getMetricValue() {
        return metricValue;
    }

    public Date getTime() {
        return time;
    }

    public String getHostName() {
        return hostName;
    }

    public Integer getSpaceInstanceID() {
        return spaceInstanceID;
    }

    public Long getGscPid() {
        return gscPid;
    }

    public void setMetricValue(String metricValue) {
        this.metricValue = metricValue;
    }

    public String getMetricName(){
        return (qualifier != null) ? (metric.displayName() + "_" + qualifier) : metric.displayName();
    }
}
