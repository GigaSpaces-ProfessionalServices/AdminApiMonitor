package com.gigaspaces.monitoring.metrics_source.adminapi;

import com.gigaspaces.cluster.activeelection.SpaceMode;
import com.gigaspaces.cluster.replication.async.mirror.MirrorStatistics;
import com.gigaspaces.monitoring.metrics_source.counter.ExponentialAverageCounter;
import com.j_spaces.core.filters.ReplicationStatistics;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.gsc.GridServiceContainer;
import org.openspaces.admin.gsc.GridServiceContainers;
import org.openspaces.admin.machine.Machines;
import org.openspaces.admin.space.*;
import org.openspaces.admin.vm.VirtualMachine;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AdminAPIMonitor {

   // @Value( "${spaceMonitor.adminUser}" )
    private String adminUser;

    //@Value( "${spaceMonitor.adminPassword}" )
    private String adminPassword;

    //@Value( "${spaceMonitor.secured}" )
    private boolean secured = false;

    //@Value( "${spaceMonitor.locators}" )
    private String locators = null;

    private String groups = null;

    private String spaceName = null;

    private ExponentialAverageCounter averageCounter;

    private Map<Long,AverageStat> lastCollectedStat = new HashMap<Long, AverageStat>();

    public AdminAPIMonitor(){

    }

    public Map<Long,AverageStat> startCollection(){
        AdminFactory factory = new AdminFactory();
        if(secured){
            factory.credentials(adminUser,adminPassword);
        }
        factory.addLocators(locators);
        factory.addGroup("test");
        factory.discoverUnmanagedSpaces();
        Admin admin = factory.createAdmin();

        Machines machines = admin.getMachines();
        machines.waitFor(1);
        GridServiceContainers gscs = admin.getGridServiceContainers();

      // TODO check (how to start GSC from java?)
        gscs.waitFor(1, 500, TimeUnit.MILLISECONDS);

        Spaces spaces = admin.getSpaces();
        spaces.waitFor("testSpace"); //TODO replace by configurable space name
        collectStats(admin);
        return lastCollectedStat;
    }

    public void collectStats(Admin admin){
        collectJVMStats(admin);
        collectRedologStats(admin);
        collectMirrorStats(admin);
        collectActivityStats(admin);
    }

    //TODO provide smarter solution, below method is hotfix for Belk
    public Long getMemoryUsed(){
        long memoryHeapUsedInBytes = 0;
        try {
            AdminFactory factory = new AdminFactory();
            if(secured){
                factory.credentials(adminUser,adminPassword);
            }
            factory.addLocators(locators);
            factory.addGroups(groups);
            factory.discoverUnmanagedSpaces();
            Admin admin = factory.createAdmin();

            Machines machines = admin.getMachines();
            machines.waitFor(1);
            GridServiceContainers gscs = admin.getGridServiceContainers();

            // TODO check (how to start GSC from java?)
         gscs.waitFor(1, 500, TimeUnit.MILLISECONDS);

            Spaces spaces = admin.getSpaces();
            spaces.waitFor(spaceName); //TODO replace by configurable space name
            //collectStats(admin);
            //return lastCollectedStat;
            GridServiceContainer containers[] = admin.getGridServiceContainers().getContainers();
            memoryHeapUsedInBytes = containers[0].getVirtualMachine().getStatistics().getMemoryHeapUsedInBytes();
        }   catch (Exception e) {

        }
        return memoryHeapUsedInBytes;
    }

    public void collectJVMStats(Admin admin){
        GridServiceContainer containers[] = admin.getGridServiceContainers().getContainers();
        for(int i=0;i<containers.length;i++){
            VirtualMachine vm = containers[i].getVirtualMachine();
            AverageStat stat = lastCollectedStat.get(vm.getDetails().getPid());
            if(stat == null){
                stat = new AverageStat();
                stat.pid = vm.getDetails().getPid();
            }
            stat.totalMemory = averageCounter.average(stat.totalMemory, vm.getDetails().getMemoryHeapMaxInBytes());
            stat.heapUsedMemory = averageCounter.average(stat.heapUsedMemory, vm.getStatistics().getMemoryHeapUsedInBytes());
            stat.cpuPercent = averageCounter.average(stat.cpuPercent, vm.getStatistics().getCpuPerc());
            stat.gcCollectionCount = vm.getStatistics().getGcCollectionCount();
            stat.hostname = vm.getMachine().getHostName();
            stat.totalThreads = averageCounter.average(stat.totalThreads, vm.getStatistics().getThreadCount());
            stat.nonHeapUsedMemory = averageCounter.average(stat.nonHeapUsedMemory, vm.getStatistics().getMemoryNonHeapUsedInBytes());
            stat.timestamp = new Date();
            lastCollectedStat.put(vm.getDetails().getPid(),stat);
        }
    }
    public void collectRedologStats(Admin admin){
        Space space = admin.getSpaces().waitFor("testSpace", 3, TimeUnit.SECONDS);
        space.waitFor(space.getNumberOfInstances(), SpaceMode.PRIMARY,10 , TimeUnit.SECONDS);
        SpacePartition partitions[]= space.getPartitions();
        long redologSize = 0;
        long redologBytesPerSecond = 0;
        for (int i=0;i<partitions.length;i++) {
            SpacePartition partition = partitions[i];

            ReplicationStatistics replicationStatistics = partition.getPrimary().getStatistics().getReplicationStatistics();

            if (replicationStatistics != null){
                redologSize += replicationStatistics.getOutgoingReplication().getRedoLogSize();

                List<ReplicationStatistics.OutgoingChannel> channelList = replicationStatistics.getOutgoingReplication().getChannels();
                for(ReplicationStatistics.OutgoingChannel channel : channelList){
                    redologBytesPerSecond += channel.getSendBytesPerSecond();
                }
            }

        }
        for(Long pid : lastCollectedStat.keySet()){
            AverageStat stat = lastCollectedStat.get(pid);
            stat.redologSize = averageCounter.average(stat.redologSize, redologSize);
            stat.redologSendBytesPerSecond = averageCounter.average(stat.redologSendBytesPerSecond, redologBytesPerSecond);
        }
    }
    public void collectMirrorStats(Admin admin){
        long mirrorTotalOperations=0;
        long mirrorSuccessfulOperations=0;
        long mirrorFailedOperations=0;

        for (Space space : admin.getSpaces()) {
            for (SpaceInstance spaceInstance : space) {
                MirrorStatistics mirrorStat = spaceInstance.getStatistics().getMirrorStatistics();
                 // check if this instance is mirror
                 if(mirrorStat != null)
                 {
                    mirrorTotalOperations= mirrorStat.getOperationCount();
                    mirrorSuccessfulOperations = mirrorStat.getSuccessfulOperationCount();
                    mirrorFailedOperations = mirrorStat.getFailedOperationCount();
                 }
            }

        }
        for(Long pid : lastCollectedStat.keySet()){
            AverageStat stat = lastCollectedStat.get(pid);
            //TODO check do we need EAC calculation here?
            stat.mirrorTotalOperations = mirrorTotalOperations;
            stat.mirrorSuccessfulOperations = mirrorSuccessfulOperations;
            stat.mirrorFailedOperations = mirrorFailedOperations;

        }
    }

    public void collectActivityStats(Admin admin){
        double readCountPerSecond = 0;
        double updateCountPerSecond = 0;
        double writeCountPerSecond = 0;
        double changePerSecond = 0;
        double executePerSecond = 0;
        int processorQueueSize = 0;
        long activeTransactionCount = 0;

            for (Space space : admin.getSpaces()) {
                for (SpaceInstance spaceInstance : space) {
                    SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                    readCountPerSecond += stats.getReadPerSecond();
                    updateCountPerSecond += stats.getUpdatePerSecond();
                    writeCountPerSecond += stats.getWritePerSecond();
                    changePerSecond += stats.getChangePerSecond();
                    executePerSecond += stats.getExecutePerSecond();
                    processorQueueSize += stats.getProcessorQueueSize();
                    activeTransactionCount += stats.getActiveTransactionCount();
                }
            }
            for(Long pid : lastCollectedStat.keySet()){
                AverageStat stat = lastCollectedStat.get(pid);
                stat.readCountPerSecond = averageCounter.average(stat.readCountPerSecond, readCountPerSecond);
                stat.updateCountPerSecond = averageCounter.average(stat.updateCountPerSecond, updateCountPerSecond);
                stat.writeCountPerSecond = averageCounter.average(stat.writeCountPerSecond, writeCountPerSecond);
                stat.changePerSecond = averageCounter.average(stat.changePerSecond, changePerSecond);
                stat.executePerSecond = averageCounter.average(stat.executePerSecond, executePerSecond);
                stat.processorQueueSize = averageCounter.average(stat.processorQueueSize, processorQueueSize);
                stat.activeTransactionCount = averageCounter.average(stat.activeTransactionCount, activeTransactionCount);
            }
        }

    public String getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(String adminUser) {
        this.adminUser = adminUser;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public void setSecured(String secured) {
        this.secured = Boolean.valueOf(secured);
    }

    public void setLocators(String locators) {
        this.locators = locators;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    @Required
    public void setAverageCounter(ExponentialAverageCounter averageCounter) {
        this.averageCounter = averageCounter;
    }

    public Map<Long, AverageStat> getLastCollectedStat() {
        return lastCollectedStat;
    }

}
