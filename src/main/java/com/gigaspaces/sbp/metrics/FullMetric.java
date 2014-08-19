package com.gigaspaces.sbp.metrics;

import org.openspaces.admin.gsc.GridServiceContainer;

import java.util.Date;

public class FullMetric {

    private NamedMetric metric;

    private String metricValue;

    private Date time;

    private String hostName;

    private Integer spaceInstanceID;

    private Integer gscPid;

//    public FullMetric(NamedMetric metric, String metricValue, Date time, String hostName, int spaceInstanceID, int gscPid) {
//        this.metric = metric;
//        this.metricValue = metricValue;
//        this.time = time;
//        this.hostName = hostName;
//        this.spaceInstanceID = spaceInstanceID;
//        this.gscPid = gscPid;
//    }

    public FullMetric(NamedMetric metric, String metricValue, GridServiceContainer gridServiceContainer) {
        this.metric = metric;
        this.metricValue = metricValue;
        this.time = new Date();
        this.hostName = gridServiceContainer.getMachine().getHostName();
        this.spaceInstanceID = gridServiceContainer.getMachine().getSpaceInstances()[0].getInstanceId();
        this.gscPid = gridServiceContainer.getAgentId();
    }

    public FullMetric(NamedMetric metric, String metricValue) {
        this.metric = metric;
        this.metricValue = metricValue;
        this.time = new Date();
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

    public Integer getGscPid() {
        return gscPid;
    }
}
