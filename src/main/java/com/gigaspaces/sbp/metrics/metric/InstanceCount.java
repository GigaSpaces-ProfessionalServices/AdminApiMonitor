package com.gigaspaces.sbp.metrics.metric;

import com.gigaspaces.sbp.metrics.FullMetric;
import com.gigaspaces.sbp.metrics.visitor.StatsVisitor;
import org.openspaces.admin.gsc.GridServiceContainer;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitInstance;
import org.openspaces.pu.service.ServiceDetails;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public enum InstanceCount implements NamedMetric{

    PU("pu_count"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            int puCount = statsVisitor.admin().getProcessingUnits().getSize();
            FullMetric metric = new FullMetric.FullMetricBuilder().metric(this).metricValue(String.valueOf(puCount)).create();
            statsVisitor.saveStat(metric);
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
    //TODO move to another enums
    TOTAL_CONNECTED_MACHINES("total_connected_machines"){ //TODO
        @Override
        public void accept(StatsVisitor statsVisitor) {
            Set<String> classes = new HashSet<>();
            for (GridServiceContainer gsc : statsVisitor.gridServiceContainers()){
//                String[] classNames =
//                classes.addAll(Arrays.asList(classNames));
                gsc.getGridServiceAgent();
                ProcessingUnit processingUnit = gsc.getProcessingUnitInstances()[0].getProcessingUnit();
            }
            FullMetric metric = new FullMetric.FullMetricBuilder().metric(this).metricValue(String.valueOf(classes.size())).create();
            statsVisitor.saveStat(metric);
        }
    },
    CONNECTED_MACHINES("connected_machines"){ //TODO
        @Override
        public void accept(StatsVisitor statsVisitor) {
//            for (Space space : statsVisitor.admin().getSpaces()){
//                Integer classesCount = space.getRuntimeDetails().getClassNames().length;
//                FullMetric metric = new FullMetric.FullMetricBuilder().metric(this).metricValue(String.valueOf(classesCount))
//                        .qualifier(space.getName()).create();
//                statsVisitor.saveStat(metric);
//            }
//            Admin admin = statsVisitor.admin();
//            SpaceInstance spaceInstance = statsVisitor.spaceInstance().get(0);
//            spaceInstance.getRuntimeDetails().getLocalCacheDetails();
        }
    },
    WEB_PU("web_pu"){ //TODO
        @Override
        public void accept(StatsVisitor statsVisitor) {
//            for (GridServiceContainer gsc : statsVisitor.gridServiceContainers()){
//                for (ProcessingUnitInstance processingUnit : gsc.getProcessingUnitInstances()){
//                    Map<String, ServiceDetails[]> serviceDetailsByServiceType = processingUnit.getServiceDetailsByServiceType();
//                }
//                gsc.getGridServiceAgent();
//                ProcessingUnit processingUnit = gsc.getProcessingUnitInstances()[0].getProcessingUnit();
//            }
        }
    },
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
