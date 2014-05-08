package com.gigaspaces.monitoring.metrics_source.space_proxy;

import com.gigaspaces.monitoring.metrics_source.counter.ExponentialAverageCounter;
import org.springframework.beans.factory.annotation.Required;

import java.util.concurrent.atomic.AtomicInteger;

public class SpaceProxyCounter {

    private ExponentialAverageCounter averageCounter;

    private PeriodicMetricsCounter periodicCounter = new PeriodicMetricsCounter();

    AtomicInteger readCounter = new AtomicInteger(0);

    AtomicInteger averageReadTime = new AtomicInteger(0);

    AtomicInteger writeCounter = new AtomicInteger(0);

    AtomicInteger averageWriteTime = new AtomicInteger(0);

    AtomicInteger changeCounter = new AtomicInteger(0);

    AtomicInteger averageChangeTime = new AtomicInteger(0);

    AtomicInteger takeCounter = new AtomicInteger(0);

    AtomicInteger averageTakeTime = new AtomicInteger(0);

    AtomicInteger cacheHitCounter = new AtomicInteger(0);

    AtomicInteger cacheMissCounter = new AtomicInteger(0);

    public SpaceProxyCounter() {
    }

    public void count(SimplePerformanceItem performanceItem){
        String methodName = performanceItem.getSourceMethodName();
        Integer elapsedTime = performanceItem.getElapsedTime();
        if (methodName.contains("write") || methodName.contains("Write")){
            periodicCounter.writeCounter.addAndGet(1);
            writeCounter.addAndGet(1);
            updateAverageTime(elapsedTime, averageWriteTime);
        }   else if (methodName.contains("read") || methodName.contains("Read")){
            periodicCounter.readCounter.addAndGet(1);
            readCounter.addAndGet(1);
            updateHitOrMissCounters(performanceItem);
            updateAverageTime(elapsedTime, averageReadTime);
        }   else if (methodName.contains("change") || methodName.contains("Change")){
            periodicCounter.changeCounter.addAndGet(1);
            changeCounter.addAndGet(1);
            updateAverageTime(elapsedTime, averageChangeTime);
        }   else if (methodName.contains("take") || methodName.contains("Take")){
            periodicCounter.takeCounter.addAndGet(1);
            takeCounter.addAndGet(1);
            updateAverageTime(elapsedTime, averageTakeTime);
        }
    }

    /**
     * Updates cache hit or cache miss counter based on SimplePerformanceItem.cacheHit property
     * //TODO check should this method be used only for read operation?
     * @param performanceItem
     */
    private void updateHitOrMissCounters(SimplePerformanceItem performanceItem) {
        if (performanceItem.getCacheHit()){
            periodicCounter.cacheHitCounter.addAndGet(1);
            cacheHitCounter.addAndGet(1);
        }   else {
            periodicCounter.cacheMissCounter.addAndGet(1);
            cacheMissCounter.addAndGet(1);
        }
    }

    /**
     * resets the counter and returns old result
     * @return
     */
    public PeriodicMetricsCounter refreshTimedMetrics(){
        PeriodicMetricsCounter oldMetrics = periodicCounter;
        periodicCounter = new PeriodicMetricsCounter();
        return oldMetrics;
    }

    /**
     * The purpose of this tricky method is to updateAverageTime for different operations
     * There are no Atomic floating point numbers in java, so AtomicInteger holds float bits
     *
     * @param elapsedTime
     * @param averageTime
     * @return
     */
    private boolean updateAverageTime(Integer elapsedTime, AtomicInteger averageTime) {
        float oldAverageFloat = Float.intBitsToFloat(averageTime.get());
        float newAverageFloat = averageCounter.average(oldAverageFloat, elapsedTime);
        int newIntBits = Float.floatToIntBits(newAverageFloat);
        return averageTime.compareAndSet(averageTime.get(), newIntBits);
    }

    @Override
    public String toString() {
        return "SpaceProxyCounter{" +
                "readCounter=" + readCounter +
                ", averageReadTime=" + Float.intBitsToFloat(averageReadTime.get()) +
                ", writeCounter=" + writeCounter +
                ", averageWriteTime=" + Float.intBitsToFloat(averageWriteTime.get()) +
                ", changeCounter=" + changeCounter +
                ", averageChangeTime=" + Float.intBitsToFloat(averageChangeTime.get()) +
                ", takeCounter=" + takeCounter +
                ", averageTakeTime=" + Float.intBitsToFloat(averageTakeTime.get()) +
                ", cacheHitCounter=" + cacheHitCounter +
                ", cacheMissCounter=" + cacheMissCounter +
                '}';
    }

    @Required
    public void setAverageCounter(ExponentialAverageCounter averageCounter) {
        this.averageCounter = averageCounter;
    }

    public PeriodicMetricsCounter getPeriodicCounter() {
        return periodicCounter;
    }

    public Integer getReadCounter() {
        return readCounter.get();
    }

    public Float getAverageReadTime() {
        return Float.intBitsToFloat(averageReadTime.get());
    }

    public Integer getWriteCounter() {
        return writeCounter.get();
    }

    public Float getAverageWriteTime() {
        return Float.intBitsToFloat(averageWriteTime.get());
    }

    public Integer getChangeCounter() {
        return changeCounter.get();
    }

    public Float getAverageChangeTime() {
        return Float.intBitsToFloat(averageChangeTime.get());
    }

    public Integer getTakeCounter() {
        return takeCounter.get();
    }

    public Float getAverageTakeTime() {
        return Float.intBitsToFloat(averageTakeTime.get());
    }

    public Integer getCacheHitCounter() {
        return cacheHitCounter.get();
    }

    public Integer getCacheMissCounter() {
        return cacheMissCounter.get();
    }

    public static class PeriodicMetricsCounter {

        AtomicInteger readCounter = new AtomicInteger(0);

        AtomicInteger writeCounter = new AtomicInteger(0);

        AtomicInteger changeCounter = new AtomicInteger(0);

        AtomicInteger takeCounter = new AtomicInteger(0);

        AtomicInteger cacheHitCounter = new AtomicInteger(0);

        AtomicInteger cacheMissCounter = new AtomicInteger(0);

        @Override
        public String toString() {
            return "PeriodicMetricsCounter{" +
                    "readCounter=" + readCounter +
                    ", writeCounter=" + writeCounter +
                    ", changeCounter=" + changeCounter +
                    ", takeCounter=" + takeCounter +
                    ", cacheHitCounter=" + cacheHitCounter +
                    ", cacheMissCounter=" + cacheMissCounter +
                    '}';
        }
    }
}
