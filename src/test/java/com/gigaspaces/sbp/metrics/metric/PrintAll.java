package com.gigaspaces.sbp.metrics.metric;

import com.gigaspaces.sbp.metrics.metric.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 8/7/14
 * Time: 9:43 AM
 * Provides...
 */
public class PrintAll {

    public static void main(String[] args){

        List<NamedMetric> metrics = new ArrayList<>();
        metrics.addAll(Arrays.asList(GigaSpacesActivity.values()));
        metrics.addAll(Arrays.asList(GigaSpacesClusterInfo.values()));
        metrics.addAll(Arrays.asList(GsMirrorInfo.values()));
        metrics.addAll(Arrays.asList(JvmInfo.values()));
        metrics.addAll(Arrays.asList(Memory.values()));
        metrics.addAll(Arrays.asList(OperatingSystemInfo.values()));


        StringBuilder buff = new StringBuilder();
        for( NamedMetric metric : metrics ){
            buff.append(metric.displayName()).append("\n");
        }

        System.out.println(buff.toString());

    }

}
