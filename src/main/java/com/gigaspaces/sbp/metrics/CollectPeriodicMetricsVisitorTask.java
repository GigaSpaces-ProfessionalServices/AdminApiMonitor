package com.gigaspaces.sbp.metrics;

import com.gigaspaces.sbp.metrics.bootstrap.GsMonitorSettings;
import com.gigaspaces.sbp.metrics.metric.NamedMetric;
import com.gigaspaces.sbp.metrics.visitor.CsvVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;

@Component
public class CollectPeriodicMetricsVisitorTask /*extends AbstractPeriodicVisitorTask*/ {

    private static final String THING_REQUIRED = "%s are required";
    private static final String UNSUPPORTED_FORMAT = "OutputFormat '%s' is not supported by this build of the tool.";

    @Resource
    private final GsMonitorSettings settings;
    @Resource
    private final ExponentialMovingAverage average;

    private boolean headersSaved;
    private Collection<NamedMetric> metrics;

    @Autowired
    public CollectPeriodicMetricsVisitorTask(GsMonitorSettings settings, ExponentialMovingAverage average) {
        assert settings != null : String.format(THING_REQUIRED, GsMonitorSettings.class.getSimpleName());
        assert average != null : String.format(THING_REQUIRED, ExponentialMovingAverage.class.getSimpleName());
        this.settings = settings;
        this.average = average;
    }

    // TODO set period from GsMonitorSettings

    private CsvVisitor visitor;
    // CsvVisitor visitor = null;//new CsvVisitor(adminMonitor.getAdmin(), spaceNames, pidMetricMap, exponentialMovingAverage, alerts, period);
    // StatsVisitor visitor = null;// new PrintVisitor(adminMonitor.getAdmin(), spaceNames, pidMetricMap, exponentialMovingAverage, alerts, period);

    public void collectMetrics() {
        switch (settings.outputFormat()) {
            case Csv:
                if (!headersSaved) {
                    visitor.setSaveHeaders(true);
                    headersSaved = true;
                }
                for (NamedMetric metric : metrics) {
                    metric.accept(visitor);
                }
                visitor.printCsvMetrics();
                break;
            case LogFormat:
                for (NamedMetric metric : metrics) {
                    metric.accept(visitor);
                }
                break;
            default:
                throw new IllegalStateException(String.format(UNSUPPORTED_FORMAT, settings.outputFormat()));
        }

    }

}
