package com.gigaspaces.sbp.metrics_source.adminapi;

import com.gigaspaces.cluster.activeelection.SpaceMode;
import com.gigaspaces.cluster.replication.async.mirror.MirrorStatistics;
import com.gigaspaces.sbp.metrics_reporter.CollectPeriodicAverageMetricsTask;
import com.gigaspaces.sbp.metrics_source.counter.ExponentialAverageCounter;
import com.j_spaces.core.filters.ReplicationStatistics;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.gsc.GridServiceContainer;
import org.openspaces.admin.gsc.GridServiceContainers;
import org.openspaces.admin.machine.Machines;
import org.openspaces.admin.space.*;
import org.openspaces.admin.vm.VirtualMachine;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AdminAPIMonitor {

    private Admin admin;

    private String adminUser;

    private String adminPassword;

    private boolean secured = false;

    private String locators = null;

    private String groups = null;

    private String spaceName = null;

    private ExponentialAverageCounter averageCounter;

    private Map<Long,AverageStat> lastCollectedStat = new HashMap<Long, AverageStat>();

    private String vmName;

    public AdminAPIMonitor(){

    }

    public Map<Long,AverageStat> startCollection(){
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
        spaces.waitFor(spaceName);
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
            GridServiceContainer containers[] = admin.getGridServiceContainers().getContainers();
            for (GridServiceContainer container : containers){
                memoryHeapUsedInBytes += container.getVirtualMachine().getStatistics().getMemoryHeapUsedInBytes();
            }
        }   catch (Exception e) {
        }
        return memoryHeapUsedInBytes;
    }

    public Long getObjectsCount(){
        long objectsCount = 0;
        try {
            Spaces spaces = admin.getSpaces();
            Space space = spaces.getSpaceByName(spaceName);
            for (SpaceInstance spaceInstance : space) {
                SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                objectsCount = stats.getObjectCount();
            }
        }   catch (Exception e) {
        }
        return objectsCount;
    }

    public Long getThroughput(){
        long throughput = 0;
        try {
            Spaces spaces = admin.getSpaces();
            Space space = spaces.getSpaceByName(spaceName);
            for (SpaceInstance spaceInstance : space) {
                SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                throughput = stats.getActiveTransactionCount();
            }
        }   catch (Exception e) {
        }
        return throughput;
    }

    public void init(){
        AdminFactory factory = new AdminFactory();
        if(secured){
            factory.credentials(adminUser,adminPassword);
        }
        factory.addLocators(locators);
        factory.addGroups(groups);
        factory.discoverUnmanagedSpaces();
        admin = factory.createAdmin();

        Machines machines = admin.getMachines();
        machines.waitFor(1);
        GridServiceContainers gscs = admin.getGridServiceContainers();

        gscs.waitFor(1, 500, TimeUnit.MILLISECONDS);

        Spaces spaces = admin.getSpaces();
        spaces.waitFor(spaceName);
        GridServiceContainer containers[] = admin.getGridServiceContainers().getContainers();
        vmName = containers[0].getVirtualMachine().getStatistics().getDetails().getUid();
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

    public static void main(String[] args) throws InterruptedException {
        boolean applicationContextStarted = false;
        while (!applicationContextStarted){
            try {
                startApplicationContext(args);
                applicationContextStarted = true;
            }  catch (BeanCreationException e){
                System.out.println("===================================================");
                System.out.println("Unable to start AdminAPIMonitor");
                System.out.println("===================================================");
                Thread.sleep(5000);
            }
        }

    }

    private static void startApplicationContext(String[] args){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/META-INF/spring/admin-api-context.xml");
        CollectPeriodicAverageMetricsTask collectPeriodicMetricsTask = (CollectPeriodicAverageMetricsTask) applicationContext.getBean("collectPeriodicMetricsTask");
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

    public String getVmName() {
        return vmName;
    }
}