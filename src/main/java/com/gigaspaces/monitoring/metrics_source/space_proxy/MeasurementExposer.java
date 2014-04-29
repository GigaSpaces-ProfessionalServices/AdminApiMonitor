/**
 * 
 */
package com.gigaspaces.monitoring.metrics_source.space_proxy;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

//import org.springframework.jmx.export.notification.NotificationPublisher;
//import org.springframework.jmx.export.notification.NotificationPublisherAware;

/**
 * Raises a JMX event with a simple payload with minimal time by the calling
 * thread. <br>
 * This is meant to be a Spring bean exposed as JMX.
 * 
 * @author Toby Sarver
 * 
 */
// Component
// ManagedResource(objectName="com.gigaspaces.monitoring:type=MeasurementExposer")
// implements NotificationPublisherAware
public class MeasurementExposer extends PerformanceSource implements MeasurementExposerMBean, NotificationEmitter {

	public static final String BEAN_ID = "com.gigaspaces.monitoring:name=MeasurementExposer";
	public static final String JMX_NOTIFY_TYPE = "perfMonitor";
	
	protected int duration = -1, frequency = -1;
	protected long messageNum = 0;
//	protected NotificationPublisher publisher;
	protected NotificationBroadcasterSupport notifyer = new NotificationBroadcasterSupport();

	public String getDuration() {
		return "";
	}

	public String getFrequency() {
		return "";
	}

	// Required
	public void setDuration(String duration) {
		this.duration = Integer.parseInt(duration);
	}

	// Required
	public void setFrequency(String frequency) {
		this.frequency = Integer.parseInt(frequency);
	}
	
	// ManagedOperation
	public void expose(PerformanceItem item) throws Exception {
		String message = "Performance for " + item.getSourceClassName() + "."
				+ item.getSourceMethodName() + ": ";
		final Notification notification = new Notification(JMX_NOTIFY_TYPE, this,
				this.messageNum++, System.currentTimeMillis(), message);
		notification.setUserData(item);
//		publisher.sendNotification(notification);
		this.notify(notification);
	}
	
	// ManagedAttribute
	public boolean isGoodConfiguration() {
		return super.isGoodConfiguration();
	}

	static class SpewingThread extends Thread {
		protected MeasurementExposer exposer;
		SpewingThread(MeasurementExposer exposer) {
			this.exposer = exposer;
		}
		public void run() {
			if (exposer.duration < 1 || exposer.frequency < 1) {
				System.out.println("Duration and Frequency were not set.");
				return;
			}
			final int freqMillis = exposer.frequency * 1000;
			final int durMillis = exposer.duration * 1000;
			int messageNum = 1;
			final long end = System.currentTimeMillis() + durMillis;
			boolean finished = false;
			long now = 0;
			while ((now = System.currentTimeMillis()) < end && !finished) {
				final String message = "Spewing " + messageNum;
				//System.out.println(message);
				try {
//					publisher.sendNotification(new Notification(JMX_NOTIFY_TYPE, this, messageNum, now, message));
					this.exposer.notify(new Notification(JMX_NOTIFY_TYPE, this, messageNum, now, message));
					messageNum++;
					Thread.sleep(freqMillis);
				} catch (InterruptedException e) {
					finished = true;
				} catch (Exception e) {
					//do nothing, keep going
				}
			}

		}
	}

	public void startSpewing() {
		new SpewingThread(this).start();
	}

	protected void notify(Notification notification) {
		this.notifyer.sendNotification(notification);
	}
	
	@Override
	public void addNotificationListener(NotificationListener listener,
			NotificationFilter filter, Object handback)
			throws IllegalArgumentException {
		this.notifyer.addNotificationListener(listener, filter, handback);
	}

	@Override
	public void removeNotificationListener(NotificationListener listener)
			throws ListenerNotFoundException {
		this.notifyer.removeNotificationListener(listener);
	}

	@Override
	public MBeanNotificationInfo[] getNotificationInfo() {
		return this.notifyer.getNotificationInfo();
	}

	@Override
	public void removeNotificationListener(NotificationListener listener,
			NotificationFilter filter, Object handback)
			throws ListenerNotFoundException {
		this.notifyer.removeNotificationListener(listener, filter, handback);
	}
	

}
