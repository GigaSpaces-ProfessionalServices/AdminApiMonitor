package com.gigaspaces.monitoring.metrics_reporter;

import com.gigaspaces.monitoring.metrics_source.adminapi.AdminAPIMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CollectPeriodicAverageMetricsTask {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private AdminAPIMonitor adminMonitor;

    private Integer collectionPeriod;

    private String logFileLocation;

    private void collectMetrics(){
        logger.info("Collect timed moving average metrics");
        logger.info("PERIODIC_METRICS = " + getMetrics());
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
    public void setAdminMonitor(AdminAPIMonitor adminMonitor) {
        this.adminMonitor = adminMonitor;
    }


    public void setCollectionPeriod(Integer collectionPeriod) {
        this.collectionPeriod = collectionPeriod;
    }

    public Logger getLogger() {
        return logger;
    }
}
