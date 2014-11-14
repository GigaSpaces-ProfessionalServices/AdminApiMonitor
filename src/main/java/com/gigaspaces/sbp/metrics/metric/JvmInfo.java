package com.gigaspaces.sbp.metrics.metric;

import com.gigaspaces.sbp.metrics.visitor.StatsVisitor;
import org.openspaces.admin.GridComponent;

import org.openspaces.admin.os.OperatingSystem;
import org.openspaces.admin.vm.VirtualMachine;
import org.openspaces.admin.vm.VirtualMachineStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.util.Arrays.*;

public enum JvmInfo implements GridComponentMetric {

    GARBAGE_COLLECTION_COUNT("gc_count"){
        @Override
        public String getMetricValue(GridComponent gridComponent) {
            VirtualMachineStatistics stats = gridComponent.getVirtualMachine().getStatistics();
            if( stats == null ) return null;
            Long gcCount = stats.getGcCollectionCount();
            if( gcCount == null ) return null;
            return String.valueOf(gcCount);
        }
    }
    , GARBAGE_COLLECTION_TIME_IN_SECONDS("gc_time_secs"){
        @Override
        public String getMetricValue(GridComponent gridComponent) {
            VirtualMachineStatistics stats = gridComponent.getVirtualMachine().getStatistics();
            if( stats == null ) return null;
            Long gcTime = stats.getGcCollectionTime();
            if( gcTime == null ) return null;
            gcTime /= 1000l;
            return String.valueOf(gcTime);
        }
    }
    , THREAD_COUNT("threads"){
        @Override
        public String getMetricValue(GridComponent gridComponent) {
            VirtualMachineStatistics stats = gridComponent.getVirtualMachine().getStatistics();
            if( stats == null ) return null;
            Integer threads = stats.getThreadCount();
            if( threads == null ) return null;
            return String.valueOf(threads);
        }
    }
    , JVM_UPTIME("jvm_uptime_secs"){
        @Override
        public String getMetricValue(GridComponent gridComponent) {
            VirtualMachineStatistics stats = gridComponent.getVirtualMachine().getStatistics();
            if( stats == null ) return null;
            Long upTime = stats.getUptime();
            if( upTime == null ) return null;
            upTime /= 1000;
            return String.valueOf(upTime);
        }
    }
    ,
    /**
     * There is currently not a good way to do this in Java, particularly across a network.
     * We're going to hack it for now with the bash script gsc_cpu_monitor.sh instead.
     */
    @Deprecated
    JVM_CPU_LOAD("cpu_usage"){
        @Override
        public String getMetricValue(GridComponent gridComponent) {
            VirtualMachine virtualMachine = gridComponent.getVirtualMachine();
            VirtualMachineStatistics statistics = virtualMachine.getStatistics();
            OperatingSystem operatingSystem = virtualMachine.getMachine().getOperatingSystem();
            int cpuNum = operatingSystem.getDetails().getAvailableProcessors();
            double cpuPerc = statistics.getCpuPerc();
            if( cpuPerc >= 0 ){
                //divide to number of CPUs
                cpuPerc = (cpuPerc/cpuNum) * 100;
            }
            return String.format(Locale.ENGLISH, "%.3f", cpuPerc);
        }
    }
    ;

    static Map<String, List<? extends GridComponent>> getGridComponentsByType(StatsVisitor statsVisitor){
        Map<String, List<? extends GridComponent>> result = new HashMap<>();
        result.put("GSM", asList(statsVisitor.admin().getGridServiceManagers().getManagers()));
        result.put("GSA", asList(statsVisitor.admin().getGridServiceAgents().getAgents()));
        result.put("LUS", asList(statsVisitor.admin().getLookupServices().getLookupServices()));
        result.put("GSC", statsVisitor.gridServiceContainers());
        return result;
    }


    private static final Logger LOGGER = LoggerFactory.getLogger("file");
    private static final JmxUtils JMX_UTILS = new JmxUtils();
    private final String displayName;

    JvmInfo(String displayName) {
        this.displayName = displayName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public void accept(StatsVisitor statsVisitor) {
        Map<String, List<? extends GridComponent>> gridComponentsByType = JvmInfo.getGridComponentsByType(statsVisitor);
        for (String type : gridComponentsByType.keySet()){
            List<? extends GridComponent> gridComponents = gridComponentsByType.get(type);
            for (GridComponent gridComponent: gridComponents){
                FullMetric fullMetric = new FullMetric.FullMetricBuilder().
                        metric(this).
                        metricValue(getMetricValue(gridComponent)).
                        hostName(gridComponent.getMachine().getHostName()).
                        gridComponentName(type).
                        pid(gridComponent.getVirtualMachine().getDetails().getPid()).
                        create();
                statsVisitor.saveStat(fullMetric);
            }
        }
    }


}
