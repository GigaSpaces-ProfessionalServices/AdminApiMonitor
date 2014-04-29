package com.gigaspaces.monitoring.metrics_source.space_proxy;

import java.util.concurrent.atomic.AtomicInteger;

public class SpaceProxyCounter {

    AtomicInteger readCounter;

    AtomicInteger writeCounter;

    AtomicInteger changeCounter;

    AtomicInteger takeCounter;
}
