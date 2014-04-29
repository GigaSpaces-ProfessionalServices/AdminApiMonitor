package com.gigaspaces.monitoring.metrics_source.space_proxy;

import java.util.concurrent.atomic.AtomicInteger;

public class SpaceProxyCounter {

    AtomicInteger readCounter = new AtomicInteger(0);

    AtomicInteger writeCounter = new AtomicInteger(0);

    AtomicInteger changeCounter = new AtomicInteger(0);

    AtomicInteger takeCounter = new AtomicInteger(0);

    public SpaceProxyCounter() {
    }

    public SpaceProxyCounter(AtomicInteger readCounter, AtomicInteger writeCounter, AtomicInteger changeCounter, AtomicInteger takeCounter) {
        this.readCounter = readCounter;
        this.writeCounter = writeCounter;
        this.changeCounter = changeCounter;
        this.takeCounter = takeCounter;
    }

    @Override
    public String toString() {
        return "SpaceProxyCounter{" +
                "readCounter=" + readCounter +
                ", writeCounter=" + writeCounter +
                ", changeCounter=" + changeCounter +
                ", takeCounter=" + takeCounter +
                '}';
    }
}
