package com.gigaspaces.sbp.metrics;

import com.gigaspaces.cluster.replication.async.mirror.MirrorStatistics;
import com.j_spaces.core.filters.ReplicationStatistics;
import org.openspaces.admin.space.SpaceInstance;
import org.openspaces.admin.vm.VirtualMachineDetails;
import org.openspaces.admin.vm.VirtualMachinesStatistics;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 8/6/14
 * Time: 5:42 PM
 * Provides a source of statistics that metrics to which metrics can bind.
 */
public interface StatsVisitor {

    // STATE VARIABLES FROM WHICH TO EXTRACT METRIC DATA...

    VirtualMachineDetails virtualMachineDetails();

    VirtualMachinesStatistics vmStatistics();

    ReplicationStatistics replicationStatistics();

    MirrorStatistics mirrorStatistics();

    SpaceInstance spaceInstance();

    // For possible, future use...
    //    SpaceStatistics spaceStatistics();
    //    SpaceInstanceStatistics instanceStatistics();


    /**
     * Save off a statistic for safe-keeping. May be reported now or later, at the
     * Visitor's discretion
     * @param metric name of the metric
     * @param value value of it
     */
    void saveStat(NamedMetric metric, String value);

    /**
     * Determine if a system-wide metric has been saved already
     * @param metric if a named metric has been saved in a chunk of system-wide state
     * @return if so
     */
    boolean isSavedOnce(NamedMetric metric);

    /**
     * If this metric hasn't been saved in a system-wide manner yet, save it
     * @param metric thing to save
     * @param value value to save for it
     */
    void saveOnce(NamedMetric metric, String value);

}