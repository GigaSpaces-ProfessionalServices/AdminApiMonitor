package com.gigaspaces.sbp.metrics;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 8/4/14
 * Time: 5:47 PM
 *
 * A collection of a bunch of names for metrics we can easily gather from Admin API.
 * And a few that customers want us to gather anyway.
 */

/*
 * A Simple marker interface.
 */
public interface NamedMetric {
    String displayName();
}

enum GigaSpacesClusterInfo implements NamedMetric {

    SPACE_MODE("space_mode")
    ;

    private final String displayName;

    GigaSpacesClusterInfo(String displayName){
        this.displayName = displayName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String displayName() {
        return displayName;
    }

}

enum GigaSpacesActivity implements NamedMetric {

    READ_COUNT("reads")
    , WRITE_COUNT("writes")
    , EXECUTE_COUNT("executes")
    , TAKE_COUNT("takes")
    , UPDATE_COUNT("updates") // also applies to "change'
    , TOTAL_EVENT_COUNT("events")
    , OWNED_LOCK_COUNT("owned_locks")
    , LRMI_CONNECTIONS("lrmi_connections")
    , TRANSACTION_COUNT("open_transactions")
    ;

    private final String displayName;

    GigaSpacesActivity(String displayName) {
        this.displayName = displayName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String displayName() {
        return displayName;
    }

}

enum GsMirrorInfo implements NamedMetric {

    REDO_LOG_SIZE("redo_log_size")
    , REDO_LOG_SEND_BYTES_PER_SECOND("mirror_sent_bytes_per_sec")
    , MIRROR_OPERATIONS("mirror_total_operations")
    , MIRROR_SUCCESSFUL_OPERATIONS("mirror_successes")
    , MIRROR_FAILED_OPERATIONS("mirror_failures")
    ;

    private final String displayName;

    GsMirrorInfo(String displayName){
        this.displayName = displayName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String displayName() {
        return null;
    }

}

enum OperatingSystemInfo implements NamedMetric {

    OPEN_FILE_DESCRIPTOR_COUNT("open_file_descriptors")
    , MAX_FILE_DESCRIPTOR_COUNT("max_file_descriptors")
    ;

    private final String displayName;

    OperatingSystemInfo(String displayName){
        this.displayName = displayName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String displayName() {
        return null;
    }

}

enum JvmInfo implements NamedMetric {

    GARBAGE_COLLECTION_COUNT("gc_count")
    , GARBAGE_COLLECTION_TIME_IN_SECONDS("total_gc_time_secs")
    , THREAD_COUNT("threads")
    , JVM_UPTIME("jvm_uptime_secs")
    , CPU_LOAD("cpu_usage")
    ;

    private final String displayName;

    JvmInfo(String displayName) {
        this.displayName = displayName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String displayName() {
        return displayName;
    }

}

enum Memory implements NamedMetric {

    TOTAL_BYTES("bytes_allocated") // "allocated"; -Xmn <= allocated <= -Xmx
    , NON_HEAP_USED_BYTES("non_heap_used_bytes") // OS & JVM native
    , HEAP_USED_BYTES("head_used_bytes") // "working set" ~= application memory
    , HEAP_NON_COMMITTED_BYTES("heap_non_committed_bytes") // "JVM"; -Xmx minus HEAP_COMMITTED_BYTES
    , HEAP_COMMITTED_BYTES("head_committed_bytes") // "committed"; NON_HEAP_USED_BYTES + HEAP_USED_BYTES + garbage
    ;

    private final String displayName;

    Memory(String displayName) {
        this.displayName = displayName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String displayName() {
        return displayName;
    }

}