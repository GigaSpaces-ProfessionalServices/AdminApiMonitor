package com.gigaspaces.sbp.metrics.visitor;

import com.gigaspaces.Factory;
import com.gigaspaces.sbp.metrics.ExponentialMovingAverage;
import com.gigaspaces.sbp.metrics.bootstrap.GsMonitorSettings;
import com.gigaspaces.sbp.metrics.bootstrap.cli.OutputFormat;
import com.gigaspaces.sbp.metrics.bootstrap.xap.ConnectToXap;
import com.gigaspaces.sbp.metrics.metric.FullMetric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 12/12/14
 * Time: 10:49 AM
 */
@Component
public class VisitorFactory implements Factory<StatsVisitor> {

    private static final String UNSUPPORTED_FORMAT_ERROR = "Unsupported %s %s";

    private Map<String, FullMetric> pidMetricMap = new LinkedHashMap<>();
    private ConcurrentHashMap<String, AtomicInteger> alerts = new ConcurrentHashMap<>();

    @Resource
    private final GsMonitorSettings settings;
    @Resource
    private final ExponentialMovingAverage exponentialMovingAverage;
    @Resource
    private final ConnectToXap connectToXap;

    @Autowired
    public VisitorFactory(GsMonitorSettings settings, ExponentialMovingAverage exponentialMovingAverage, ConnectToXap connectToXap) {
        this.settings = settings;
        this.exponentialMovingAverage = exponentialMovingAverage;
        this.connectToXap = connectToXap;
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
                        , pidMetricMap
                        , exponentialMovingAverage
                        , alerts
                        , settings.derivedMetricsPeriodInMs()
                );
            case LogFormat:
                return new PrintVisitor(
                        connectToXap.getAdmin()
                        , Arrays.asList(settings.spaceNames())
                        , pidMetricMap
                        , exponentialMovingAverage
                        , alerts
                        , settings.derivedMetricsPeriodInMs()
                );
            default:
                throw new UnsupportedOperationException(String.format(UNSUPPORTED_FORMAT_ERROR, OutputFormat.class.getSimpleName(), fmt.name()));
        }
    }
}
