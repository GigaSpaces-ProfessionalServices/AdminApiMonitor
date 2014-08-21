package com.gigaspaces.sbp.metrics;

import com.gigaspaces.cluster.activeelection.SpaceMode;
import com.gigaspaces.cluster.replication.async.mirror.MirrorStatistics;
import com.gigaspaces.sbp.metrics.cli.ProcessArgs;
import com.gigaspaces.sbp.metrics.visitor.PrintVisitor;
import com.gigaspaces.sbp.metrics.visitor.StatsVisitor;
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

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static boolean applicationContextStarted;
    private static EnumSet<Settings> settings; // from command line

    // GigaSpaces configuration elements (from context.xml or properties file)
    private Admin admin;
    private String adminUser;
    private String adminPassword;
    private String locators;
    private String groups;
    private String spaceName;

    // Fields to hold data that we'll report on
    private ExponentialMovingAverage averageCounter;
    private Map<Long,AdminApiMetrics> pidMetricMap = new HashMap<>();
    private String vmName;

    public Admin getAdmin() {
        return admin;
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

    /**
     * Called from spring context
     * @param admin on which to collect statistics
     */
    public void collectStats(Admin admin){
        collectJvmStats(admin);
        collectRedoLogStats(admin);
        collectMirrorStats(admin);
        collectActivityStats(admin);
    }

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

    public void collectJvmStats(Admin admin){
        GridServiceContainer containers[] = admin.getGridServiceContainers().getContainers();
        for (GridServiceContainer container : containers) {
            VirtualMachine vm = container.getVirtualMachine();
            AdminApiMetrics metrics = pidMetricMap.get(vm.getDetails().getPid());
            if (metrics == null) {
                metrics = new AdminApiMetrics();
                metrics.pid = vm.getDetails().getPid();
            }
            metrics.totalMemoryInBytes = averageCounter.average(metrics.totalMemoryInBytes.doubleValue(), vm.getDetails().getMemoryHeapMaxInBytes()).longValue();
            metrics.heapUsedMemoryInBytes = averageCounter.average(metrics.heapUsedMemoryInBytes.doubleValue(), vm.getStatistics().getMemoryHeapUsedInBytes()).longValue();
            metrics.cpuPercent = averageCounter.average(metrics.cpuPercent, vm.getStatistics().getCpuPerc());
            metrics.gcCollectionCount = vm.getStatistics().getGcCollectionCount();
            metrics.hostname = vm.getMachine().getHostName();
            metrics.totalThreads = averageCounter.average(metrics.totalThreads.floatValue(), vm.getStatistics().getThreadCount()).doubleValue();
            metrics.nonHeapUsedMemoryInBytes = averageCounter.average(metrics.nonHeapUsedMemoryInBytes.doubleValue(), vm.getStatistics().getMemoryNonHeapUsedInBytes()).longValue();
            metrics.timestamp = new Date();
            pidMetricMap.put(vm.getDetails().getPid(), metrics);
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
        for(Long pid : pidMetricMap.keySet()){
            AdminApiMetrics stat = pidMetricMap.get(pid);
            stat.redoLogSize = averageCounter.average(stat.redoLogSize, redoLogSize);
            stat.redoLogSendBytesPerSecond = averageCounter.average(stat.redoLogSendBytesPerSecond, redoLogBytesPerSecond);
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
        for(Long pid : pidMetricMap.keySet()){
            AdminApiMetrics stat = pidMetricMap.get(pid);
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
        long processorQueueSize = 0;
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

        for(Long pid : pidMetricMap.keySet()){
            AdminApiMetrics metrics = pidMetricMap.get(pid);
            metrics.readCountPerSecond = averageCounter.average(metrics.readCountPerSecond.doubleValue(), readCountPerSecond).longValue();
            metrics.updateCountPerSecond = averageCounter.average(metrics.updateCountPerSecond.doubleValue(), updateCountPerSecond).longValue();
            metrics.writeCountPerSecond = averageCounter.average(metrics.writeCountPerSecond.doubleValue(), writeCountPerSecond).longValue();
            metrics.changePerSecond = averageCounter.average(metrics.changePerSecond.doubleValue(), changePerSecond).longValue();
            metrics.executePerSecond = averageCounter.average(metrics.executePerSecond.doubleValue(), executePerSecond).longValue();
            metrics.processorQueueSize = averageCounter.average(metrics.processorQueueSize.doubleValue(), processorQueueSize).longValue();
            metrics.activeTransactionCount = averageCounter.average(metrics.activeTransactionCount.doubleValue(), activeTransactionCount).longValue();
        }
    }

    public static void main(String[] args) throws InterruptedException {

        ProcessArgs processArgs = new ProcessArgs();

        try {
            settings = processArgs.invoke(args);
        } catch (ParseException e) {
            System.err.println("ERROR: User error. Please try again...");
            final PrintWriter writer = new PrintWriter(System.err);
            final HelpFormatter usageFormatter = new HelpFormatter();
            usageFormatter.printUsage(writer, TERMINAL_WIDTH, "java -DjavaOpt=foo -jar " + AdminApiMonitor.class.getSimpleName() + ".jar", processArgs.getOptions());
            writer.flush();
            System.exit(666);
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