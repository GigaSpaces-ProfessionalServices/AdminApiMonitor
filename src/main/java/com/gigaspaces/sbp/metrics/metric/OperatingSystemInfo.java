package com.gigaspaces.sbp.metrics.metric;

import com.gigaspaces.sbp.metrics.FullMetric;
import com.gigaspaces.sbp.metrics.visitor.StatsVisitor;
import org.openspaces.admin.gsc.GridServiceContainer;
import org.openspaces.admin.vm.VirtualMachineDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import javax.management.openmbean.CompositeDataSupport;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public enum OperatingSystemInfo implements NamedMetric {

    OPEN_FILE_DESCRIPTOR_COUNT("open_file_descriptors"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            final String openFdCount = "OpenFileDescriptorCount";
            List<GridServiceContainer> gridServiceContainers = statsVisitor.gridServiceContainers();
            for (GridServiceContainer gridServiceContainer : gridServiceContainers){
                VirtualMachineDetails details = gridServiceContainer.getVirtualMachine().getDetails();
                try {
                    ObjectName objectName = new ObjectName(JmxUtils.OS_SEARCH_STRING);
                    MBeanServerConnection server = JMX_UTILS.mbeanServer(details);
                    AttributeList list = server.getAttributes(objectName, new String[]{openFdCount});
                    for (Attribute attr : list.asList()){
                        if (attr.getName().equals(openFdCount)){
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
    , MAX_FILE_DESCRIPTOR_COUNT("max_file_descriptors"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            if( statsVisitor.isSavedOnce(this)) return;
            final String maxFdCount = "MaxFileDescriptorCount";
            final String osSearchString = "java.lang:type=OperatingSystem";
            List<GridServiceContainer> gridServiceContainers = statsVisitor.gridServiceContainers();
            for (GridServiceContainer gridServiceContainer : gridServiceContainers){
                VirtualMachineDetails details = gridServiceContainer.getVirtualMachine().getDetails();
                try {
                    ObjectName objectName = new ObjectName(osSearchString);
                    MBeanServerConnection server = JMX_UTILS.mbeanServer(details);
                    AttributeList list = server.getAttributes(objectName, new String[]{maxFdCount});
                    for( Attribute attr : list.asList() ){
                        if( attr.getName().equals(maxFdCount)){
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
    , LRMI_CONNECTIONS("lrmi_connections"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            final String threading = "java.lang:type=Threading";
            List<GridServiceContainer> gridServiceContainers = statsVisitor.gridServiceContainers();
            for (GridServiceContainer gridServiceContainer : gridServiceContainers){
                VirtualMachineDetails details = gridServiceContainer.getVirtualMachine().getDetails();
                try {
                    ObjectName objectName = new ObjectName(threading);
                    MBeanServerConnection server = JMX_UTILS.mbeanServer(details);
                    Set<ObjectInstance> mBeans = server.queryMBeans(objectName, null);
                    long lrmiThreadCount = 0;
                    for( ObjectInstance mbean : mBeans ){
                        ObjectName name = mbean.getObjectName();
                        if( name.toString().contains("Threading")){
                            long[] threadIds = (long[]) server.getAttribute(name, "AllThreadIds");
                            for( Long threadId : threadIds ) {
                                String[] signatures = new String[]{"long"};
                                CompositeDataSupport threadInfo =
                                        (CompositeDataSupport) server.invoke(name, "getThreadInfo", new Long[]{threadId}, signatures);
                                if( threadInfo == null ) continue;
                                Object threadName = threadInfo.get("threadName");
                                if( threadName == null ) continue;
                                if (threadName.toString().contains("LRMI Connection"))
                                    lrmiThreadCount++;
                            }
                        }
                    }
                    FullMetric fullMetric = new FullMetric.FullMetricBuilder().
                            metric(this).
                            metricValue(String.valueOf(lrmiThreadCount)).
                            hostName(gridServiceContainer.getMachine().getHostName()).
                            gscPid(gridServiceContainer.getVirtualMachine().getDetails().getPid()).
                            create();
                    statsVisitor.saveStat(fullMetric);
                } catch (IOException | MalformedObjectNameException | ReflectionException | InstanceNotFoundException | AttributeNotFoundException | MBeanException e) {
                    LOGGER.error("Error determining " + this.displayName(), e);
                }
            }
        }
    }
    ;

    private static final JmxUtils JMX_UTILS = new JmxUtils();
    private static final Logger LOGGER = LoggerFactory.getLogger("file");
    private final String displayName;

    OperatingSystemInfo(String displayName){
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
