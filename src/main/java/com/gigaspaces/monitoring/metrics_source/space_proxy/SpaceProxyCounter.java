package com.gigaspaces.monitoring.metrics_source.space_proxy;

import com.gigaspaces.monitoring.metrics_source.counter.ExponentialAverageCounter;
import org.springframework.beans.factory.annotation.Required;

import java.util.concurrent.atomic.AtomicInteger;

public class SpaceProxyCounter {

    private ExponentialAverageCounter averageCounter;

    AtomicInteger readCounter = new AtomicInteger(0);

    AtomicInteger averageReadTime = new AtomicInteger(0);

    AtomicInteger writeCounter = new AtomicInteger(0);

    AtomicInteger averageWriteTime = new AtomicInteger(0);

    AtomicInteger changeCounter = new AtomicInteger(0);

    AtomicInteger averageChangeTime = new AtomicInteger(0);

    AtomicInteger takeCounter = new AtomicInteger(0);

    AtomicInteger averageTakeTime = new AtomicInteger(0);

    public SpaceProxyCounter() {
    }

    public void count(SimplePerformanceItem performanceItem){
        String methodName = performanceItem.getSourceMethodName();
        Integer elapsedTime = performanceItem.getElapsedTime();
        if (methodName.contains("write") || methodName.contains("Write")){
            writeCounter.addAndGet(1);
            updateAverageTime(elapsedTime, averageWriteTime);
        }   else if (methodName.contains("read") || methodName.contains("Read")){
            readCounter.addAndGet(1);
            updateAverageTime(elapsedTime, averageReadTime);
        }   else if (methodName.contains("change") || methodName.contains("Change")){
            changeCounter.addAndGet(1);
            updateAverageTime(elapsedTime, averageChangeTime);
        }   else if (methodName.contains("take") || methodName.contains("Take")){
            takeCounter.addAndGet(1);
            updateAverageTime(elapsedTime, averageTakeTime);
        }
    }

    private boolean updateAverageTime(Integer elapsedTime, AtomicInteger averageTime) {
        return averageTime.compareAndSet(averageTime.get(), Float.floatToIntBits(averageCounter.average(averageTime.get(), elapsedTime)));
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
}
