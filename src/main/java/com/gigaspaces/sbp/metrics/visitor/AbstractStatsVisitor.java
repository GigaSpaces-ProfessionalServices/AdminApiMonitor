package com.gigaspaces.sbp.metrics.visitor;

import com.gigaspaces.cluster.replication.async.mirror.MirrorStatistics;
import com.gigaspaces.grid.gsc.GSC;
import com.gigaspaces.sbp.metrics.NamedMetric;
import com.j_spaces.core.filters.ReplicationStatistics;
import org.openspaces.admin.Admin;
import org.openspaces.admin.gsc.GridServiceContainer;
import org.openspaces.admin.space.Space;
import org.openspaces.admin.space.SpaceInstance;
import org.openspaces.admin.space.SpacePartition;
import org.openspaces.admin.vm.VirtualMachineDetails;
import org.openspaces.admin.vm.VirtualMachineStatistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public abstract class AbstractStatsVisitor implements StatsVisitor {

    protected List<GridServiceContainer> gridServiceContainers;

    protected List<VirtualMachineDetails> vmDetails;

    protected List<VirtualMachineStatistics> vmStatistics;

    protected List<ReplicationStatistics> replicationStatistics;

    protected List<MirrorStatistics> mirrorStatistics;

    protected List<SpaceInstance> spaceInstances;

    protected Set<NamedMetric> savedMetrics;

    protected AbstractStatsVisitor(Admin admin, String spaceName){
        gridServiceContainers = Arrays.asList(admin.getGridServiceContainers().getContainers());
        Space targetSpace = admin.getSpaces().getSpaceByName(spaceName);
        vmDetails = new ArrayList<>(gridServiceContainers.size());
        vmStatistics = new ArrayList<>(gridServiceContainers.size());
        replicationStatistics = new ArrayList<>();
        mirrorStatistics = new ArrayList<>();
        spaceInstances = Arrays.asList(targetSpace.getInstances());
        for (GridServiceContainer gsc : gridServiceContainers){
            vmDetails.add(gsc.getVirtualMachine().getDetails());
            vmStatistics.add(gsc.getVirtualMachine().getStatistics());
        }
        for (SpacePartition partition : targetSpace.getPartitions()){
            replicationStatistics.add(partition.getPrimary().getStatistics().getReplicationStatistics());
            mirrorStatistics.add(partition.getPrimary().getStatistics().getMirrorStatistics());
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
    public List<ReplicationStatistics> replicationStatistics() {
        return replicationStatistics;
    }

    @Override
    public List<MirrorStatistics> mirrorStatistics() {
        return mirrorStatistics;
    }

    @Override
    public List<SpaceInstance> spaceInstance() {
        return spaceInstances;
    }

    @Override
    public List<GridServiceContainer> gridServiceContainers() {
        return gridServiceContainers;
    }
}
