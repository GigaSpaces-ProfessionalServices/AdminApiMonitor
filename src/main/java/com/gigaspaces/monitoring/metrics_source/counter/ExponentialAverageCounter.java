package com.gigaspaces.monitoring.metrics_source.counter;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class ExponentialAverageCounter {

    @Value( "${stat.sample.alpha}" )
    private double alpha;

    public double average(Double oldValue, double input) {
        if (oldValue == null) {
            return input;
        }
        return oldValue + alpha * (input - oldValue);
    }

    public double average(Double oldValue, long input) {
        if (oldValue == null) {
            return input;
        }
        return oldValue + alpha * (input - oldValue);
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }
}
