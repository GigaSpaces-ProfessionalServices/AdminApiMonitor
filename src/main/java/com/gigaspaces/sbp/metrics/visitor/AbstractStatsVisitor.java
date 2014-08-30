package com.gigaspaces.sbp.metrics.visitor;

import com.gigaspaces.cluster.replication.async.mirror.MirrorStatistics;
import com.gigaspaces.sbp.metrics.*;
import com.gigaspaces.sbp.metrics.metric.*;
import com.j_spaces.core.filters.ReplicationStatistics;
import org.openspaces.admin.Admin;
import org.openspaces.admin.gsc.GridServiceContainer;
import org.openspaces.admin.space.Space;
import org.openspaces.admin.space.SpaceInstance;
import org.openspaces.admin.space.SpacePartition;
import org.openspaces.admin.vm.VirtualMachineDetails;
import org.openspaces.admin.vm.VirtualMachineStatistics;

import java.util.*;

import static java.util.Arrays.*;

public abstract class AbstractStatsVisitor implements StatsVisitor {

    protected List<GridServiceContainer> gridServiceContainers;

    protected List<VirtualMachineDetails> vmDetails;

    protected List<VirtualMachineStatistics> vmStatistics;

    protected List<ReplicationStatistics> replicationStatistics;

    protected List<MirrorStatistics> mirrorStatistics;

    protected List<SpaceInstance> spaceInstances;

    protected Set<NamedMetric> savedMetrics;

    protected Map<Long, Map<NamedMetric, String>> pidMetricMap;

    protected ExponentialMovingAverage average;

    protected List<NamedMetric> emas = asList(new NamedMetric[]{GigaSpacesActivity.READ_PER_SEC,
            GigaSpacesActivity.WRITES_PER_SEC,
            GigaSpacesActivity.TAKES_PER_SECOND,
            GigaSpacesActivity.UPDATES_PER_SEC,
            GigaSpacesActivity.EXECUTES_PER_SEC, GigaSpacesActivity.TRANSACTION_COUNT,
            GsMirrorInfo.REDO_LOG_SIZE, GsMirrorInfo.REDO_LOG_SEND_BYTES_PER_SECOND,
            JvmInfo.THREAD_COUNT, JvmInfo.JVM_CPU_LOAD,
            Memory.TOTAL_BYTES, Memory.HEAP_USED_BYTES, Memory.HEAP_COMMITTED_BYTES, Memory.NON_HEAP_USED_BYTES, Memory.NON_HEAP_COMMITTED_BYTES,
            OperatingSystemInfo.LRMI_CONNECTIONS
    });


    protected AbstractStatsVisitor(Admin admin, List<String> spaceNames, Map<Long, Map<NamedMetric, String>> pidMetricMap, ExponentialMovingAverage average) {
        this.pidMetricMap = pidMetricMap;
        this.average = average;
        //whole grid
        gridServiceContainers = asList(admin.getGridServiceContainers().getContainers());

        vmDetails = new ArrayList<>(gridServiceContainers.size());
        vmStatistics = new ArrayList<>(gridServiceContainers.size());
        replicationStatistics = new ArrayList<>();
        mirrorStatistics = new ArrayList<>();
        spaceInstances = new ArrayList<>();

        // separate spaces
        for (String spaceName : spaceNames){
            Space targetSpace = admin.getSpaces().getSpaceByName(spaceName);
            if(targetSpace == null) System.err.println("Error connecting to space with spaceName: " + spaceName );
            else {
                spaceInstance().addAll(asList(targetSpace.getInstances()));
                for (GridServiceContainer gsc : gridServiceContainers) {
                    vmDetails.add(gsc.getVirtualMachine().getDetails());
                    vmStatistics.add(gsc.getVirtualMachine().getStatistics());
                }
                for (SpacePartition partition : targetSpace.getPartitions()) {
                    replicationStatistics.add(partition.getPrimary().getStatistics().getReplicationStatistics());
                    mirrorStatistics.add(partition.getPrimary().getStatistics().getMirrorStatistics());
                }
            }
        }
    }

    protected void prepareMetric(FullMetric fullMetric) {
        NamedMetric metricName = fullMetric.getMetric();
        Map<NamedMetric, String> namedMetricStringMap = pidMetricMap.get(fullMetric.getGscPid());
        if (namedMetricStringMap == null){
            namedMetricStringMap = new HashMap<>();
            pidMetricMap.put(fullMetric.getGscPid(), namedMetricStringMap);
        }
        if (exponentialMovingAverage(metricName)){
            if (namedMetricStringMap.get(metricName) == null){
                namedMetricStringMap.put(metricName, fullMetric.getMetricValue());
            }
            Double oldValue = Double.parseDouble(namedMetricStringMap.get(metricName));
            Double collectedValue = Double.parseDouble(fullMetric.getMetricValue());
            Double averagedValue = average.average(oldValue, collectedValue);
            fullMetric.setMetricValue(String.format("%.3f", averagedValue));
            namedMetricStringMap.put(metricName, averagedValue.toString());
        }   else {
            namedMetricStringMap.put(metricName, fullMetric.getMetricValue());
        }
    }

    protected boolean exponentialMovingAverage(NamedMetric metric) {
        return emas.contains(metric);
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
