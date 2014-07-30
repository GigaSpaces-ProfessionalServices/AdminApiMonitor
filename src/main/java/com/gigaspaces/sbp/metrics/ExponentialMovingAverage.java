package com.gigaspaces.sbp.metrics;

/**
 * Class instance computes exponentially moving average corresponding to given alpha
 */
public class ExponentialMovingAverage {

    private static final Float DEFAULT_ALPHA = 0.5f;

    private float alpha = DEFAULT_ALPHA;

    public Double average(Double oldValue, Double input) {
        if (oldValue == null) {
            return input;
        }
        return oldValue + alpha * (input - oldValue);
    }

    public Double average(Double oldValue, Long input) {
        if (oldValue == null) {
            return input.doubleValue();
        }
        return oldValue + alpha * (input - oldValue);
    }

    public Float average(Float oldValue, Integer input) {
        return oldValue + alpha * (input - oldValue);
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
}
