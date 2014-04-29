/**
 *
 */
package com.gigaspaces.monitoring.metrics_source.space_proxy;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

/**
 * @author tsarver
 *
 */
public class TestNotificationListener implements NotificationListener,
		NotificationFilter {

	private static final long serialVersionUID = -4459656587149768562L;

	public static final DateFormat DEFAULT_FORMAT = new SimpleDateFormat("YYYY-dd-MM HH:mm:ss:SSS");

	/* (non-Javadoc)
	 * @see javax.management.NotificationFilter#isNotificationEnabled(javax.management.Notification)
	 */
	@Override
	public boolean isNotificationEnabled(Notification notification) {
        return Notification.class.isAssignableFrom(notification.getClass());
	}

	/* (non-Javadoc)
	 * @see javax.management.NotificationListener#handleNotification(javax.management.Notification, java.lang.Object)
	 */
	@Override
	public void handleNotification(Notification notification, Object handback) {
        System.out.print("Notified: message=[");
        System.out.print(notification.getMessage());
        System.out.print("], timedate=");
        System.out.print(DEFAULT_FORMAT.format(new Date(notification.getTimeStamp())));
        System.out.print(", seq=");
        System.out.print(notification.getSequenceNumber());
        System.out.print(", type=");
        System.out.println(notification.getType());
        Object payload = notification.getUserData();
        if (null != payload) {
        	PerformanceItem perfItem = (PerformanceItem) payload;
        	if (perfItem.isInException()) {
        		System.out.print("\tException=");
        		System.out.println(perfItem.getExceptionStack());
        	} else {
	        	System.out.print("\tCompleted time=");
	        	System.out.println(perfItem.getElapsedTime());
        	}
        }
	}

}
