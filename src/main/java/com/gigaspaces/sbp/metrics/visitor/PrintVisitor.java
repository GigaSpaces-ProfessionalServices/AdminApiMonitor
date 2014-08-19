
package com.gigaspaces.sbp.metrics.visitor;

import com.gigaspaces.sbp.metrics.FullMetric;
import com.gigaspaces.sbp.metrics.NamedMetric;
import org.openspaces.admin.Admin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

public class PrintVisitor extends AbstractStatsVisitor {

    public static final String VALUE_SEPARATOR = " :: ";
    private Logger logger = LoggerFactory.getLogger(getClass());

    private Set<NamedMetric> savedOnceMetrics = new HashSet<>();

    public PrintVisitor(Admin admin, String spaceName){
        super(admin, spaceName);
    }

    @Override
    public void saveStat(NamedMetric metric, String value) {
        logger.info(formatMetrics(metric.displayName(), value));
    }

    @Override
    public void saveStat(FullMetric fullMetric) {
        logger.info(formatMetrics(fullMetric));
    }

    private String formatMetrics(FullMetric fullMetric) {
        SimpleDateFormat date = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        String spaceInstanceID = (fullMetric.getSpaceInstanceID() != null) ? fullMetric.getSpaceInstanceID().toString() : " - ";
        String gscPid = (fullMetric.getGscPid() != null) ? fullMetric.getGscPid().toString() : " - ";
        return "\n" + date.format(new Date()) + VALUE_SEPARATOR + fullMetric.getMetric().displayName() + VALUE_SEPARATOR + spaceInstanceID + VALUE_SEPARATOR + gscPid +
                VALUE_SEPARATOR + fullMetric.getMetricValue();
    }

    @Override
    public boolean isSavedOnce(NamedMetric metric) {
        return savedOnceMetrics.contains(metric);
    }

    @Override
    public void saveOnce(NamedMetric metric, String value) {
        savedOnceMetrics.add(metric);
        logger.info(formatMetrics(metric.displayName(), value));
    }

    @Override
    public void saveOnce(FullMetric fullMetric) {
        logger.info(formatMetrics(fullMetric));
    }

    private String formatMetrics(String metricType, String metricValue){
        SimpleDateFormat date = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        String serverName = "TODO SERVER NAME";
        return "\n" + date.format(new Date()) + " -- " + metricType + "-- " + serverName +  " -- " + "TODO VM NAME" + " -- cacheNum — " + metricValue;
    }
}
