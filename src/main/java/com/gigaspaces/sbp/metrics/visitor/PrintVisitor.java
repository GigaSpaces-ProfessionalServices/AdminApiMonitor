
package com.gigaspaces.sbp.metrics.visitor;

import com.gigaspaces.cluster.replication.async.mirror.MirrorStatistics;
import com.gigaspaces.sbp.metrics.NamedMetric;
import com.j_spaces.core.filters.ReplicationStatistics;
import org.openspaces.admin.Admin;
import org.openspaces.admin.gsc.GridServiceContainer;
import org.openspaces.admin.space.Space;
import org.openspaces.admin.space.SpaceInstance;
import org.openspaces.admin.space.SpacePartition;
import org.openspaces.admin.vm.VirtualMachineDetails;
import org.openspaces.admin.vm.VirtualMachineStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrintVisitor implements StatsVisitor {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private List<VirtualMachineDetails> vmDetails;

    private List<VirtualMachineStatistics> vmStatistics;

    private List<ReplicationStatistics> replicationStatistics;

    private List<MirrorStatistics> mirrorStatistics;

    private List<SpaceInstance> spaceInstances;

    //TODO lists
    public PrintVisitor(Admin admin, String spaceName){
        GridServiceContainer[] gridServiceContainers = admin.getGridServiceContainers().getContainers();

        List<VirtualMachineDetails> virtualMachineDetails = new ArrayList<VirtualMachineDetails>(gridServiceContainers.length);
        List<VirtualMachineStatistics> virtualMachineStatistics = new ArrayList<>(gridServiceContainers.length);

        for (GridServiceContainer gsc : gridServiceContainers){
            virtualMachineDetails.add(gsc.getVirtualMachine().getDetails());
            virtualMachineStatistics.add(gsc.getVirtualMachine().getStatistics());
        }

        vmDetails = virtualMachineDetails;
        vmStatistics = virtualMachineStatistics;

        Space targetSpace = admin.getSpaces().getSpaceByName(spaceName);

        replicationStatistics = new ArrayList<ReplicationStatistics>();
        mirrorStatistics = new ArrayList<MirrorStatistics>();
        for (SpacePartition partition : targetSpace.getPartitions()){
            replicationStatistics.add(partition.getPrimary().getStatistics().getReplicationStatistics());
            mirrorStatistics.add(partition.getPrimary().getStatistics().getMirrorStatistics());
        }

        spaceInstances = new ArrayList<SpaceInstance>();

        for (SpaceInstance spaceInstance : targetSpace.getInstances()){
            spaceInstances.add(spaceInstance);
        }
    }

    @Override
    public List<VirtualMachineDetails> virtualMachineDetails() {
        return vmDetails;
    }

    @Override
    public List<VirtualMachineStatistics> vmStatistics() {
        return vmStatistics;
    }

    @Override
    public List<MirrorStatistics> mirrorStatistics() {
        return mirrorStatistics;
    }

    @Override
    public List<ReplicationStatistics> replicationStatistics() {
        return replicationStatistics;
    }

    @Override
    public List<SpaceInstance> spaceInstance() {
        return spaceInstances;
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
