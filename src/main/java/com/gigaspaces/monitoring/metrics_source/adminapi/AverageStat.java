package com.gigaspaces.monitoring.metrics_source.adminapi;

import java.util.Date;

/**
 * Modified Stat class to hold double average values
 */
public class AverageStat {
    long pid;
    String hostname;
    Date timestamp;
    double totalMemory;
    double heapUsedMemory;
    double nonHeapUsedMemory;
    double totalThreads;
    double cpuPercent;
    double redologSize;
    double redologSendBytesPerSecond;
    long gcCollectionCount;
    long mirrorTotalOperations;
    long mirrorSuccessfulOperations;
    long mirrorFailedOperations;
    double readCountPerSecond;
    double updateCountPerSecond;
    double writeCountPerSecond;
    double changePerSecond;
    double executePerSecond;
    double processorQueueSize;
    double activeTransactionCount;

}
