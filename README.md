AdminApiMonitor
=========================

A little tool that 

Initial requirements:

#### Keep an [exponential moving average](http://stackoverflow.com/questions/9200874/implementing-exponential-moving-average-in-java) updated for each of the following stats:

- long totalMemory
- long heapUsedMemory
- long nonHeapUsedMemory
- int totalThreads
- double cpuPercent
- long redologSize
- long redologSendBytesPerSecond
- long gcCollectionCount
- long mirrorTotalOperations
- long mirrorSuccessfulOperations
- long mirrorFailedOperations
- double readCountPerSecond
- double updateCountPerSecond
- double writeCountPerSecond
- double changePerSecond
- double executePerSecond
- int processorQueueSize
- long activeTransactionCount

#### Add a configuration (to the spring context file, for now) for the following two configuration options: 

- stat sample interval (in seconds)
- stat alpha value 

Apply these two configurations to all of the averaged statistics

#### Add another configuration for logging interval and one for log file name.

Log all stats as well as pid, hostname, and timestamp every time the interval expires.