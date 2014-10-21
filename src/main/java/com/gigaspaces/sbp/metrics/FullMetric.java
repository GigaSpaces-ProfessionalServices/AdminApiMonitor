package com.gigaspaces.sbp.metrics;

import com.gigaspaces.sbp.metrics.metric.NamedMetric;

import java.util.Date;

public class FullMetric {

    private NamedMetric metric;

    private String metricValue;

    private Date time;

    private String hostName;

    private String spaceInstanceID;

    private String gridComponentName;

    private Long pid;

    private String qualifier;

    private String spaceMode;

    public FullMetric(NamedMetric metric, String metricValue, Date time, String hostName, String spaceInstanceID, String gridComponentName,Long pid, String qualifier, String spaceMode) {
        this.metric = metric;
        this.metricValue = metricValue;
        this.time = time;
        this.hostName = hostName;
        this.spaceInstanceID = spaceInstanceID;
        this.gridComponentName = gridComponentName;
        this.pid = pid;
        this.qualifier = qualifier;
        this.spaceMode = spaceMode;
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

    public String getSpaceInstanceID() {
        return spaceInstanceID;
    }

    public String getGridComponentName() {
        return gridComponentName;
    }

    public Long getPid() {
        return pid;
    }

    public void setMetricValue(String metricValue) {
        this.metricValue = metricValue;
    }

    public String getSpaceMode() {
        return spaceMode;
    }

    public String getMetricFullName(){
        StringBuilder result = new StringBuilder();
        result.append(metric.displayName()).append("_");
        if (qualifier != null){
            result.append(qualifier);
        }   else if (getPid() != null && pid != 0l){
            result.append(pid);
        }   else if (spaceInstanceID != null){
            result.append(spaceInstanceID);
        }
        return result.toString();
    }

    public static class FullMetricBuilder{

        private NamedMetric metric;

        private String metricValue;

        private Date time;

        private String hostName;

        private String spaceInstanceID;

        private String gridComponentName;

        private Long pid = 0l;

        private String qualifier;

        private String spaceMode;

        public FullMetricBuilder metric(NamedMetric metric){
            this.metric = metric;
            return this;
        }

        public FullMetricBuilder metricValue(String metricValue){
            this.metricValue = metricValue.replace(",",".");
            return this;
        }

        public FullMetricBuilder hostName(String hostName){
            this.hostName = hostName;
            return this;
        }

        public FullMetricBuilder spaceInstanceID(String spaceInstanceID){
            this.spaceInstanceID = spaceInstanceID;
            return this;
        }

        public FullMetricBuilder gridComponentName(String gridComponentName){
            this.gridComponentName = gridComponentName;
            return this;
        }

        public FullMetricBuilder pid(Long pid){
            this.pid = pid;
            return this;
        }

        public FullMetricBuilder qualifier(String qualifier){
            this.qualifier = qualifier;
            return this;
        }

        public FullMetricBuilder spaceMode(String spaceMode){
            this.spaceMode = spaceMode;
            return this;
        }

        public FullMetric create(){
            return new FullMetric(metric, metricValue, new Date(), hostName, spaceInstanceID, gridComponentName, pid, qualifier, spaceMode);
        }

    }
}
