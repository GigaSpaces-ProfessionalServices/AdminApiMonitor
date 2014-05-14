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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;


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
public class MeasurementExposerImpl extends PerformanceSource implements MeasurementExposerImplMBean, NotificationEmitter {

	public static final String BEAN_ID = "com.gigaspaces.monitoring:name=MeasurementExposer";

	public static final String JMX_NOTIFY_TYPE = "perfMonitor";
	
	protected int duration = -1, frequency = -1;

    protected AtomicLong messageNum = new AtomicLong(0);

//	protected NotificationPublisher publisher;
	protected NotificationBroadcasterSupport notifier = new NotificationBroadcasterSupport();

    //TODO check appropriate executor service implementation
    protected ExecutorService executorService = Executors.newCachedThreadPool();

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
	public void expose(SimplePerformanceItem item) throws Exception {
        executorService.submit(new ExposeThread(notifier, item, messageNum));
	}
	
	// ManagedAttribute
	public boolean isGoodConfiguration() {
		return super.isGoodConfiguration();
	}

    private static class ExposeThread implements Runnable{

        private NotificationBroadcasterSupport notifier;

        private SimplePerformanceItem performanceItem;

        private AtomicLong messageNum;

        private ExposeThread(NotificationBroadcasterSupport notifier, SimplePerformanceItem performanceItem, AtomicLong messageNum) {
            this.notifier = notifier;
            this.performanceItem = performanceItem;
            this.messageNum = messageNum;
        }

        @Override
        public void run() {
            String message = "Performance for " + performanceItem.getSourceClassName() + "."
                    + performanceItem.getSourceMethodName() + ": ";
            final Notification notification = new Notification(JMX_NOTIFY_TYPE, this,
                    messageNum.addAndGet(1), System.currentTimeMillis(), message);
            System.out.println("MESSAGE_NUMBER = " + notification.getSequenceNumber());
            notification.setUserData(performanceItem);
//		publisher.sendNotification(notification);
            notifier.sendNotification(notification);
        }
    }

	static class SpewingThread extends Thread {
		protected MeasurementExposerImpl exposer;
		SpewingThread(MeasurementExposerImpl exposer) {
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
		this.notifier.sendNotification(notification);
	}
	
	@Override
	public void addNotificationListener(NotificationListener listener,
			NotificationFilter filter, Object handback)
			throws IllegalArgumentException {
		this.notifier.addNotificationListener(listener, filter, handback);
	}

	@Override
	public void removeNotificationListener(NotificationListener listener)
			throws ListenerNotFoundException {
		this.notifier.removeNotificationListener(listener);
	}

	@Override
	public MBeanNotificationInfo[] getNotificationInfo() {
		return this.notifier.getNotificationInfo();
	}

	@Override
	public void removeNotificationListener(NotificationListener listener,
			NotificationFilter filter, Object handback)
			throws ListenerNotFoundException {
		this.notifier.removeNotificationListener(listener, filter, handback);
	}
	

}
