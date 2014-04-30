package com.gigaspaces.monitoring.metrics_source.space_proxy;

import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

public class SpaceProxyNotificationListener implements NotificationListener, NotificationFilter {

    private SpaceProxyCounter spaceProxyCounter;

    @Override
    public boolean isNotificationEnabled(Notification notification) {
        return Notification.class.isAssignableFrom(notification.getClass());
    }

    @Override
    public void handleNotification(Notification notification, Object handback) {
        SimplePerformanceItem userData = (SimplePerformanceItem) notification.getUserData();
        spaceProxyCounter.count(userData);
        System.out.println("COUNTER = " + spaceProxyCounter);
    }

    public SpaceProxyCounter getSpaceProxyCounter() {
        return spaceProxyCounter;
    }

    public void setSpaceProxyCounter(SpaceProxyCounter spaceProxyCounter) {
        this.spaceProxyCounter = spaceProxyCounter;
    }
}
