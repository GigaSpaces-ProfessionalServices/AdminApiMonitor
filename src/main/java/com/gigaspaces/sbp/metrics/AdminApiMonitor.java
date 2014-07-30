package com.gigaspaces.sbp.metrics;

import com.gigaspaces.cluster.activeelection.SpaceMode;
import com.gigaspaces.cluster.replication.async.mirror.MirrorStatistics;
import com.gigaspaces.sbp.metrics.cli.ProcessArgs;
import com.j_spaces.core.filters.ReplicationStatistics;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.gsc.GridServiceContainer;
import org.openspaces.admin.gsc.GridServiceContainers;
import org.openspaces.admin.machine.Machines;
import org.openspaces.admin.space.*;
import org.openspaces.admin.vm.VirtualMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class AdminApiMonitor {

    private static final String APPLICATION_CONTEXT_PATH = "/META-INF/spring/admin-api-context.xml";
    private static final int WAITING_FOR_GRID_PAUSE = 5000;
    private static final int TERMINAL_WIDTH = 110;

    private static boolean applicationContextStarted;
    private static EnumSet<Settings> settings;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Admin admin;
    private String adminUser;
    private String adminPassword;
    private String locators;
    private String groups;
    private String spaceName;

    private ExponentialMovingAverage averageCounter;

    private Map<Long,AverageStat> lastCollectedStat = new HashMap<>();

    private String vmName;

    public Map<Long,AverageStat> startCollection(){
        AdminFactory factory = new AdminFactory();
        if(settings.contains(Settings.Secured)){
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
        collectJvmStats(admin);
        collectRedoLogStats(admin);
        collectMirrorStats(admin);
        collectActivityStats(admin);
    }

    // TODO provide smarter solution, below method is hotfix for Belk
    public Long getMemoryUsed(){
        long memoryHeapUsedInBytes = 0;
        try {
            GridServiceContainer containers[] = admin.getGridServiceContainers().getContainers();
            for (GridServiceContainer container : containers){
                memoryHeapUsedInBytes += container.getVirtualMachine().getStatistics().getMemoryHeapUsedInBytes();
            }
        }   catch (Exception e) {
            logger.debug("Error reading used memory.", e);
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
            logger.debug("Error reading object count.", e);
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
            logger.debug("Error calculating throughput.", e);
        }
        return throughput;
    }

    public void init(){

        AdminFactory factory = new AdminFactory();
        if(settings.contains(Settings.Secured)){
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

        // TODO I'm pretty sure this is wrong
        Spaces spaces = admin.getSpaces();
        spaces.waitFor(spaceName);
        GridServiceContainer containers[] = admin.getGridServiceContainers().getContainers();
        vmName = containers[0].getVirtualMachine().getStatistics().getDetails().getUid();
    }

    public void collectJvmStats(Admin admin){
        GridServiceContainer containers[] = admin.getGridServiceContainers().getContainers();
        for (GridServiceContainer container : containers) {
            VirtualMachine vm = container.getVirtualMachine();
            AverageStat stat = lastCollectedStat.get(vm.getDetails().getPid());
            if (stat == null) {
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
            lastCollectedStat.put(vm.getDetails().getPid(), stat);
        }
    }

    public void collectRedoLogStats(Admin admin){
        Space space = admin.getSpaces().waitFor("testSpace", 3, TimeUnit.SECONDS);
        space.waitFor(space.getNumberOfInstances(), SpaceMode.PRIMARY,10 , TimeUnit.SECONDS);
        SpacePartition partitions[]= space.getPartitions();
        long redoLogSize = 0;
        long redoLogBytesPerSecond = 0;
        for (SpacePartition partition : partitions) {
            ReplicationStatistics replicationStatistics = partition.getPrimary().getStatistics().getReplicationStatistics();

            if (replicationStatistics != null) {
                redoLogSize += replicationStatistics.getOutgoingReplication().getRedoLogSize();

                List<ReplicationStatistics.OutgoingChannel> channelList = replicationStatistics.getOutgoingReplication().getChannels();
                for (ReplicationStatistics.OutgoingChannel channel : channelList) {
                    redoLogBytesPerSecond += channel.getSendBytesPerSecond();
                }
            }

        }
        for(Long pid : lastCollectedStat.keySet()){
            AverageStat stat = lastCollectedStat.get(pid);
            stat.redologSize = averageCounter.average(stat.redologSize, redoLogSize);
            stat.redologSendBytesPerSecond = averageCounter.average(stat.redologSendBytesPerSecond, redoLogBytesPerSecond);
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

        ProcessArgs processArgs = new ProcessArgs();

        try {
            settings = processArgs.invoke(args);
        } catch (ParseException e) {
            final PrintWriter writer = new PrintWriter(System.err);
            final HelpFormatter usageFormatter = new HelpFormatter();
            usageFormatter.printUsage(writer, TERMINAL_WIDTH, AdminApiMonitor.class.getSimpleName(), processArgs.getOptions());
            writer.flush();
        }

        while (!applicationContextStarted)
            attemptStart();

    }

    private static void attemptStart() throws InterruptedException {
        try {
            startApplicationContext();
            applicationContextStarted = true;
        }  catch (BeanCreationException e){
            System.out.println("===================================================");
            System.out.println("Unable to start " + AdminApiMonitor.class.getSimpleName() + ". Retrying in " + WAITING_FOR_GRID_PAUSE / 1000 + " seconds.");
            System.out.println("===================================================");
            Thread.sleep(WAITING_FOR_GRID_PAUSE);
        }
    }

    private static void startApplicationContext(){
        new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_PATH);
    }

    public void setAdminUser(String adminUser) {
        this.adminUser = adminUser;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
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
    public void setAverageCounter(ExponentialMovingAverage averageCounter) {
        this.averageCounter = averageCounter;
    }

    public String getVmName() {
        return vmName;
    }

}