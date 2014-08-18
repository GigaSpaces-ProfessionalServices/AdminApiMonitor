package com.gigaspaces.sbp.metrics;

import com.gigaspaces.sbp.metrics.AdminApiMonitor;
import com.gigaspaces.sbp.metrics.GigaSpacesActivity;
import com.gigaspaces.sbp.metrics.visitor.PrintVisitor;
import com.gigaspaces.sbp.metrics.visitor.StatsVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CollectPeriodicMetricsVisitorTask {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private AdminApiMonitor adminMonitor;

    private void collectMetrics(){
        logger.info("VISITOR_COLLECTION_STARTS");
        //logger.info("PERIODIC_METRICS = " + getMetrics());
        StatsVisitor visitor = new PrintVisitor(adminMonitor.getAdmin());
        List<NamedMetric> metrics = new ArrayList<>();
        metrics.addAll(Arrays.asList(GigaSpacesActivity.values()));
        metrics.addAll(Arrays.asList(GigaSpacesClusterInfo.values()));
        metrics.addAll(Arrays.asList(GsMirrorInfo.values()));
        metrics.addAll(Arrays.asList(JvmInfo.values()));
        metrics.addAll(Arrays.asList(Memory.values()));
        metrics.addAll(Arrays.asList(OperatingSystemInfo.values()));
        for (NamedMetric metric : metrics){
            metric.accept(visitor);
        }
    }

    public String getMetrics(){
//        Memory Consumed / Have already (Admin API)
//        Number of Puts / Have already (Admin API) ~ puts per ???
//        Number of Gets / Have already (Admin API) ~ per ???
//        Number of Removes / Have already (Admin API) ~ takes per ???
//        Latency / Have already - latency per get (PI) - needs to be exposed
        return  formatMetrics("cacheNum", adminMonitor.getObjectsCount()) + formatMetrics("cacheMemUsed", adminMonitor.getMemoryUsed());
    }

    private String formatMetrics(String metricType, Number metricValue){
        SimpleDateFormat date = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        String serverName = getServerName();
        return "\n" + date.format(new Date()) + " -- " + metricType + "-- " + serverName +  " -- "+ adminMonitor.getVmName() + " -- cacheNum — " + metricValue;
    }

    private String getServerName() {
        try {
            return  InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "serverName";
        }
    }

    @Required
    public void setAdminMonitor(AdminApiMonitor adminMonitor) {
        this.adminMonitor = adminMonitor;
    }

}
