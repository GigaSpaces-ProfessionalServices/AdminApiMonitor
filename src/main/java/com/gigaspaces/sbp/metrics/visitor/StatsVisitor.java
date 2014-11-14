package com.gigaspaces.sbp.metrics.visitor;

import com.gigaspaces.cluster.replication.async.mirror.MirrorStatistics;
import com.gigaspaces.sbp.metrics.metric.FullMetric;
import com.gigaspaces.sbp.metrics.metric.NamedMetric;
import com.j_spaces.core.filters.ReplicationStatistics;
import org.openspaces.admin.Admin;
import org.openspaces.admin.gsc.GridServiceContainer;
import org.openspaces.admin.pu.ProcessingUnitInstance;
import org.openspaces.admin.space.SpaceInstance;
import org.openspaces.admin.vm.VirtualMachineDetails;
import org.openspaces.admin.vm.VirtualMachineStatistics;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 8/6/14
 * Time: 5:42 PM
 * Provides a source of statistics that metrics to which metrics can bind.
 */
public interface StatsVisitor {

    // STATE VARIABLES FROM WHICH TO EXTRACT METRIC DATA...

    List<GridServiceContainer> gridServiceContainers();

    List<VirtualMachineDetails> virtualMachineDetails();

    List<VirtualMachineStatistics> vmStatistics();

    List<ReplicationStatistics> replicationStatistics();

    List<MirrorStatistics> mirrorStatistics();

    List<SpaceInstance> spaceInstance();

    List<ProcessingUnitInstance> processingUnitInstances();

    Map<String, Integer> alerts();

    Admin admin();

    // For possible, future use...
    //    SpaceStatistics spaceStatistics();
    //    SpaceInstanceStatistics instanceStatistics();

    /**
     * Save off a statistic for safe-keeping. May be reported now or later, at the
     * Visitor's discretion
     * @param fullMetric the metric
     */
    void saveStat(FullMetric fullMetric);

    /**
     * Determine if a system-wide metric has been saved already
     * @param metric if a named metric has been saved in a chunk of system-wide state
     * @return if so
     */
    boolean isSavedOnce(NamedMetric metric);

    /**
     * If this metric hasn't been saved in a system-wide manner yet, save it
     * @param fullMetric the metric
     */
    void saveOnce(FullMetric fullMetric);

}