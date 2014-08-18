package com.gigaspaces.sbp.metrics.visitor;

import com.gigaspaces.cluster.replication.async.mirror.MirrorStatistics;
import com.gigaspaces.sbp.metrics.NamedMetric;
import com.j_spaces.core.filters.ReplicationStatistics;
import org.openspaces.admin.space.SpaceInstance;
import org.openspaces.admin.vm.VirtualMachineDetails;
import org.openspaces.admin.vm.VirtualMachinesStatistics;

/**
 * Created by ubuntu on 8/18/14.
 */
public class CsvVisitor implements StatsVisitor{
    @Override
    public VirtualMachineDetails virtualMachineDetails() {
        return null;
    }

    @Override
    public VirtualMachinesStatistics vmStatistics() {
        return null;
    }

    @Override
    public ReplicationStatistics replicationStatistics() {
        return null;
    }

    @Override
    public MirrorStatistics mirrorStatistics() {
        return null;
    }

    @Override
    public SpaceInstance spaceInstance() {
        return null;
    }

    @Override
    public void saveStat(NamedMetric metric, String value) {

    }

    @Override
    public boolean isSavedOnce(NamedMetric metric) {
        return false;
    }

    @Override
    public void saveOnce(NamedMetric metric, String value) {

    }
}
