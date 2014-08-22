package com.gigaspaces.sbp.metrics.metric;

import com.gigaspaces.sbp.metrics.FullMetric;
import com.gigaspaces.sbp.metrics.visitor.StatsVisitor;
import org.openspaces.admin.gsc.GridServiceContainer;
import org.openspaces.admin.vm.VirtualMachineDetails;
import org.openspaces.admin.vm.VirtualMachineStatistics;

import java.util.List;

public enum Memory implements NamedMetric {

    TOTAL_BYTES("bytes_allocated") { // "allocated"; -Xmn <= allocated <= -Xmx
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            List<GridServiceContainer> gridServiceContainers = statsVisitor.gridServiceContainers();
            for (GridServiceContainer gridServiceContainer : gridServiceContainers){
                VirtualMachineDetails details = gridServiceContainer.getVirtualMachine().getDetails();
                if( details == null ) return;
                Long heap = details.getMemoryHeapMaxInBytes();
                Long nonHeap = details.getMemoryNonHeapInitInBytes();
                if( heap == null || nonHeap == null ) return;
                Long bytes = heap + nonHeap;
                statsVisitor.saveStat(new FullMetric(this, bytes.toString(), gridServiceContainer));
            }
        }
    }
    , NON_HEAP_USED_BYTES("non_heap_used_bytes") { // OS & JVM native
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            List<GridServiceContainer> gridServiceContainers = statsVisitor.gridServiceContainers();
            for (GridServiceContainer gridServiceContainer : gridServiceContainers){
                VirtualMachineDetails details = gridServiceContainer.getVirtualMachine().getDetails();
                if( details == null ) return;
                Long nonHeap = details.getMemoryNonHeapInitInBytes();
                if( nonHeap == null ) return;
                statsVisitor.saveStat(new FullMetric(this, nonHeap.toString(), gridServiceContainer));
            }
        }
    }
    , HEAP_USED_BYTES("heap_used_bytes") { // "working set" ~= application memory
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            List<GridServiceContainer> gridServiceContainers = statsVisitor.gridServiceContainers();
            for (GridServiceContainer gridServiceContainer : gridServiceContainers){
                VirtualMachineDetails details = gridServiceContainer.getVirtualMachine().getDetails();
                Long heap = details.getMemoryHeapInitInBytes();
                if( heap == null ) return;
                FullMetric fullMetric = new FullMetric(this, heap.toString(), gridServiceContainer);
                statsVisitor.saveStat(fullMetric);
            }
        }
    }
    , NON_HEAP_COMMITTED_BYTES("non_heap_committed_bytes") { // "JVM"; -Xmx minus HEAP_COMMITTED_BYTES
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            List<GridServiceContainer> gridServiceContainers = statsVisitor.gridServiceContainers();
            for (GridServiceContainer gridServiceContainer : gridServiceContainers){
                VirtualMachineStatistics stats = gridServiceContainer.getVirtualMachine().getStatistics();
                Long nonHeapCommitted = stats.getMemoryNonHeapCommittedInBytes();
                if( nonHeapCommitted == null ) return;
                statsVisitor.saveStat(new FullMetric(this, nonHeapCommitted.toString(), gridServiceContainer));
            }
        }
    }
    , HEAP_COMMITTED_BYTES("heap_committed_bytes") { // "committed"; NON_HEAP_USED_BYTES + HEAP_USED_BYTES + garbage
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            List<GridServiceContainer> gridServiceContainers = statsVisitor.gridServiceContainers();
            for (GridServiceContainer gridServiceContainer : gridServiceContainers){
                VirtualMachineStatistics stats = gridServiceContainer.getVirtualMachine().getStatistics();
                Long heapCommitted = stats.getMemoryHeapCommittedInBytes();
                if( heapCommitted == null ) return;
                statsVisitor.saveStat(new FullMetric(this, heapCommitted.toString(), gridServiceContainer));
            }
        }
    }
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
