
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
import java.util.*;

public class PrintVisitor extends AbstractStatsVisitor {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public PrintVisitor(Admin admin, String spaceName){
        super(admin, spaceName);
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
