package com.gigaspaces.sbp.metrics.metric;

import com.gigaspaces.sbp.metrics.visitor.StatsVisitor;
import org.openspaces.admin.GridComponent;
import org.openspaces.admin.vm.VirtualMachineDetails;
import org.openspaces.admin.vm.VirtualMachineStatistics;

import java.util.List;
import java.util.Map;

public enum Memory implements GridComponentMetric {

    TOTAL_BYTES("bytes_allocated") { // "allocated"; -Xmn <= allocated <= -Xmx
        @Override
        public String getMetricValue(GridComponent gridComponent) {
            VirtualMachineDetails details = gridComponent.getVirtualMachine().getDetails();
            if( details == null ) return null;
            Long heap = details.getMemoryHeapMaxInBytes();
            Long nonHeap = details.getMemoryNonHeapInitInBytes();
            if( heap == null || nonHeap == null ) return null;
            Long bytes = heap + nonHeap;
            return String.valueOf(bytes);
        }
    }
    , NON_HEAP_USED_BYTES("non_heap_used_bytes") { // OS & JVM native
        @Override
        public String getMetricValue(GridComponent gridComponent) {
            VirtualMachineDetails details = gridComponent.getVirtualMachine().getDetails();
            if( details == null ) return null;
            Long nonHeap = details.getMemoryNonHeapInitInBytes();
            if( nonHeap == null ) return null;
            return String.valueOf(nonHeap);
        }
    }
    , HEAP_USED_BYTES("heap_used_bytes") { // "working set" ~= application memory
        @Override
        public String getMetricValue(GridComponent gridComponent) {
            VirtualMachineDetails details = gridComponent.getVirtualMachine().getDetails();
            Long heap = details.getMemoryHeapInitInBytes();
            if( heap == null ) return null;
            return String.valueOf(heap);
        }
    }
    , NON_HEAP_COMMITTED_BYTES("non_heap_committed_bytes") { // "JVM"; -Xmx minus HEAP_COMMITTED_BYTES
        @Override
        public String getMetricValue(GridComponent gridComponent) {
            VirtualMachineStatistics stats = gridComponent.getVirtualMachine().getStatistics();
            Long nonHeapCommitted = stats.getMemoryNonHeapCommittedInBytes();
            if( nonHeapCommitted == null ) return null;
            return String.valueOf(nonHeapCommitted);
        }
    }
    , HEAP_COMMITTED_BYTES("heap_committed_bytes") { // "committed"; NON_HEAP_USED_BYTES + HEAP_USED_BYTES + garbage
        @Override
        public String getMetricValue(GridComponent gridComponent) {
            VirtualMachineStatistics stats = gridComponent.getVirtualMachine().getStatistics();
            Long heapCommitted = stats.getMemoryHeapCommittedInBytes();
            if( heapCommitted == null ) return null;
            return String.valueOf(heapCommitted);
        }
    }, COMMITTED_BYTES_PCT("heap_committed_pct") {
        @Override
        public String getMetricValue(GridComponent gridComponent) {
            VirtualMachineStatistics stats = gridComponent.getVirtualMachine().getStatistics();
            Long heapCommitted = stats.getMemoryHeapCommittedInBytes();
            VirtualMachineDetails details = gridComponent.getVirtualMachine().getDetails();
            Long heapMax = details.getMemoryHeapMaxInBytes();
            if( heapCommitted == null || heapMax == null) return null;
            Double freeMemory = (heapCommitted / Double.valueOf(heapMax)) * 100;
            return String.valueOf(freeMemory);
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

    @Override
    public void accept(StatsVisitor statsVisitor) {
        Map<String, List<? extends GridComponent>> gridComponentsByType = JvmInfo.getGridComponentsByType(statsVisitor);
        for (String type : gridComponentsByType.keySet()){
            List<? extends GridComponent> gridComponents = gridComponentsByType.get(type);
            for (GridComponent gridComponent: gridComponents){
                FullMetric fullMetric = new FullMetric.FullMetricBuilder().
                        metric(this).
                        metricValue(getMetricValue(gridComponent)).
                        hostName(gridComponent.getMachine().getHostName()).
                        gridComponentName(type).
                        pid(gridComponent.getVirtualMachine().getDetails().getPid()).
                        create();
                statsVisitor.saveStat(fullMetric);
            }
        }
    }

}
