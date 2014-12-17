package com.gigaspaces.sbp.metrics.visitor;

import com.gigaspaces.Factory;
import com.gigaspaces.sbp.metrics.Constants;
import com.gigaspaces.sbp.metrics.ExponentialMovingAverage;
import com.gigaspaces.sbp.metrics.bootstrap.GsMonitorSettings;
import com.gigaspaces.sbp.metrics.bootstrap.cli.OutputFormat;
import com.gigaspaces.sbp.metrics.bootstrap.xap.ConnectToXap;
import com.gigaspaces.sbp.metrics.metric.MetricsRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 12/12/14
 * Time: 10:49 AM
 */
@Component
public class VisitorFactory implements Factory<StatsVisitor> {

    private static final String UNSUPPORTED_FORMAT_ERROR = "Unsupported %s %s";

    @Resource
    private final GsMonitorSettings settings;
    @Resource
    private final ExponentialMovingAverage exponentialMovingAverage;
    @Resource
    private final ConnectToXap connectToXap;
    @Resource
    private final MetricsRegistry metricsRegistry;

    @Autowired
    public VisitorFactory(GsMonitorSettings settings, ExponentialMovingAverage exponentialMovingAverage, ConnectToXap connectToXap, MetricsRegistry metricsRegistry) {
        assert settings != null : String.format(Constants.THINGS_REQUIRED, GsMonitorSettings.class.getSimpleName());
        assert exponentialMovingAverage != null : String.format(Constants.THING_REQUIRED, ExponentialMovingAverage.class.getSimpleName());
        assert connectToXap != null : String.format(Constants.THING_REQUIRED, ConnectToXap.class.getSimpleName());
        assert metricsRegistry != null : String.format(Constants.THING_REQUIRED, MetricsRegistry.class.getSimpleName());
        this.settings = settings;
        this.exponentialMovingAverage = exponentialMovingAverage;
        this.connectToXap = connectToXap;
        this.metricsRegistry = metricsRegistry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsVisitor build() {
        OutputFormat fmt = settings.outputFormat();
        switch (fmt){
            case Csv:
                return new CsvVisitor(
                        connectToXap.getAdmin()
                        , Arrays.asList(settings.spaceNames())
                        , metricsRegistry.getPidMetrics()
                        , exponentialMovingAverage
                        , metricsRegistry.getAlerts()
                        , settings.derivedMetricsPeriodInMs()
                );
            case LogFormat:
                return new PrintVisitor(
                        connectToXap.getAdmin()
                        , Arrays.asList(settings.spaceNames())
                        , metricsRegistry.getPidMetrics()
                        , exponentialMovingAverage
                        , metricsRegistry.getAlerts()
                        , settings.derivedMetricsPeriodInMs()
                );
            default:
                throw new UnsupportedOperationException(String.format(UNSUPPORTED_FORMAT_ERROR, OutputFormat.class.getSimpleName(), fmt.name()));
        }
    }
}
