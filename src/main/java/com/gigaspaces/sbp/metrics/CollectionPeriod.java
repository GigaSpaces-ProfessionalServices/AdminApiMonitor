package com.gigaspaces.sbp.metrics;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 8/5/14
 * Time: 3:05 PM
 */
public interface CollectionPeriod {

    public TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;

    /**
     * @return point in time when collection started
     */
    long collectionStartedAt();

    /**
     * @return number of time units elapsed between startedAt and the end of collection
     */
    long getCount();

    /**
     * @return unit of temporal measure
     */
    TimeUnit getUnit();

    /**
     * @param endedAt when collection ended
     */
    void setEndedAt(long endedAt);

}

class CalculateDuration {

    long timeBetween(Date d1, Date d2){
        if( d1.before(d2)) return d2.getTime() - d1.getTime();
        return d1.getTime() - d2.getTime();
    }

    long secondsBetween(Date d1, Date d2){
        long millisBetween = timeBetween(d1, d2);
        return millisBetween / 1000;
    }

    long minutesBetween(Date d1, Date d2){
        long secondsBetween = secondsBetween(d1, d2);
        return secondsBetween / 60;
    }

    long hoursBetween(Date d1, Date d2){
        long minutesBetween = minutesBetween(d1, d2);
        return minutesBetween / 60;
    }

    long daysBetween(Date d1, Date d2){
        long hoursBetween = hoursBetween(d1, d2);
        return hoursBetween / 24;
    }

}