package com.gigaspaces.monitoring.metrics_source.space_proxy;

import com.gigaspaces.monitoring.metrics_source.counter.ExponentialAverageCounter;
import org.springframework.beans.factory.annotation.Required;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

public class SpaceProxyCounter {

    private ExponentialAverageCounter averageCounter;

    private PeriodicMetricsCounter periodicCounter = new PeriodicMetricsCounter();

    private AtomicInteger readCounter = new AtomicInteger(0);

    private AtomicInteger averageReadTime = new AtomicInteger(0);

    private AtomicInteger writeCounter = new AtomicInteger(0);

    private AtomicInteger averageWriteTime = new AtomicInteger(0);

    private AtomicInteger changeCounter = new AtomicInteger(0);

    private AtomicInteger averageChangeTime = new AtomicInteger(0);

    private AtomicInteger takeCounter = new AtomicInteger(0);

    private AtomicInteger averageTakeTime = new AtomicInteger(0);

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

    public static class PeriodicMetricsCounter {

        private AtomicInteger readCounter = new AtomicInteger(0);

        private AtomicInteger writeCounter = new AtomicInteger(0);

        private AtomicInteger changeCounter = new AtomicInteger(0);

        private AtomicInteger takeCounter = new AtomicInteger(0);

        @Override
        public String toString() {
            return "PeriodicMetricsCounter{" +
                    "readCounter=" + readCounter +
                    ", writeCounter=" + writeCounter +
                    ", changeCounter=" + changeCounter +
                    ", takeCounter=" + takeCounter +
                    '}';
        }

        public AtomicInteger getReadCounter() {
            return readCounter;
        }

        public AtomicInteger getWriteCounter() {
            return writeCounter;
        }

        public AtomicInteger getChangeCounter() {
            return changeCounter;
        }

        public AtomicInteger getTakeCounter() {
            return takeCounter;
        }
    }
}
