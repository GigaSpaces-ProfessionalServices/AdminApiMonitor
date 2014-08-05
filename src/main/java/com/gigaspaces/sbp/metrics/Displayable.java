package com.gigaspaces.sbp.metrics;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 8/4/14
 * Time: 5:47 PM
 * Provides...
 */
/**
 * Simple marker interface
 */
public interface Displayable {
    String displayName();
}

enum Memory implements Displayable {

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

enum GarbageCollection implements Displayable {

    GARBAGE_COLLECTION_COUNT("garbage_collections")
    , GARBAGE_COLLECTION_TIME_IN_SECONDS("garbage_colletion_time")
    ;

    private final String displayName;

    GarbageCollection(String displayName) {
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

enum Activity implements Displayable {

    READ_COUNT("reads")
    , WRITE_COUNT("writes")
    , EXECUTE_COUNT("executes")
    , TAKE_COUNT("takes")
    , UPDATE_COUNT("updates") // also applies to "change'
    ;

    private final String displayName;

    Activity(String displayName) {
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
