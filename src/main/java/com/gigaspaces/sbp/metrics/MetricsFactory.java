package com.gigaspaces.sbp.metrics;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 8/4/14
 * Time: 5:41 PM
 */
abstract class MetricsFactory {

    AbstractMetric fullContext(final Collection<String> hostNames,
                               final NamedMetric metricName,
                               final GigaSpaceProcess gsProcess) {

        return new MetricCollectedNow(gsProcess, hostNames) {
            /**
             * {@inheritDoc}
             */
            @Override
            public String displayName() {
                return metricName.displayName();
            }

            @Override
            public void accept(StatsVisitor statsVisitor) {
                // CODE SMELL: deferred bequest
            }
        };
    }

    protected class NowMetrics {

        private final Collection<String> hostNames = new ArrayList<>();

        NowMetrics(Collection<String> hostNames) {
            if( hostNames != null )
                this.hostNames.addAll(hostNames);
        }

        AbstractMetric gsa(final NamedMetric metric) {
            return fullContext(hostNames, metric, GigaSpaceProcess.GSA);
        }

        AbstractMetric gsc(final NamedMetric metric) {
            return fullContext(hostNames, metric, GigaSpaceProcess.GSC);
        }

        AbstractMetric lus(final NamedMetric metric) {
            return fullContext(hostNames, metric, GigaSpaceProcess.LUS);
        }

        AbstractMetric gsm(final NamedMetric metric) {
            return fullContext(hostNames, metric, GigaSpaceProcess.GSM);
        }

        AbstractMetric mirror(final NamedMetric metric) {
            return fullContext(hostNames, metric, GigaSpaceProcess.MIRROR);
        }

        AbstractMetric esm(final NamedMetric metric) {
            return fullContext(hostNames, metric, GigaSpaceProcess.ESM);
        }

        AbstractMetric partition(final NamedMetric metric) {
            return fullContext(hostNames, metric, GigaSpaceProcess.PARTITION);
        }

        AbstractMetric proxy(final NamedMetric metric) {
            return fullContext(hostNames, metric, GigaSpaceProcess.PROXY);
        }

        AbstractMetric webUi(final NamedMetric metric) {
            return fullContext(hostNames, metric, GigaSpaceProcess.WEBUI);
        }

        AbstractMetric gigaSpacesUi(final NamedMetric metric) {
            return fullContext(hostNames, metric, GigaSpaceProcess.GIGASPACES_UI);
        }

        AbstractMetric grid(final NamedMetric metric) {
            return fullContext(hostNames, metric, GigaSpaceProcess.CLUSTER);
        }
    }

    abstract Collection<AbstractMetric> create();

}
