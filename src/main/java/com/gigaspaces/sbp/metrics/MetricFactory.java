package com.gigaspaces.sbp.metrics;


import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 8/4/14
 * Time: 5:41 PM
 */
class MetricFactory {

    Metric fullContext(final CollectionPeriod collectionPeriod,
                               final String[] hostNames,
                               final Displayable metricName,
                               final GigaSpaceProcess gsProcess) {

        return new Metric(gsProcess) {

            /**
             * {@inheritDoc}
             */
            @Override
            public SortedSet<String> onHosts() {
                SortedSet<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
                set.addAll(Arrays.asList(hostNames));
                return set;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public CollectionPeriod during() {
                return collectionPeriod;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public String displayName() {
                return metricName.displayName();
            }
        };
    }

    Metric singleNodeContext(final Displayable metricName,
                             final CollectionPeriod collectionPeriod,
                             final String hostname,
                             final GigaSpaceProcess gsProcess) {
        return fullContext(collectionPeriod, new String[]{hostname}, metricName, gsProcess);
    }

    private void procMeBabyOneMoreTime(final Displayable metricName){
        GigaSpaceProcess gsProcess = null;

        switch (gsProcess){

            case GSA: return;
            case GSC: return;
            case LUS: return;
            case GSM: return;

            case MIRROR: return;

            case ESM: return;

            case PARTITION: return;
            case PROXY: return;
            case SPACE: return;
            case WEBUI: return;

        }
    }

}
