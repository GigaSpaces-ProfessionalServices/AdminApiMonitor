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

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Double.parseDouble;
import static java.util.Arrays.*;

public abstract class AbstractStatsVisitor implements StatsVisitor {

    protected final MetricsRegistry metricsRegistry;

    protected Admin admin;

    protected List<GridServiceContainer> gridServiceContainers;

    protected List<VirtualMachineDetails> vmDetails;

    protected List<VirtualMachineStatistics> vmStatistics;

    protected List<ReplicationStatistics> replicationStatistics;

    protected List<MirrorStatistics> mirrorStatistics;

    protected List<SpaceInstance> spaceInstances;

    protected List<ProcessingUnitInstance> processingUnitInstances;

    protected Long derivedMetricsPeriodInMs;

    private Map<String, AtomicInteger> alerts;

    protected ExponentialMovingAverage average;

    protected SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);

    protected AbstractStatsVisitor(
            Admin admin
            , List<String> spaceNames
            , ExponentialMovingAverage average
            , Map<String, AtomicInteger> alerts
            , Long derivedMetricsPeriodInMs
            , MetricsRegistry metricsRegistry) {

        this.admin = admin;
        this.alerts = alerts;
        this.average = average;
        this.derivedMetricsPeriodInMs = derivedMetricsPeriodInMs;

        assert metricsRegistry != null : String.format(Constants.THING_REQUIRED, MetricsRegistry.class.getSimpleName());
        this.metricsRegistry = metricsRegistry;

        // whole grid
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
                spaceInstances.addAll(asList(targetSpace.getInstances()));
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

    }

    protected List<FullMetric> prepareMetric(FullMetric fullMetric) {
        List<FullMetric> result = new ArrayList<>();
        NamedMetric namedMetric = fullMetric.getMetric();
        String metricFullName = fullMetric.getMetricFullName();

        if (metricsRegistry.hasDerivativeMetric(namedMetric)){
            FullMetric previous = metricsRegistry.getPidMetrics().get(metricFullName);
            Double metricValue = parseDouble(fullMetric.getMetricValue());
            Double diff = (previous != null) ? metricValue - parseDouble(previous.getMetricValue()) : metricValue;
            Double currentValue = diff / (derivedMetricsPeriodInMs /1000);
            NamedMetric derivedMetric = metricsRegistry.getDerivedMetric(fullMetric.getMetric());
            FullMetric calculatedMetric = new FullMetric.FullMetricBuilder().
                                            metric(derivedMetric).
                                            metricValue(String.valueOf(currentValue)).
                                            spaceInstanceID(fullMetric.getSpaceInstanceID()).
                                            spaceMode(fullMetric.getSpaceMode()).
                                            create();
            if (metricsRegistry.isMovingAveraged(derivedMetric)){
                movingAverage(calculatedMetric);
            }
            result.add(calculatedMetric);
            metricsRegistry.getPidMetrics().put(calculatedMetric.getMetricFullName(), calculatedMetric);
        }   else if (metricsRegistry.isMovingAveraged(namedMetric)){
            movingAverage(fullMetric);
        }
        result.add(fullMetric);
        metricsRegistry.getPidMetrics().put(metricFullName, fullMetric);
        return result;
    }

    private void movingAverage(FullMetric metric) {
        String metricFullName = metric.getMetricFullName();
        if (metricsRegistry.getPidMetrics().get(metricFullName) == null){
            metricsRegistry.getPidMetrics().put(metricFullName, metric);
        }
        Double oldValue = parseDouble(metricsRegistry.getPidMetrics().get(metricFullName).getMetricValue());
        Double collectedValue = parseDouble(metric.getMetricValue());
        Double averagedValue = average.average(oldValue, collectedValue);
        metric.setMetricValue(String.format(Locale.ENGLISH, "%.3f", averagedValue));
    }

    public Map<String, Integer> alerts(){
        Map<String, Integer> result = new HashMap<>();
        for (String key : alerts.keySet()){
            result.put(key, alerts.get(key).intValue());
        }
        return result;
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
