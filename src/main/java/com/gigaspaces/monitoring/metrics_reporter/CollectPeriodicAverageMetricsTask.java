package com.gigaspaces.monitoring.metrics_reporter;

import com.gigaspaces.monitoring.metrics_source.adminapi.AdminAPIMonitor;
import com.gigaspaces.monitoring.metrics_source.adminapi.AverageStat;
import com.gigaspaces.monitoring.metrics_source.space_proxy.SpaceProxyCounter;
import org.springframework.beans.factory.annotation.Required;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Logger;

public class CollectPeriodicAverageMetricsTask {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private SpaceProxyCounter spaceProxyCounter;

    private AdminAPIMonitor adminMonitor;

    private void collectMetrics(){
        logger.info("Collect timed moving average metrics");
        logger.info("PERIODIC_METRICS = " + getMetrics(spaceProxyCounter.refreshTimedMetrics()));
    }

    public String getMetrics(SpaceProxyCounter.PeriodicMetricsCounter periodicMetricsCounter){
//        Memory Consumed / Have already (Admin API)
//        Number of Puts / Have already (Admin API) ~ puts per ???
//        Number of Gets / Have already (Admin API) ~ per ???
//        Number of Removes / Have already (Admin API) ~ takes per ???
//        Latency / Have already - latency per get (PI) - needs to be exposed
        return formatMetrics("cacheMemUsed", adminMonitor.getMemoryUsed()) + formatMetrics("cachePutCount", periodicMetricsCounter.getWriteCounter()) +
                formatMetrics("cacheGetCount", periodicMetricsCounter.getReadCounter()) + formatMetrics("cacheRemoveCount", periodicMetricsCounter.getTakeCounter()) +
                formatMetrics("cacheLatencyCount", spaceProxyCounter.getAverageReadTime());

    }

    private String formatMetrics(String metricType, Number metricValue){
        String date = Calendar.getInstance().getTime().toString(); //TODO to be changed
        String serverName = getServerName();
        return "\n" + "ex. --> " + date + " -- " + metricType + "-- " + serverName +  " -- jvmName -- cacheNum — " + metricValue;
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

    @Required
    public void setSpaceProxyCounter(SpaceProxyCounter spaceProxyCounter) {
        this.spaceProxyCounter = spaceProxyCounter;
    }
}
