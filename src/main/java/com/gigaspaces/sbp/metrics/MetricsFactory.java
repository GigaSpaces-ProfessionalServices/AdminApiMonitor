package com.gigaspaces.sbp.metrics;

import com.gigaspaces.sbp.metrics.visitor.StatsVisitor;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 8/4/14
 * Time: 5:41 PM
 */
abstract class MetricsFactory {

    GigaSpaceMetric fullContext(final Collection<String> hostNames,
                               final NamedMetric metricName,
                               final GigaSpaceProcess gsProcess) {

        return new NowGigaSpaceMetricOnHost(gsProcess, hostNames) {
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

        GigaSpaceMetric gsa(final NamedMetric metric) {
            return fullContext(hostNames, metric, GigaSpaceProcess.GSA);
        }

        GigaSpaceMetric gsc(final NamedMetric metric) {
            return fullContext(hostNames, metric, GigaSpaceProcess.GSC);
        }

        GigaSpaceMetric lus(final NamedMetric metric) {
            return fullContext(hostNames, metric, GigaSpaceProcess.LUS);
        }

        GigaSpaceMetric gsm(final NamedMetric metric) {
            return fullContext(hostNames, metric, GigaSpaceProcess.GSM);
        }

        GigaSpaceMetric mirror(final NamedMetric metric) {
            return fullContext(hostNames, metric, GigaSpaceProcess.MIRROR);
        }

        GigaSpaceMetric esm(final NamedMetric metric) {
            return fullContext(hostNames, metric, GigaSpaceProcess.ESM);
        }

        GigaSpaceMetric partition(final NamedMetric metric) {
            return fullContext(hostNames, metric, GigaSpaceProcess.PARTITION);
        }

        GigaSpaceMetric proxy(final NamedMetric metric) {
            return fullContext(hostNames, metric, GigaSpaceProcess.PROXY);
        }

        GigaSpaceMetric webUi(final NamedMetric metric) {
            return fullContext(hostNames, metric, GigaSpaceProcess.WEBUI);
        }

        GigaSpaceMetric gigaSpacesUi(final NamedMetric metric) {
            return fullContext(hostNames, metric, GigaSpaceProcess.GIGASPACES_UI);
        }

        GigaSpaceMetric grid(final NamedMetric metric) {
            return fullContext(hostNames, metric, GigaSpaceProcess.CLUSTER);
        }
    }

    abstract Collection<GigaSpaceMetric> create();

}
