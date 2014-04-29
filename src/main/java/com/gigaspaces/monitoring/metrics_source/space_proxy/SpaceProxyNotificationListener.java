package com.gigaspaces.monitoring.metrics_source.space_proxy;

import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

public class SpaceProxyNotificationListener implements NotificationListener, NotificationFilter {

    private SpaceProxyCounter spaceProxyCounter = new SpaceProxyCounter();

    @Override
    public boolean isNotificationEnabled(Notification notification) {
        return Notification.class.isAssignableFrom(notification.getClass());
    }

    @Override
    public void handleNotification(Notification notification, Object handback) {
        SimplePerformanceItem userData = (SimplePerformanceItem) notification.getUserData();
        String methodName = userData.getSourceMethodName();
        incrementCounter(methodName);
        System.out.println("COUNTER = " + spaceProxyCounter);
    }

    private void incrementCounter(String methodName) {
        //TODO find better way to match method and counter
        if (methodName.contains("write") || methodName.contains("Write")){
            spaceProxyCounter.writeCounter.addAndGet(1);
        }   else if (methodName.contains("read") || methodName.contains("Read")){
            spaceProxyCounter.readCounter.addAndGet(1);
        }   else if (methodName.contains("change") || methodName.contains("Change")){
            spaceProxyCounter.changeCounter.addAndGet(1);
        }   else if (methodName.contains("take") || methodName.contains("Take")){
            spaceProxyCounter.takeCounter.addAndGet(1);
        }
    }

    public SpaceProxyCounter getSpaceProxyCounter() {
        return spaceProxyCounter;
    }
}
