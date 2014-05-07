package com.gigaspaces.monitoring.metrics_source.counter;

import org.springframework.beans.factory.annotation.Value;

/**
 * Class instance computes exponentially moving average corresponding to given alpha
 */
public class ExponentialAverageCounter {

    @Value( "${stat.sample.alpha}" )
    private float alpha;

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

    public float average(float oldValue, int input) {
        return oldValue + alpha * (input - oldValue);
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
}
