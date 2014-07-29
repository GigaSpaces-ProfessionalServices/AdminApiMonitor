package com.gigaspaces.sbp.metrics_source.adminapi;

import java.util.Date;

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
