
package com.gigaspaces.sbp.metrics.visitor;

import com.gigaspaces.cluster.replication.async.mirror.MirrorStatistics;
import com.gigaspaces.sbp.metrics.NamedMetric;
import com.gigaspaces.sbp.metrics.visitor.StatsVisitor;
import com.j_spaces.core.filters.ReplicationStatistics;
import org.openspaces.admin.Admin;
import org.openspaces.admin.gsc.GridServiceContainer;
import org.openspaces.admin.space.SpaceInstance;
import org.openspaces.admin.vm.VirtualMachineDetails;
import org.openspaces.admin.vm.VirtualMachineStatistics;
import org.openspaces.admin.vm.VirtualMachinesStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PrintVisitor implements StatsVisitor {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private VirtualMachineDetails virtualMachineDetails;

    private VirtualMachineStatistics vmStatistics;

    private ReplicationStatistics replicationStatistics;

    private MirrorStatistics mirrorStatistics;

    private SpaceInstance spaceInstance;

    //TODO lists
    public PrintVisitor(Admin admin){
        GridServiceContainer gridServiceContainer = admin.getGridServiceContainers().getContainers()[0];
        virtualMachineDetails = gridServiceContainer.getVirtualMachine().getDetails();
        vmStatistics = gridServiceContainer.getVirtualMachine().getStatistics();
        replicationStatistics = admin.getSpaces().getSpaceByName("belkSpikes").getPartitions()[0].getPrimary().getStatistics().getReplicationStatistics();
        mirrorStatistics = admin.getSpaces().getSpaceByName("belkSpikes").getPartitions()[0].getPrimary().getStatistics().getMirrorStatistics();
        spaceInstance = admin.getSpaces().getSpaceByName("belkSpikes").getInstances()[0];
    }

    @Override
    public VirtualMachineDetails virtualMachineDetails() {
        return virtualMachineDetails;
    }

    @Override
    public VirtualMachineStatistics vmStatistics() {
        return vmStatistics;
    }

    @Override
    public MirrorStatistics mirrorStatistics() {
        return mirrorStatistics;
    }

    @Override
    public ReplicationStatistics replicationStatistics() {
        return replicationStatistics;
    }

    @Override
    public SpaceInstance spaceInstance() {
        return spaceInstance;
    }

    @Override
    public void saveStat(NamedMetric metric, String value) {
        logger.info(formatMetrics(metric.displayName(), value));
    }

    @Override
    public boolean isSavedOnce(NamedMetric metric) {
        return false;
    }

    @Override
    public void saveOnce(NamedMetric metric, String value) {

    }

    private String formatMetrics(String metricType, String metricValue){
        SimpleDateFormat date = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        String serverName = "TODO SERVER NAME";
        return "\n" + date.format(new Date()) + " -- " + metricType + "-- " + serverName +  " -- " + "TODO VM NAME" + " -- cacheNum — " + metricValue;
    }
}
