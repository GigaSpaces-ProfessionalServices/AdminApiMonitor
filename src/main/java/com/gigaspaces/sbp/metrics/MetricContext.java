package com.gigaspaces.sbp.metrics;

import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 8/4/14
 * Time: 4:41 PM
 *
 * Any given {@link Metric} describes some state in a given context. The context describes
 * when the metric was recorded and which {@link GigaSpaceProcess}es and hostname(s) that hosted those
 * {@link GigaSpaceProcess}es
 */
public interface MetricContext {

    /**
     * @return host names on which a metric applies
     */
    SortedSet<String> onHosts();

    /**
     * @return process types that the metric spans
     */
    GigaSpaceProcess over();

    /**
     * This field is nullable
     * @return interval over which a metric applies - as for an average
     */
    CollectionPeriod during();

}
