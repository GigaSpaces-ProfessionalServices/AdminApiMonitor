package com.gigaspaces.sbp.metrics;

import com.gigaspaces.sbp.metrics.metric.NamedMetric;

import java.util.Date;

public class FullMetric {

    private NamedMetric metric;

    private String metricValue;

    private Date time;

    private String hostName;

    private String spaceInstanceID;

    private Long gscPid;

    private String qualifier;

    private String spaceMode;

    public FullMetric(NamedMetric metric, String metricValue, Date time, String hostName, String spaceInstanceID, Long gscPid, String qualifier, String spaceMode) {
        this.metric = metric;
        this.metricValue = metricValue;
        this.time = time;
        this.hostName = hostName;
        this.spaceInstanceID = spaceInstanceID;
        this.gscPid = gscPid;
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

    public Long getGscPid() {
        return gscPid;
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
        }   else if (getGscPid() != null && gscPid != 0l){
            result.append(gscPid);
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

        private Long gscPid = 0l;

        private String qualifier;

        private String spaceMode;

        public FullMetricBuilder metric(NamedMetric metric){
            this.metric = metric;
            return this;
        }

        public FullMetricBuilder metricValue(String metricValue){
            this.metricValue = metricValue;
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

        public FullMetricBuilder gscPid(Long gscPid){
            this.gscPid = gscPid;
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
            return new FullMetric(metric, metricValue, new Date(), hostName, spaceInstanceID, gscPid, qualifier, spaceMode);
        }

    }
}
