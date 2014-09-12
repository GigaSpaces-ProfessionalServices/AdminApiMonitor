package com.gigaspaces.sbp.metrics.metric;

import com.gigaspaces.sbp.metrics.FullMetric;
import com.gigaspaces.sbp.metrics.visitor.StatsVisitor;
import org.openspaces.admin.gsc.GridServiceContainer;
import org.openspaces.admin.vm.VirtualMachineDetails;
import org.openspaces.admin.vm.VirtualMachineStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.io.IOException;
import java.util.List;

public enum JvmInfo implements NamedMetric {

    GARBAGE_COLLECTION_COUNT("gc_count"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            List<GridServiceContainer> gridServiceContainers = statsVisitor.gridServiceContainers();
            for (GridServiceContainer gridServiceContainer : gridServiceContainers){
                VirtualMachineStatistics stats = gridServiceContainer.getVirtualMachine().getStatistics();
                if( stats == null ) return;
                Long gcCount = stats.getGcCollectionCount();
                if( gcCount == null ) return;
                FullMetric fullMetric = new FullMetric.FullMetricBuilder().
                        metric(this).
                        metricValue(String.valueOf(gcCount)).
                        hostName(gridServiceContainer.getMachine().getHostName()).
                        gscPid(gridServiceContainer.getVirtualMachine().getDetails().getPid()).
                        create();
                statsVisitor.saveStat(fullMetric);
            }
        }
    }
    , GARBAGE_COLLECTION_TIME_IN_SECONDS("gc_time_secs"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            List<GridServiceContainer> gridServiceContainers = statsVisitor.gridServiceContainers();
            for (GridServiceContainer gridServiceContainer : gridServiceContainers){
                VirtualMachineStatistics stats = gridServiceContainer.getVirtualMachine().getStatistics();
                if( stats == null ) return;
                Long gcTime = stats.getGcCollectionTime();
                if( gcTime == null ) return;
                gcTime /= 1000l;
                FullMetric fullMetric = new FullMetric.FullMetricBuilder().
                        metric(this).
                        metricValue(String.valueOf(gcTime)).
                        hostName(gridServiceContainer.getMachine().getHostName()).
                        gscPid(gridServiceContainer.getVirtualMachine().getDetails().getPid()).
                        create();
                statsVisitor.saveStat(fullMetric);
            }
        }
    }
    , THREAD_COUNT("threads"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            List<GridServiceContainer> gridServiceContainers = statsVisitor.gridServiceContainers();
            for (GridServiceContainer gridServiceContainer : gridServiceContainers){
                VirtualMachineStatistics stats = gridServiceContainer.getVirtualMachine().getStatistics();
                if( stats == null ) return;
                Integer threads = stats.getThreadCount();
                if( threads == null ) return;
                FullMetric fullMetric = new FullMetric.FullMetricBuilder().
                        metric(this).
                        metricValue(String.valueOf(threads)).
                        hostName(gridServiceContainer.getMachine().getHostName()).
                        gscPid(gridServiceContainer.getVirtualMachine().getDetails().getPid()).
                        create();
                statsVisitor.saveStat(fullMetric);
            }
        }
    }
    , JVM_UPTIME("jvm_uptime_secs"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            List<GridServiceContainer> gridServiceContainers = statsVisitor.gridServiceContainers();
            for (GridServiceContainer gridServiceContainer : gridServiceContainers){
                VirtualMachineStatistics stats = gridServiceContainer.getVirtualMachine().getStatistics();
                if( stats == null ) return;
                Long upTime = stats.getUptime();
                if( upTime == null ) return;
                upTime /= 1000;
                FullMetric fullMetric = new FullMetric.FullMetricBuilder().
                        metric(this).
                        metricValue(String.valueOf(upTime)).
                        hostName(gridServiceContainer.getMachine().getHostName()).
                        gscPid(gridServiceContainer.getVirtualMachine().getDetails().getPid()).
                        create();
                statsVisitor.saveStat(fullMetric);
            }
        }
    }
    , JVM_CPU_LOAD("cpu_usage"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            List<GridServiceContainer> gridServiceContainers = statsVisitor.gridServiceContainers();
            for (GridServiceContainer gridServiceContainer : gridServiceContainers){
                VirtualMachineDetails details = gridServiceContainer.getVirtualMachine().getDetails();
                final String cpuLoad = "ProcessCpuLoad";
                try {
                    ObjectName objectName = new ObjectName(JmxUtils.OS_SEARCH_STRING);
                    MBeanServerConnection server = JMX_UTILS.mbeanServer(details, JmxUtils.OS_SEARCH_STRING);
                    AttributeList list = server.getAttributes(objectName, new String[]{cpuLoad});
                    for( Attribute attr : list.asList() ){
                        if( attr.getName().equals(cpuLoad)){
                            FullMetric fullMetric = new FullMetric.FullMetricBuilder().
                                    metric(this).
                                    metricValue(String.valueOf(attr.getValue().toString())).
                                    hostName(gridServiceContainer.getMachine().getHostName()).
                                    gscPid(gridServiceContainer.getVirtualMachine().getDetails().getPid()).
                                    create();
                            statsVisitor.saveStat(fullMetric);
                        }
                    }
                } catch (IOException | MalformedObjectNameException | ReflectionException | InstanceNotFoundException e) {
                    LOGGER.error("Error determining " + this.displayName(), e);
                }
            }
        }
    }
    ;

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

}
