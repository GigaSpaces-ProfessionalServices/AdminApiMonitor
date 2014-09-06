package com.gigaspaces.sbp.metrics;

import java.util.Date;

public class AdminApiMetrics {

    Date timestamp; // 0

    // HOST INFO
    String hostname; // 1
    Long pid; // 2
    int partition; // 3

    // ACTIVITY STATS
    Long readCountPerSecond;
    Long updateCountPerSecond;
    Long writeCountPerSecond;
    Long changePerSecond;
    Long executePerSecond;

    Long activeTransactionCount;
    Long processorQueueSize;

    // MEMORY STATS
    Long totalMemoryInBytes;
    Long nonHeapUsedMemoryInBytes; // 9
    Long heapUsedMemoryInBytes; // 10
    Long gcCollectionCount; // 13

    // MIRROR STATS
    Double redoLogSize;
    Double redoLogSendBytesPerSecond;
    Long mirrorTotalOperations;
    Long mirrorSuccessfulOperations;
    Long mirrorFailedOperations;

    Double cpuPercent;
    Double totalThreads; // 15

}