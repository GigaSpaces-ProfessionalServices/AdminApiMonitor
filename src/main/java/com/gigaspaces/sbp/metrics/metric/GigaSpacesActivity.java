package com.gigaspaces.sbp.metrics.metric;

import com.gigaspaces.sbp.metrics.FullMetric;
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
                    readCount = stats.getReadCount();
                    FullMetric fullMetric = new FullMetric.FullMetricBuilder().
                            metric(this).
                            metricValue(String.valueOf(readCount)).
                            spaceInstanceID(spaceInstance.getSpaceInstanceName()).
                            create();
                    visitor.saveStat(fullMetric);
                }
            }
        }
    }
    , READ_PER_SEC("reads_per_sec"){
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            Double readsPerSec = 0.0;
            for (SpaceInstance spaceInstance : visitor.spaceInstance()){
                if( spaceInstance != null ){
                    SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                    readsPerSec = stats.getReadPerSecond();
                    FullMetric fullMetric = new FullMetric.FullMetricBuilder().
                            metric(this).
                            metricValue(String.valueOf(readsPerSec)).
                            spaceInstanceID(spaceInstance.getSpaceInstanceName()).
                            create();
                    visitor.saveStat(fullMetric);
                }
            }
        }
    }
    , WRITE_COUNT("writes"){
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            Long writeCount = 0l;
            for (SpaceInstance spaceInstance : visitor.spaceInstance()){
                if( spaceInstance != null ){
                    SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                    writeCount = stats.getWriteCount();
                    FullMetric fullMetric = new FullMetric.FullMetricBuilder().
                            metric(this).
                            metricValue(String.valueOf(writeCount)).
                            spaceInstanceID(spaceInstance.getSpaceInstanceName()).
                            create();
                    visitor.saveStat(fullMetric);
                }
            }
        }
    }
    , WRITES_PER_SEC("writes_per_sec"){
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            Double writesPerSec = 0.0;
            for (SpaceInstance spaceInstance : visitor.spaceInstance()){
                if( spaceInstance != null ){
                    SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                    writesPerSec = stats.getWritePerSecond();
                    FullMetric fullMetric = new FullMetric.FullMetricBuilder().
                            metric(this).
                            metricValue(String.valueOf(writesPerSec)).
                            spaceInstanceID(spaceInstance.getSpaceInstanceName()).
                            create();
                    visitor.saveStat(fullMetric);
                }
            }
        }
    }
    , EXECUTE_COUNT("executes"){
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            Long executeCount = 0l;
            for (SpaceInstance spaceInstance : visitor.spaceInstance()){
                if( spaceInstance != null ){
                    SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                    executeCount = stats.getExecuteCount();
                    FullMetric fullMetric = new FullMetric.FullMetricBuilder().
                            metric(this).
                            metricValue(String.valueOf(executeCount)).
                            spaceInstanceID(spaceInstance.getSpaceInstanceName()).
                            create();
                    visitor.saveStat(fullMetric);
                }
            }
        }
    }
    , EXECUTES_PER_SEC("executes_per_sec"){
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            Double execPerSec = 0.0;
            for (SpaceInstance spaceInstance : visitor.spaceInstance()){
                if( spaceInstance != null ){
                    SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                    execPerSec = stats.getExecutePerSecond();
                    FullMetric fullMetric = new FullMetric.FullMetricBuilder().
                            metric(this).
                            metricValue(String.valueOf(execPerSec)).
                            spaceInstanceID(spaceInstance.getSpaceInstanceName()).
                            create();
                    visitor.saveStat(fullMetric);
                }
            }
        }
    }
    , TAKE_COUNT("takes"){
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            Long takeCount = 0l;
            for (SpaceInstance spaceInstance : visitor.spaceInstance()){
                if( spaceInstance != null ){
                    SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                    takeCount = stats.getTakeCount();
                    FullMetric fullMetric = new FullMetric.FullMetricBuilder().
                            metric(this).
                            metricValue(String.valueOf(takeCount)).
                            spaceInstanceID(spaceInstance.getSpaceInstanceName()).
                            create();
                    visitor.saveStat(fullMetric);
                }
            }

        }
    }
    , TAKES_PER_SECOND("takes_per_sec"){
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            Double takePerSec;
            for (SpaceInstance spaceInstance : visitor.spaceInstance()){
                if( spaceInstance != null ){
                    SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                    takePerSec = stats.getTakePerSecond();
                    FullMetric fullMetric = new FullMetric.FullMetricBuilder().
                            metric(this).
                            metricValue(String.valueOf(takePerSec)).
                            spaceInstanceID(spaceInstance.getSpaceInstanceName()).
                            create();
                    visitor.saveStat(fullMetric);
                }
            }
        }
    }
    , UPDATE_COUNT("updates") { // also applies to "change'
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            Long updateCount = 0l;
            for (SpaceInstance spaceInstance : visitor.spaceInstance()){
                if( spaceInstance != null ){
                    SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                    updateCount = stats.getUpdateCount();
                    FullMetric fullMetric = new FullMetric.FullMetricBuilder().
                            metric(this).
                            metricValue(String.valueOf(updateCount)).
                            spaceInstanceID(spaceInstance.getSpaceInstanceName()).
                            create();
                    visitor.saveStat(fullMetric);
                }
            }
        }
    }
    , UPDATES_PER_SEC("updates_per_sec") { // also applies to "change'
        public void accept(StatsVisitor visitor){
            if( visitor == null ) return;
            Double updatesPerSec = 0.0;
            for (SpaceInstance spaceInstance : visitor.spaceInstance()){
                if( spaceInstance != null ){
                    SpaceInstanceStatistics stats = spaceInstance.getStatistics();
                    updatesPerSec = stats.getUpdatePerSecond();
                    FullMetric fullMetric = new FullMetric.FullMetricBuilder().
                            metric(this).
                            metricValue(String.valueOf(updatesPerSec)).
                            spaceInstanceID(spaceInstance.getSpaceInstanceName()).
                            create();
                    visitor.saveStat(fullMetric);
                }
            }
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
            FullMetric fullMetric = new FullMetric.FullMetricBuilder().metric(this).metricValue(String.valueOf(activeTransactions)).create();
            visitor.saveStat(fullMetric);
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
            FullMetric fullMetric = new FullMetric.FullMetricBuilder().metric(this).metricValue(String.valueOf(connectionCount)).create();
            visitor.saveStat(fullMetric);
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
