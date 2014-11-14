package com.gigaspaces.sbp.metrics;

import com.gigaspaces.sbp.metrics.bootstrap.GsMonitorSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Class instance computes exponentially moving average corresponding to given alpha
 */
@Component
public class ExponentialMovingAverage {

    @Resource
    public final GsMonitorSettings gsMonitorSettings;

    @Autowired
    public ExponentialMovingAverage(GsMonitorSettings gsMonitorSettings) {
        assert gsMonitorSettings != null : "need non-null settings";
        this.gsMonitorSettings = gsMonitorSettings;
    }

    public Double average(Double oldValue, Double input) {
        if (oldValue == null) {
            return input;
        }
        return oldValue + gsMonitorSettings.emaAlpha() * (input - oldValue);
    }

    public Double average(Double oldValue, Long input) {
        if (oldValue == null) {
            return input.doubleValue();
        }
        return oldValue + gsMonitorSettings.emaAlpha() * (input - oldValue);
    }

    public Float average(Float oldValue, Integer input) {
        return oldValue + gsMonitorSettings.emaAlpha() * (input - oldValue);
    }

}
