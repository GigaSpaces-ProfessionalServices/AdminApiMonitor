package com.gigaspaces.sbp.metrics;

import java.util.Arrays;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 8/5/14
 * Time: 3:59 PM
 */
public abstract class MetricOnHost extends AbstractMetric{

    private SortedSet<String> hostNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

    protected MetricOnHost(GigaSpaceProcess gsProcess, String hostName) {
        this(gsProcess, new String[]{hostName});
    }

    protected MetricOnHost(GigaSpaceProcess gsProcess, String[] hostNames){
        this(gsProcess, Arrays.asList(hostNames));
    }

    protected MetricOnHost(GigaSpaceProcess gsProcess, Collection<String> hostNames){
        super(gsProcess);
        hostNames.addAll(hostNames);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedSet<String> onHosts() {
        SortedSet<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        set.addAll(hostNames);
        return set;
    }

}
