package com.gigaspaces.sbp.metrics.visitor;

import com.gigaspaces.sbp.metrics.ExponentialMovingAverage;
import com.gigaspaces.sbp.metrics.FullMetric;
import com.gigaspaces.sbp.metrics.metric.NamedMetric;
import org.openspaces.admin.Admin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

public class PrintVisitor extends AbstractStatsVisitor {

    public static final String VALUE_SEPARATOR = " :: ";
    private Logger logger = LoggerFactory.getLogger(getClass());

    private Set<NamedMetric> savedOnceMetrics = new HashSet<>();

    public PrintVisitor(Admin admin, List<String> spaceName, Map<String, FullMetric> pidMetricMap, ExponentialMovingAverage average, Long period){
        super(admin, spaceName, pidMetricMap, average, period);
    }

    @Override
    public void saveStat(FullMetric fullMetric) {
        List<FullMetric> fullMetrics = prepareMetric(fullMetric);
        for (FullMetric metric : fullMetrics){
            logger.info(formatMetrics(metric));
        }
    }

    private String formatMetrics(FullMetric fullMetric) {
        SimpleDateFormat date = new SimpleDateFormat("dd-M-yyyy HH:mm:ss");
        String spaceInstanceID = (fullMetric.getSpaceInstanceID() != null) ? fullMetric.getSpaceInstanceID() : " - ";
        String gscPid = (fullMetric.getGscPid() != null && fullMetric.getGscPid() != 0l) ? fullMetric.getGscPid().toString() : " - ";
        String hostName = (fullMetric.getHostName() != null) ? fullMetric.getHostName() : " - ";
        String spaceMode = (fullMetric.getSpaceMode() != null) ? fullMetric.getSpaceMode() : " - ";
        return "\n" + date.format(new Date()) + VALUE_SEPARATOR +  hostName + VALUE_SEPARATOR + spaceInstanceID + VALUE_SEPARATOR + gscPid +
                VALUE_SEPARATOR + fullMetric.getMetricFullName() + VALUE_SEPARATOR + fullMetric.getMetricValue() + VALUE_SEPARATOR + spaceMode;
    }

    @Override
    public boolean isSavedOnce(NamedMetric metric) {
        return savedOnceMetrics.contains(metric);
    }

    @Override
    public void saveOnce(FullMetric fullMetric) {
        savedOnceMetrics.add(fullMetric.getMetric());
        logger.info(formatMetrics(fullMetric));
    }

}
