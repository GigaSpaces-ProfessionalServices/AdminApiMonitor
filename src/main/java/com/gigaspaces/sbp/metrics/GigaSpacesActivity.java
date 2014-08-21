package com.gigaspaces.sbp.metrics;

import com.gigaspaces.sbp.metrics.visitor.StatsVisitor;
import org.openspaces.admin.space.SpaceInstance;
import org.openspaces.admin.space.SpaceInstanceStatistics;

public enum GigaSpacesActivity implements NamedMetric {

    READ_COUNT("reads"){
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            Long readCount = 0l;
            for (SpaceInstance spaceInstance : visitor.spaceInstance()){
                if( spaceInstance != null ){
                    SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                     readCount += stats.getReadCount();

                }
            }
            visitor.saveStat(new FullMetric(this, String.valueOf(readCount)));
        }
    }
    , READ_PER_SEC("reads_per_sec"){
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            Double readsPerSec = 0.0;
            for (SpaceInstance spaceInstance : visitor.spaceInstance()){
                if( spaceInstance != null ){
                    SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                    readsPerSec += stats.getReadPerSecond();
                }
            }
            visitor.saveStat(new FullMetric(this, String.valueOf(readsPerSec)));
        }
    }
    , WRITE_COUNT("writes"){
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            Long writeCount = 0l;
            for (SpaceInstance spaceInstance : visitor.spaceInstance()){
                if( spaceInstance != null ){
                    SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                    writeCount += stats.getWriteCount();
                }
            }
            visitor.saveStat(new FullMetric(this, String.valueOf(writeCount)));
        }
    }
    , WRITES_PER_SEC("writes_per_sec"){
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            Double writesPerSec = 0.0;
            for (SpaceInstance spaceInstance : visitor.spaceInstance()){
                if( spaceInstance != null ){
                    SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                    writesPerSec += stats.getWritePerSecond();
                }
            }
            visitor.saveStat(new FullMetric(this, String.valueOf(writesPerSec)));
        }
    }
    , EXECUTE_COUNT("executes"){
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            Long executeCount = 0l;
            for (SpaceInstance spaceInstance : visitor.spaceInstance()){
                if( spaceInstance != null ){
                    SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                    executeCount += stats.getExecuteCount();
                }
            }
            visitor.saveStat(new FullMetric(this, String.valueOf(executeCount)));
        }
    }
    , EXECUTES_PER_SEC("executes_per_sec"){
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            Double execPerSec = 0.0;
            for (SpaceInstance spaceInstance : visitor.spaceInstance()){
                if( spaceInstance != null ){
                    SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                    execPerSec += stats.getExecutePerSecond();
                }
            }
            visitor.saveStat(new FullMetric(this, String.valueOf(execPerSec)));
        }
    }
    , TAKE_COUNT("takes"){
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            Long takeCount = 0l;
            for (SpaceInstance spaceInstance : visitor.spaceInstance()){
                if( spaceInstance != null ){
                    SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                    takeCount += stats.getTakeCount();
                }
            }
            visitor.saveStat(new FullMetric(this, String.valueOf(takeCount)));
        }
    }
    , TAKES_PER_SECOND("takes_per_sec"){
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            Double takePerSec = 0.0;
            for (SpaceInstance spaceInstance : visitor.spaceInstance()){
                if( spaceInstance != null ){
                    SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                    takePerSec += stats.getTakePerSecond();
                }
            }
            visitor.saveStat(new FullMetric(this, String.valueOf(takePerSec)));
        }
    }
    , UPDATE_COUNT("updates") { // also applies to "change'
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            Long updateCount = 0l;
            for (SpaceInstance spaceInstance : visitor.spaceInstance()){
                if( spaceInstance != null ){
                    SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                    updateCount += stats.getUpdateCount();
                }
            }
            visitor.saveStat(new FullMetric(this, String.valueOf(updateCount)));
        }
    }
    , UPDATES_PER_SEC("updates_per_sec") { // also applies to "change'
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            Double updatesPerSec = 0.0;
            for (SpaceInstance spaceInstance : visitor.spaceInstance()){
                if( spaceInstance != null ){
                    SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                    updatesPerSec += stats.getUpdatePerSecond();
                }
            }
            visitor.saveStat(new FullMetric(this, String.valueOf(updatesPerSec)));
        }
    }
    , TRANSACTION_COUNT("active_transactions"){
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            Long activeTransactions = 0l;
            for (SpaceInstance spaceInstance : visitor.spaceInstance()){
                if( spaceInstance != null ){
                    SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                    activeTransactions += stats.getActiveTransactionCount();
                }
            }
            visitor.saveStat(new FullMetric(this, String.valueOf(activeTransactions)));
        }
    }
    , CONNECTION_COUNT("active_connections"){
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            Long connectionCount = 0l;
            for (SpaceInstance spaceInstance : visitor.spaceInstance()){
                if( spaceInstance != null ){
                    SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                    connectionCount += stats.getActiveConnectionCount();
                }
            }
            visitor.saveStat(new FullMetric(this, String.valueOf(connectionCount)));
        }
    }
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
