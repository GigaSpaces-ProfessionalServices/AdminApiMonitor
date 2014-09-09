package com.gigaspaces.sbp.metrics.metric;

import com.gigaspaces.sbp.metrics.FullMetric;
import com.gigaspaces.sbp.metrics.visitor.StatsVisitor;

import java.util.*;

public enum AlertsInfo implements NamedMetric{

    ALERT("alert"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            Map<String, Integer> alerts = statsVisitor.alerts();
            for (String alert : alerts.keySet()){
                statsVisitor.saveStat(new FullMetric.FullMetricBuilder().metric(this).metricValue(String.valueOf(alerts.get(alert))).qualifier(alert).create());
            }
        }
    }
    ;

    private final String displayName;

    AlertsInfo(String displayName){
        this.displayName = displayName;
    }

    @Override
    public String displayName() {
        return displayName;
    }

}