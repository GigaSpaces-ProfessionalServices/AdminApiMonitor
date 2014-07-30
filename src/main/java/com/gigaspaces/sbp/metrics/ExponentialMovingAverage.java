package com.gigaspaces.sbp.metrics;

/**
 * Class instance computes exponentially moving average corresponding to given alpha
 */
public class ExponentialMovingAverage {

    private static final Float DEFAULT_ALPHA = 0.5f;

    private float alpha = DEFAULT_ALPHA;

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
