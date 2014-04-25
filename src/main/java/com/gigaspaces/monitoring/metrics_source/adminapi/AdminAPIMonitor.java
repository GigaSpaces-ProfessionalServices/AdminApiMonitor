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
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AdminAPIMonitor implements Runnable{

    int pollingInterval = 5;

    @Value( "${spaceMonitor.adminUser}" )
    String adminUser;

    @Value( "${spaceMonitor.adminPassword}" )
    String adminPassword;

    @Value( "${spaceMonitor.secured}" )
    boolean secured = false;

    @Value( "${spaceMonitor.locators}" )
    String locators = null;

    ExponentialAverageCounter averageCounter;



    Map<Long,AverageStat> lastCollectedStat = new HashMap<Long, AverageStat>();


    public AdminAPIMonitor(){

    }

    public void startCollection(){
        System.out.println("HOLA!!!");
        new Thread(this).run();
    }

    @Override
    public void run() {
        AdminFactory factory = new AdminFactory();
        if(secured){
            factory.credentials(adminUser,adminPassword);
        }
        factory.addLocators(locators);
        factory.discoverUnmanagedSpaces();
        Admin admin = factory.createAdmin();
        Machines machines = admin.getMachines();
        machines.waitFor(1);
        GridServiceContainers gscs = admin.getGridServiceContainers();
        gscs.waitFor(1);
        Spaces spaces = admin.getSpaces();
        spaces.waitFor("mySpace");
        while(true){
            collectStats(admin);
            writeStats();
            try{
                Thread.sleep(pollingInterval * 1000);
            }catch(InterruptedException ie){
                ie.printStackTrace();
            }
        }
    }

    public void collectStats(Admin admin){
        collectJVMStats(admin);
        collectRedologStats(admin);
        collectMirrorStats(admin);
        collectActivityStats(admin);
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
        Space space = admin.getSpaces().waitFor("mySpace", 10, TimeUnit.SECONDS);
        space.waitFor(space.getNumberOfInstances(), SpaceMode.PRIMARY,10 , TimeUnit.SECONDS);
        SpacePartition partitions[]= space.getPartitions();
        long redologSize = 0;
        long redologBytesPerSecond = 0;
        for (int i=0;i<partitions.length;i++) {
            SpacePartition partition = partitions[i];
            redologSize += partition.getPrimary().getStatistics().getReplicationStatistics().
             getOutgoingReplication().getRedoLogSize();

            List<ReplicationStatistics.OutgoingChannel> channelList = partition.getPrimary().getStatistics().getReplicationStatistics().getOutgoingReplication().getChannels();
            for(ReplicationStatistics.OutgoingChannel channel : channelList){
                redologBytesPerSecond += channel.getSendBytesPerSecond();
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

    public void writeStats(){
        //TODO to be replaced to smth like "sendStatsToReporter"
        /*try{
            PrintWriter pw = new PrintWriter(new FileWriter(fileOutputPath,true));
            for(Stat stat : lastCollectedStat.values()){
                pw.println(stat.toCsv());
            }
            pw.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        */
    }

    public static void main(String args[]){
        System.setProperty("spaceMonitor.adminUser",System.getProperty("spaceMonitor.adminUser","deployer"));
        System.setProperty("spaceMonitor.adminPassword",System.getProperty("spaceMonitor.adminPassword","password"));
        System.setProperty("spaceMonitor.locators",System.getProperty("spaceMonitor.locators","localhost:4170"));
        System.setProperty("spaceMonitor.secured",System.getProperty("spaceMonitor.secured","true"));

        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("/META-INF/spring/pu.xml");
        AdminAPIMonitor spaceMonitor = (AdminAPIMonitor)appContext.getBean("spaceMonitor");
        System.out.println("HOLA!!!");
        while(true){}
        //The startCollection automatically runs

    }


    public void setPollingInterval(int pollingInterval) {
        this.pollingInterval = pollingInterval;
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

    @Required
    @Bean(autowire = Autowire.BY_TYPE)
    public void setAverageCounter(ExponentialAverageCounter averageCounter) {
        this.averageCounter = averageCounter;
    }
}
