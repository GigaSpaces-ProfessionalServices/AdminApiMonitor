package com.gigaspaces.sbp.metrics.metric;

import com.gigaspaces.cluster.replication.async.mirror.MirrorStatistics;
import com.gigaspaces.sbp.metrics.FullMetric;
import com.gigaspaces.sbp.metrics.visitor.StatsVisitor;
import com.j_spaces.core.filters.ReplicationStatistics;

import java.util.List;

public enum GsMirrorInfo implements NamedMetric {

    REDO_LOG_SIZE("redo_log_size"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            Long logSize = 0l;
            for (ReplicationStatistics replicationStatistics : statsVisitor.replicationStatistics()){
                if( replicationStatistics == null ) return;
                ReplicationStatistics.OutgoingReplication out = replicationStatistics.getOutgoingReplication();
                if( out == null ) return;
                logSize += out.getRedoLogSize();
            }
            statsVisitor.saveStat(new FullMetric(this, String.valueOf(logSize)));
        }
    }
    , REDO_LOG_SEND_BYTES_PER_SECOND("mirror_sent_bytes_per_sec"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            long byteCount = 0;
            for (ReplicationStatistics replicationStatistics : statsVisitor.replicationStatistics()){
                if( replicationStatistics == null ) return;
                ReplicationStatistics.OutgoingReplication out = replicationStatistics.getOutgoingReplication();
                if( out == null ) return;
                List<ReplicationStatistics.OutgoingChannel> channelList = out.getChannels();
                if( channelList == null ) return;
                for(ReplicationStatistics.OutgoingChannel channel : channelList ){
                    if( channel != null ) byteCount += channel.getSendBytesPerSecond();
                }
            }
            statsVisitor.saveStat(new FullMetric(this, String.valueOf(byteCount)));
        }
    }
    , MIRROR_OPERATIONS("mirror_total_operations"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            Long operationCountSum = 0l;
            for (MirrorStatistics mirrorStatistics : statsVisitor.mirrorStatistics()){
                if( mirrorStatistics == null ) return;
                Long operationCount = mirrorStatistics.getOperationCount();
                if( operationCount == null ) return;
                operationCountSum += operationCount;
            }
            statsVisitor.saveStat(new FullMetric(this, operationCountSum.toString()));
        }
    }
    , MIRROR_SUCCESSFUL_OPERATIONS("mirror_successes"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            Long successfulOperationsSum = 0l;
            for (MirrorStatistics mirrorStatistics : statsVisitor.mirrorStatistics()){
                if( mirrorStatistics == null ) return;
                Long successfulOperations = mirrorStatistics.getSuccessfulOperationCount();
                if( successfulOperations == null ) return;
                successfulOperationsSum += successfulOperations;
            }
            statsVisitor.saveStat(new FullMetric(this, successfulOperationsSum.toString()));
        }
    }
    , MIRROR_FAILED_OPERATIONS("mirror_failures"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            if( statsVisitor == null ) return;
            Long failedOperationsSum = 0l;
            for (MirrorStatistics mirrorStatistics : statsVisitor.mirrorStatistics()){
                if( mirrorStatistics == null ) return;
                Long failedOperations = mirrorStatistics.getFailedOperationCount();
                if( failedOperations == null ) return;
                failedOperationsSum += failedOperations;
            }
            statsVisitor.saveStat(new FullMetric(this, failedOperationsSum.toString()));
        }
    }
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
        return displayName;
    }

}
