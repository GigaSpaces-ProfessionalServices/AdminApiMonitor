package com.gigaspaces.sbp.metrics.metric;

import com.gigaspaces.sbp.metrics.FullMetric;
import com.gigaspaces.sbp.metrics.visitor.StatsVisitor;
import org.openspaces.admin.pu.ProcessingUnitInstance;
import org.openspaces.admin.pu.ProcessingUnitType;
import org.openspaces.pu.service.ServiceDetails;

import java.util.HashMap;
import java.util.Map;

public enum InstanceCount implements NamedMetric{

    PU("pu_total_count"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            int puCount = statsVisitor.processingUnitInstances().size();
            FullMetric metric = new FullMetric.FullMetricBuilder().metric(this).metricValue(String.valueOf(puCount)).create();
            statsVisitor.saveStat(metric);
        }
    },
    PU_COUNT_BY_TYPE("pu_count_by_type"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            Map<ProcessingUnitType, Integer> processingUnitCountByType = new HashMap<>();
            for (ProcessingUnitInstance processingUnitInstance : statsVisitor.processingUnitInstances()){
                ProcessingUnitType type = processingUnitInstance.getProcessingUnit().getType();
                Integer count = processingUnitCountByType.get(type);
                if (count != null){
                    processingUnitCountByType.put(type, count + 1);
                }   else {
                    processingUnitCountByType.put(type, 1);
                }
            }
            for (ProcessingUnitType type : processingUnitCountByType.keySet()){
                Integer count = processingUnitCountByType.get(type);
                FullMetric metric = new FullMetric.FullMetricBuilder().metric(this).metricValue(String.valueOf(count)).qualifier(type.toString()).create();
                statsVisitor.saveStat(metric);
            }
        }
    },
    GSMS("gsm_count"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            int gsmCount = statsVisitor.admin().getGridServiceManagers().getSize();
            FullMetric metric = new FullMetric.FullMetricBuilder().metric(this).metricValue(String.valueOf(gsmCount)).create();
            statsVisitor.saveStat(metric);
        }
    },
    GSC("gsc_count"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            int gscCount = statsVisitor.admin().getGridServiceContainers().getSize();
            FullMetric metric = new FullMetric.FullMetricBuilder().metric(this).metricValue(String.valueOf(gscCount)).create();
            statsVisitor.saveStat(metric);
        }
    },
    LUS("lus_count"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            int lusCount = statsVisitor.admin().getLookupServices().getSize();
            FullMetric metric = new FullMetric.FullMetricBuilder().metric(this).metricValue(String.valueOf(lusCount)).create();
            statsVisitor.saveStat(metric);
        }
    },
    GSA("gsa_count"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            int gsaCount = statsVisitor.admin().getGridServiceAgents().getSize();
            FullMetric metric = new FullMetric.FullMetricBuilder().metric(this).metricValue(String.valueOf(gsaCount)).create();
            statsVisitor.saveStat(metric);
        }
    },
    LOCAL_VIEW("localview_count"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            int localviewCount = 0;
            for (ProcessingUnitInstance processingUnitInstance : statsVisitor.processingUnitInstances()){
                Map<String, ServiceDetails[]> serviceDetailsByServiceType = processingUnitInstance.getServiceDetailsByServiceType();
                for (ServiceDetails[] serviceDetailses : serviceDetailsByServiceType.values()){
                    for (ServiceDetails serviceDetails : serviceDetailses){
                        String serviceSubType = serviceDetails.getServiceSubType();
                        if ("localview".equals(serviceSubType)){
                            localviewCount++;
                        }
                    }
                }
            }
            FullMetric metric = new FullMetric.FullMetricBuilder().metric(this).metricValue(String.valueOf(localviewCount)).create();
            statsVisitor.saveStat(metric);
        }
    },
    LOCAL_CACHE("localcache_count"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            int localcacheCount = 0;
            for (ProcessingUnitInstance processingUnitInstance : statsVisitor.processingUnitInstances()){
                Map<String, ServiceDetails[]> serviceDetailsByServiceType = processingUnitInstance.getServiceDetailsByServiceType();
                for (ServiceDetails[] serviceDetailses : serviceDetailsByServiceType.values()){
                    for (ServiceDetails serviceDetails : serviceDetailses){
                        String serviceSubType = serviceDetails.getServiceSubType();
                        if ("localcache".equals(serviceSubType)){
                            localcacheCount++;
                        }
                    }
                }
            }
            FullMetric metric = new FullMetric.FullMetricBuilder().metric(this).metricValue(String.valueOf(localcacheCount)).create();
            statsVisitor.saveStat(metric);
        }
    }
    ,
    ;

    private final String displayName;

    @Override
    public String displayName() {
        return displayName;
    }

    InstanceCount(String displayName) {
        this.displayName = displayName;
    }

}
