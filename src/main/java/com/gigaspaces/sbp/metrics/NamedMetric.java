package com.gigaspaces.sbp.metrics;

import com.gigaspaces.cluster.replication.async.mirror.MirrorStatistics;
import com.j_spaces.core.filters.ReplicationStatistics;
import org.openspaces.admin.space.SpaceInstance;
import org.openspaces.admin.space.SpaceInstanceStatistics;
import org.openspaces.admin.vm.VirtualMachineDetails;
import org.openspaces.admin.vm.VirtualMachinesStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 8/4/14
 * Time: 5:47 PM
 *
 * A collection of a bunch of names for metrics we can easily gather from Admin API.
 * And a few that customers want us to gather anyway.
 */

/**
 * An interface for metrics that have names...
 */
public interface NamedMetric {

    /**
     * @return human-friendly name for this metric
     */
    String displayName();

    /**
     * Accepts a statsVisitor and saves of a statistic...
     * @param statsVisitor visitor
     */
    void accept(StatsVisitor statsVisitor);
}

enum GigaSpacesClusterInfo implements NamedMetric {

    SPACE_MODE("space_mode"){
        public void accept(StatsVisitor statsVisitor){
            SpaceInstance spaceInstance = statsVisitor.spaceInstance();
            if( spaceInstance != null ){
                statsVisitor.saveStat(this, spaceInstance.getMode().name());
            }
        }
    }
    ;

    private final String displayName;

    GigaSpacesClusterInfo(String displayName){
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

enum GigaSpacesActivity implements NamedMetric {

    READ_COUNT("reads"){
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            SpaceInstance spaceInstance = visitor.spaceInstance();
            if( spaceInstance != null ){
                SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                Long readCount = stats.getReadCount();
                visitor.saveStat(this, String.valueOf(readCount));
            }
        }
    }
    , READ_PER_SEC("reads_per_sec"){
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            SpaceInstance spaceInstance = visitor.spaceInstance();
            if( spaceInstance != null ){
                SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                Double readsPerSec = stats.getReadPerSecond();
                visitor.saveStat(this, String.valueOf(readsPerSec));
            }
        }
    }
    , WRITE_COUNT("writes"){
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            SpaceInstance spaceInstance = visitor.spaceInstance();
            if( spaceInstance != null ){
                SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                Long writeCount = stats.getWriteCount();
                visitor.saveStat(this, String.valueOf(writeCount));
            }
        }
    }
    , WRITES_PER_SEC("writes_per_sec"){
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            SpaceInstance spaceInstance = visitor.spaceInstance();
            if( spaceInstance != null ){
                SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                Double writesPerSec = stats.getWritePerSecond();
                visitor.saveStat(this, String.valueOf(writesPerSec));
            }
        }
    }
    , EXECUTE_COUNT("executes"){
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            SpaceInstance spaceInstance = visitor.spaceInstance();
            if( spaceInstance != null ){
                SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                Long executeCount = stats.getExecuteCount();
                visitor.saveStat(this, String.valueOf(executeCount));
            }
        }
    }
    , EXECUTES_PER_SEC("executes_per_sec"){
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            SpaceInstance spaceInstance = visitor.spaceInstance();
            if( spaceInstance != null ){
                SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                Double execPerSec = stats.getExecutePerSecond();
                visitor.saveStat(this, String.valueOf(execPerSec));
            }
        }
    }
    , TAKE_COUNT("takes"){
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            SpaceInstance spaceInstance = visitor.spaceInstance();
            if( spaceInstance != null ){
                SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                Long takeCount = stats.getTakeCount();
                visitor.saveStat(this, String.valueOf(takeCount));
            }
        }
    }
    , TAKES_PER_SECOND("takes_per_sec"){
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            SpaceInstance spaceInstance = visitor.spaceInstance();
            if( spaceInstance != null ){
                SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                Double takePerSec = stats.getTakePerSecond();
                visitor.saveStat(this, String.valueOf(takePerSec));
            }
        }
    }
    , UPDATE_COUNT("updates") { // also applies to "change'
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            SpaceInstance spaceInstance = visitor.spaceInstance();
            if( spaceInstance != null ){
                SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                Long updateCount = stats.getUpdateCount();
                visitor.saveStat(this, String.valueOf(updateCount));
            }
        }
    }
    , UPDATES_PER_SEC("updates_per_sec") { // also applies to "change'
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            SpaceInstance spaceInstance = visitor.spaceInstance();
            if( spaceInstance != null ){
                SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                Double updatesPerSec = stats.getUpdatePerSecond();
                visitor.saveStat(this, String.valueOf(updatesPerSec));
            }
        }
    }
    , TRANSACTION_COUNT("active_transactions"){
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            SpaceInstance spaceInstance = visitor.spaceInstance();
            if( spaceInstance != null ){
                SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                Long activeTransactions = stats.getActiveTransactionCount();
                visitor.saveStat(this, String.valueOf(activeTransactions));
            }
        }
    }
    , CONNECTION_COUNT("active_connections"){
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            SpaceInstance spaceInstance = visitor.spaceInstance();
            if( spaceInstance != null ){
                SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                Long connectionCount = stats.getActiveConnectionCount();
                visitor.saveStat(this, String.valueOf(connectionCount));
            }
        }
    }
    ;

    private final String displayName;

    GigaSpacesActivity(String displayName) {
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

enum GsMirrorInfo implements NamedMetric {

    REDO_LOG_SIZE("redo_log_size"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            ReplicationStatistics replicationStatistics = statsVisitor.replicationStatistics();
            if( replicationStatistics == null ) return;
            ReplicationStatistics.OutgoingReplication out = replicationStatistics.getOutgoingReplication();
            if( out == null ) return;
            Long logSize = out.getRedoLogSize();
            statsVisitor.saveStat(this, String.valueOf(logSize));
        }
    }
    , REDO_LOG_SEND_BYTES_PER_SECOND("mirror_sent_bytes_per_sec"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            ReplicationStatistics replicationStatistics = statsVisitor.replicationStatistics();
            if( replicationStatistics == null ) return;
            ReplicationStatistics.OutgoingReplication out = replicationStatistics.getOutgoingReplication();
            if( out == null ) return;
            List<ReplicationStatistics.OutgoingChannel> channelList = out.getChannels();
            if( channelList == null ) return;
            long byteCount = 0;
            for(ReplicationStatistics.OutgoingChannel channel : channelList ){
                if( channel != null ) byteCount += channel.getSendBytesPerSecond();
            }
            statsVisitor.saveStat(this, String.valueOf(byteCount));
        }
    }
    , MIRROR_OPERATIONS("mirror_total_operations"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            MirrorStatistics mirrorStatistics= statsVisitor.mirrorStatistics();
            if( mirrorStatistics == null ) return;
            Long operationCount = mirrorStatistics.getOperationCount();
            if( operationCount == null ) return;
            statsVisitor.saveStat(this, operationCount.toString());
        }
    }
    , MIRROR_SUCCESSFUL_OPERATIONS("mirror_successes"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            MirrorStatistics mirrorStatistics= statsVisitor.mirrorStatistics();
            if( mirrorStatistics == null ) return;
            Long successfulOperations = mirrorStatistics.getSuccessfulOperationCount();
            if( successfulOperations == null ) return;
            statsVisitor.saveStat(this, successfulOperations.toString());
        }
    }
    , MIRROR_FAILED_OPERATIONS("mirror_failures"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            MirrorStatistics mirrorStatistics= statsVisitor.mirrorStatistics();
            if( mirrorStatistics == null ) return;
            Long failedOperations = mirrorStatistics.getFailedOperationCount();
            if( failedOperations == null ) return;
            statsVisitor.saveStat(this, failedOperations.toString());
        }
    }
    ;

    private final String displayName;

    GsMirrorInfo(String displayName){
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

enum OperatingSystemInfo implements NamedMetric {

    OPEN_FILE_DESCRIPTOR_COUNT("open_file_descriptors"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            final String openFdCount = "OpenFileDescriptorCount";
            try {
                ObjectName objectName = new ObjectName(JmxUtils.OS_SEARCH_STRING);
                MBeanServerConnection server = JMX_UTILS.mbeanServer(statsVisitor.virtualMachineDetails(), JmxUtils.OS_SEARCH_STRING);
                AttributeList list = server.getAttributes(objectName, new String[]{openFdCount});
                for( Attribute attr : list.asList() ) if( attr.getName().equals(openFdCount)) statsVisitor.saveStat(this, attr.getValue().toString());
            } catch (IOException | MalformedObjectNameException | ReflectionException | InstanceNotFoundException e) {
                LOGGER.error("Error determining " + this.displayName(), e);
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
            try {
                ObjectName objectName = new ObjectName(osSearchString);
                MBeanServerConnection server = JMX_UTILS.mbeanServer(statsVisitor.virtualMachineDetails(), osSearchString);
                AttributeList list = server.getAttributes(objectName, new String[]{maxFdCount});
                for( Attribute attr : list.asList() ) if( attr.getName().equals(maxFdCount)) statsVisitor.saveOnce(this, attr.getValue().toString());
            } catch (IOException | MalformedObjectNameException | ReflectionException | InstanceNotFoundException e) {
                LOGGER.error("Error determining " + this.displayName(), e);
            }
        }
    }
    , LRMI_CONNECTIONS("lrmi_connections"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            final String threading = "java.lang:type=Threading";
            try {
                ObjectName objectName = new ObjectName(threading);
                MBeanServerConnection server = JMX_UTILS.mbeanServer(statsVisitor.virtualMachineDetails(), JmxUtils.OS_SEARCH_STRING);
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
                            Object threadName = threadInfo.get("threadName");
                            if( threadName == null ) continue;
                            if (threadName.toString().contains("LRMI Connection"))
                                lrmiThreadCount++;
                        }
                    }
                }
                statsVisitor.saveStat(this, String.valueOf(lrmiThreadCount));
            } catch (IOException | MalformedObjectNameException | ReflectionException | InstanceNotFoundException | AttributeNotFoundException | MBeanException e) {
                LOGGER.error("Error determining " + this.displayName(), e);
            }
        }
    }
    ;

    private static final JmxUtils JMX_UTILS = new JmxUtils();
    private static final Logger LOGGER = LoggerFactory.getLogger(OperatingSystemInfo.class);
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

enum JvmInfo implements NamedMetric {

    GARBAGE_COLLECTION_COUNT("gc_count"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            VirtualMachinesStatistics vmStatistics = statsVisitor.vmStatistics();
            if( vmStatistics == null ) return;
            Long gcCount = vmStatistics.getGcCollectionCount();
            if( gcCount == null ) return;
            statsVisitor.saveStat(this, gcCount.toString());
        }
    }
    , GARBAGE_COLLECTION_TIME_IN_SECONDS("gc_time_secs"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            VirtualMachinesStatistics vmStatistics = statsVisitor.vmStatistics();
            if( vmStatistics == null ) return;
            Long gcTime = vmStatistics.getGcCollectionTime();
            if( gcTime == null ) return;
            gcTime /= 1000l;
            statsVisitor.saveStat(this, gcTime.toString());
        }
    }
    , THREAD_COUNT("threads"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            VirtualMachinesStatistics vmStatistics = statsVisitor.vmStatistics();
            if( vmStatistics == null ) return;
            Integer threads = vmStatistics.getThreadCount();
            if( threads == null ) return;
            statsVisitor.saveStat(this, threads.toString());
        }
    }
    , JVM_UPTIME("jvm_uptime_secs"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            VirtualMachinesStatistics vmStatistics = statsVisitor.vmStatistics();
            if( vmStatistics == null ) return;
            Long upTime = vmStatistics.getUptime();
            if( upTime == null ) return;
            upTime /= 1000;
            statsVisitor.saveStat(this, upTime.toString());
        }
    }
    , JVM_CPU_LOAD("cpu_usage"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            VirtualMachineDetails vmDetails = statsVisitor.virtualMachineDetails();
            final String cpuLoad = "ProcessCpuLoad";
            try {
                ObjectName objectName = new ObjectName(JmxUtils.OS_SEARCH_STRING);
                MBeanServerConnection server = JMX_UTILS.mbeanServer(statsVisitor.virtualMachineDetails(), JmxUtils.OS_SEARCH_STRING);
                AttributeList list = server.getAttributes(objectName, new String[]{cpuLoad});
                for( Attribute attr : list.asList() ) if( attr.getName().equals(cpuLoad)) statsVisitor.saveStat(this, attr.getValue().toString());
            } catch (IOException | MalformedObjectNameException | ReflectionException | InstanceNotFoundException e) {
                LOGGER.error("Error determining " + this.displayName(), e);
            }
        }
    }
    ;

    private static final Logger LOGGER = LoggerFactory.getLogger(JvmInfo.class);
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

class JmxUtils{

    static final String OS_SEARCH_STRING = "java.lang:type=OperatingSystem";

    MBeanServerConnection mbeanServer(VirtualMachineDetails details, String queryString) throws IOException, MalformedObjectNameException {
        JMXServiceURL jmxUrl = new JMXServiceURL(details.getJmxUrl());
        JMXConnector connection = JMXConnectorFactory.newJMXConnector(jmxUrl, null);
        connection.connect(null);
        return connection.getMBeanServerConnection();
    }

}

enum Memory implements NamedMetric {

    TOTAL_BYTES("bytes_allocated") { // "allocated"; -Xmn <= allocated <= -Xmx
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            VirtualMachineDetails details = statsVisitor.virtualMachineDetails();
            if( details == null ) return;
            Long heap = details.getMemoryHeapMaxInBytes();
            Long nonHeap = details.getMemoryNonHeapInitInBytes();
            if( heap == null || nonHeap == null ) return;
            Long bytes = heap + nonHeap;
            statsVisitor.saveStat(this, bytes.toString());
        }
    }
    , NON_HEAP_USED_BYTES("non_heap_used_bytes") { // OS & JVM native
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            VirtualMachineDetails details = statsVisitor.virtualMachineDetails();
            if( details == null ) return;
            Long nonHeap = details.getMemoryNonHeapInitInBytes();
            if( nonHeap == null ) return;
            statsVisitor.saveStat(this, nonHeap.toString());
        }
    }
    , HEAP_USED_BYTES("heap_used_bytes") { // "working set" ~= application memory
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            VirtualMachineDetails details = statsVisitor.virtualMachineDetails();
            if( details == null ) return;
            Long heap = details.getMemoryHeapInitInBytes();
            if( heap == null ) return;
            statsVisitor.saveStat(this, heap.toString());
        }
    }
    , NON_HEAP_COMMITTED_BYTES("non_heap_committed_bytes") { // "JVM"; -Xmx minus HEAP_COMMITTED_BYTES
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            VirtualMachinesStatistics stats = statsVisitor.vmStatistics();
            Long nonHeapCommitted = stats.getMemoryNonHeapCommittedInBytes();
            if( nonHeapCommitted == null ) return;
            statsVisitor.saveStat(this, nonHeapCommitted.toString());
        }
    }
    , HEAP_COMMITTED_BYTES("heap_committed_bytes") { // "committed"; NON_HEAP_USED_BYTES + HEAP_USED_BYTES + garbage
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            VirtualMachinesStatistics stats = statsVisitor.vmStatistics();
            Long heapCommitted = stats.getMemoryHeapCommittedInBytes();
            if( heapCommitted == null ) return;
            statsVisitor.saveStat(this, heapCommitted.toString());
        }
    }
    ;

    private final String displayName;

    Memory(String displayName) {
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