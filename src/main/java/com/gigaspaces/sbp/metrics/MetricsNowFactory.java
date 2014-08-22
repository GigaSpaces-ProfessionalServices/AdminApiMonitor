package com.gigaspaces.sbp.metrics;

import com.gigaspaces.sbp.metrics.metric.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 8/6/14
 * Time: 3:57 PM
 */
abstract class MetricsNowFactory extends MetricsFactory {

    private List<GigaSpaceMetric> list(int len){
        return new ArrayList<>(len);
    }

    Collection<GigaSpaceMetric> mirrorMetrics(NowMetrics nowMetrics){
        GsMirrorInfo[] info = GsMirrorInfo.values();
        List<GigaSpaceMetric> metrics = list(info.length);
        for( GsMirrorInfo mirrorInfo:GsMirrorInfo.values())
            metrics.add(nowMetrics.mirror(mirrorInfo));
        return metrics;
    }

    ////////////////// GSC /////////////////////

    private GigaSpaceMetric gsc(NowMetrics nowMetrics, NamedMetric metric){
        return nowMetrics.gsc(metric);
    }

    Collection<GigaSpaceMetric> memoryMetrics(NowMetrics nowMetrics){
        Memory[] memory = Memory.values();
        List<GigaSpaceMetric> metrics = list(memory.length);
        for( Memory metric : memory){
            metrics.add(gsc(nowMetrics, metric));}
        return metrics;
    }

    Collection<GigaSpaceMetric> jvmInfoMetrics(NowMetrics nowMetrics){
        JvmInfo[] info = JvmInfo.values();
        List<GigaSpaceMetric> metrics = list(info.length);
        for( JvmInfo jvmInfo : info ) metrics.add(gsc(nowMetrics, jvmInfo));
        return metrics;
    }

    Collection<GigaSpaceMetric> activityMetrics(NowMetrics nowMetrics){
        GigaSpacesActivity[] activities = GigaSpacesActivity.values();
        List<GigaSpaceMetric> metrics = list(activities.length);
        for( GigaSpacesActivity activity : activities ) metrics.add(gsc(nowMetrics, activity));
        return metrics;
    }

    Collection<GigaSpaceMetric> gscMetrics(NowMetrics nowMetrics){
        Collection<GigaSpaceMetric> metrics = activityMetrics(nowMetrics);
        metrics.addAll(jvmInfoMetrics(nowMetrics));
        metrics.addAll(memoryMetrics(nowMetrics));
        return metrics;
    }

    ////////////////// END GSC /////////////////////

    Collection<GigaSpaceMetric> osMetrics(NowMetrics nowMetrics) {
        OperatingSystemInfo[] info = OperatingSystemInfo.values();
        List<GigaSpaceMetric> metrics = list(info.length);
        for( OperatingSystemInfo osInfo : info) metrics.add(nowMetrics.grid(osInfo));
        return metrics;
    }

}