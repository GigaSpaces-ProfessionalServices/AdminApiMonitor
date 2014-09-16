package com.gigaspaces.sbp.metrics.visitor;

import com.gigaspaces.cluster.replication.async.mirror.MirrorStatistics;
import com.gigaspaces.sbp.metrics.*;
import com.gigaspaces.sbp.metrics.metric.*;
import com.j_spaces.core.filters.ReplicationStatistics;
import org.openspaces.admin.Admin;
import org.openspaces.admin.gsc.GridServiceContainer;
import org.openspaces.admin.pu.ProcessingUnitInstance;
import org.openspaces.admin.space.Space;
import org.openspaces.admin.space.SpaceInstance;
import org.openspaces.admin.space.SpacePartition;
import org.openspaces.admin.vm.VirtualMachineDetails;
import org.openspaces.admin.vm.VirtualMachineStatistics;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Double.parseDouble;
import static java.util.Arrays.*;

public abstract class AbstractStatsVisitor implements StatsVisitor {

    protected Admin admin;

    protected List<GridServiceContainer> gridServiceContainers;

    protected List<VirtualMachineDetails> vmDetails;

    protected List<VirtualMachineStatistics> vmStatistics;

    protected List<ReplicationStatistics> replicationStatistics;

    protected List<MirrorStatistics> mirrorStatistics;

    protected List<SpaceInstance> spaceInstances;

    protected List<ProcessingUnitInstance> processingUnitInstances;

    protected Long period;

    private Map<String, AtomicInteger> alerts;

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

    // value metric has to be derived from key metric
    protected Map<NamedMetric, NamedMetric> derivedMetricsMap = new HashMap<>();

    protected Map<String, FullMetric> metricMap;

    protected ExponentialMovingAverage average;

    protected AbstractStatsVisitor(Admin admin, List<String> spaceNames, Map<String, FullMetric> metricMap, ExponentialMovingAverage average,
                                   Map<String, AtomicInteger> alerts, Long period) {
        this.metricMap = metricMap;
        this.admin=admin;
        this.alerts=alerts;
        this.average = average;
        this.period = period;
        //whole grid
        gridServiceContainers = asList(admin.getGridServiceContainers().getContainers());

        vmDetails = new ArrayList<>(gridServiceContainers.size());
        vmStatistics = new ArrayList<>(gridServiceContainers.size());
        replicationStatistics = new ArrayList<>();
        mirrorStatistics = new ArrayList<>();
        spaceInstances = new ArrayList<>();
        processingUnitInstances = new ArrayList<>();

        // separate spaces
        for (String spaceName : spaceNames){
            Space targetSpace = admin.getSpaces().getSpaceByName(spaceName);
            if(targetSpace == null) System.err.println("Error connecting to space with spaceName: " + spaceName );
            else {
                spaceInstance().addAll(asList(targetSpace.getInstances()));
                for (GridServiceContainer gsc : gridServiceContainers) {
                    vmDetails.add(gsc.getVirtualMachine().getDetails());
                    vmStatistics.add(gsc.getVirtualMachine().getStatistics());
                    processingUnitInstances.addAll(Arrays.asList(gsc.getProcessingUnitInstances()));
                }
                for (SpacePartition partition : targetSpace.getPartitions()) {
                    if (partition.getPrimary() != null && partition.getPrimary().getStatistics() != null){
                        replicationStatistics.add(partition.getPrimary().getStatistics().getReplicationStatistics());
                    }
                }
                for (SpaceInstance spaceInstance : spaceInstance()){
                    mirrorStatistics.add(spaceInstance.getStatistics().getMirrorStatistics());
                }
            }
        }

        derivedMetricsMap.put(GigaSpacesActivity.WRITE_COUNT, GigaSpacesActivity.WRITES_PER_SEC);
        derivedMetricsMap.put(GigaSpacesActivity.READ_COUNT, GigaSpacesActivity.READ_PER_SEC);
        derivedMetricsMap.put(GigaSpacesActivity.UPDATE_COUNT, GigaSpacesActivity.UPDATES_PER_SEC);
        derivedMetricsMap.put(GigaSpacesActivity.EXECUTE_COUNT, GigaSpacesActivity.EXECUTES_PER_SEC);
        derivedMetricsMap.put(GigaSpacesActivity.TAKE_COUNT, GigaSpacesActivity.TAKES_PER_SECOND);
    }

    protected List<FullMetric> prepareMetric(FullMetric fullMetric) {
        List<FullMetric> result = new ArrayList<>();
        NamedMetric namedMetric = fullMetric.getMetric();
        String metricFullName = fullMetric.getMetricFullName();

        if (calculateAverage(namedMetric)){
            FullMetric previous = metricMap.get(metricFullName);
            Double metricValue = parseDouble(fullMetric.getMetricValue());
            Double diff = (previous != null) ? metricValue - parseDouble(previous.getMetricValue()) : metricValue;
            Double currentValue = diff / (period/1000);
            NamedMetric calculatedMetricName = derivedMetricsMap.get(fullMetric.getMetric());
            FullMetric calculatedMetric = new FullMetric.FullMetricBuilder().
                                            metric(calculatedMetricName).
                                            metricValue(String.valueOf(currentValue)).
                                            spaceInstanceID(fullMetric.getSpaceInstanceID()).
                                            spaceMode(fullMetric.getSpaceMode()).
                                            create();
            if (exponentialMovingAverage(calculatedMetricName)){
                movingAverage(calculatedMetric);
            }
            result.add(calculatedMetric);
            metricMap.put(calculatedMetric.getMetricFullName(), calculatedMetric);
        }   else if (exponentialMovingAverage(namedMetric)){
            movingAverage(fullMetric);
        }
        result.add(fullMetric);
        metricMap.put(metricFullName, fullMetric);
        return result;
    }

    private void movingAverage(FullMetric metric) {
        String metricFullName = metric.getMetricFullName();
        if (metricMap.get(metricFullName) == null){
            metricMap.put(metricFullName, metric);
        }
        Double oldValue = parseDouble(metricMap.get(metricFullName).getMetricValue());
        Double collectedValue = parseDouble(metric.getMetricValue());
        Double averagedValue = average.average(oldValue, collectedValue);
        metric.setMetricValue(String.format("%.3f", averagedValue));
    }

    public Map<String, Integer> alerts(){
        Map<String, Integer> result = new HashMap<>();
        for (String key : alerts.keySet()){
            result.put(key, alerts.get(key).intValue());
        }
        return result;
    }

    private boolean calculateAverage(NamedMetric metricName) {
        return derivedMetricsMap.keySet().contains(metricName);
    }

    protected boolean exponentialMovingAverage(NamedMetric metric) {
        return emas.contains(metric);
    }

    @Override
    public Admin admin() {
        return admin;
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

    @Override
    public List<ProcessingUnitInstance> processingUnitInstances() {
        return processingUnitInstances;
    }
}
