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
        System.out.println("Received notification");
        SimplePerformanceItem userData = (SimplePerformanceItem) notification.getUserData();
        System.out.println("CLASSname = " + userData.getSourceClassName());
        String methodName = userData.getSourceMethodName();
        incrementCounter(methodName);
        System.out.println("METHOD name = " + methodName);
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
}
