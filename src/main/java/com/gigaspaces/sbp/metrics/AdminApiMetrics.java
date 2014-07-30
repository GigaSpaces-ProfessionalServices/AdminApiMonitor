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

    Long totalReads; // 4
    Long totalWrites; // 5
    Long totalTakes; // 6
    Long totalExecutions; // 7
    Long objectCountOnHost; // 8
    Long activeTransactionCount;
    Long processorQueueSize;

    // MEMORY STATS
    Long totalMemoryInBytes;
    Long nonHeapUsedMemoryInBytes; // 9
    Long heapUsedMemoryInBytes; // 10
    Long heapNonCommittedInBytes; // 11
    Long heapCommittedInBytes; // 12
    Long gcCollectionCount; // 13
    Long gcTimeInSeconds; // 14

    // MIRROR STATS
    Double redoLogSize;
    Double redoLogSendBytesPerSecond;
    Long mirrorTotalOperations;
    Long mirrorSuccessfulOperations;
    Long mirrorFailedOperations;

    Double cpuPercent;
    Double totalThreads; // 15

    Long upTime; // 16

}