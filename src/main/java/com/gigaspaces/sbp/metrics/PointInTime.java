package com.gigaspaces.sbp.metrics;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 8/5/14
 * Time: 3:06 PM
 * Provides a concretion for an instantaneous collection period.
 */
public class PointInTime implements CollectionPeriod{

    private final long startedAt;

    public PointInTime(){
        this(new Date());
    }

    public PointInTime(Long startedAt) {
        assert startedAt != null : "startedAt cannot be null";
        this.startedAt = startedAt;
    }

    public PointInTime(Date startedAt){
        assert startedAt != null : "startedAt cannot be null";
        this.startedAt = startedAt.getTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long collectionStartedAt() {
        return startedAt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getCount() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TimeUnit getUnit() {
        return TimeUnit.MILLISECONDS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEndedAt(long endedAt) {
        // noOp code smell = refused bequest
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PointInTime{");
        sb.append("startedAt=").append(startedAt);
        sb.append('}');
        return sb.toString();
    }
}
