/**
 * 
 */
package com.gigaspaces.monitoring;

import java.lang.management.ManagementFactory;
import java.util.Set;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

/**
 * This creates a MBeanServer and populates it with an <tt>AsyncJmxMeasurementExposer</tt>
 * 
 * @author Toby Sarver
 *
 */
public class TestJmxStarter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args == null || args.length != 2) {
//			System.out.println("Usage: java TestJmxStarter <Spring config classpath>");
			//Was: "/META-INF/spring/mbean-server.xml"
			//System.out.println("Usage: java TestJmxStarter <JMX Bean name> <frequency> <duration>");
			System.out.println("Usage: java TestJmxStarter <frequency> <duration>");
			System.out.println("Note: frequency and duration are in seconds.");
			System.exit(1);
		}
//		ApplicationContext springContext = new ClassPathXmlApplicationContext(args[0]);
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		try {
			ObjectName objectName = storeMBean(args, server);
			queryMBean(server, objectName);
		} catch (MalformedObjectNameException | InstanceAlreadyExistsException
				| MBeanRegistrationException | NotCompliantMBeanException e1) {
			e1.printStackTrace(System.err);
		}
		//Wait for 10 minutes
		try {
			Thread.sleep(10*60*1000);
		} catch (InterruptedException e) {
			e.printStackTrace(System.err);
		}
	}

	protected static MeasurementExposer createMBean(String duration, String frequency) {
		MeasurementExposer mbean = new MeasurementExposer();
		mbean.setDuration(duration);
		mbean.setFrequency(frequency);
		return mbean;
	}
	
	protected static ObjectName storeMBean(String args[], MBeanServer server) throws MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
		MeasurementExposer mbean = createMBean(args[1], args[0]);
		String mbeanName = MeasurementExposer.BEAN_ID;
		//String mbeanName = args[0];
		ObjectName objectName = new ObjectName(mbeanName);
		server.registerMBean(mbean, objectName);
		final TestNotificationListener listener = new TestNotificationListener();
		mbean.addNotificationListener(listener, listener, null);
		mbean.startSpewing();
		return objectName;
	}


	protected static void queryMBean(MBeanServer server, ObjectName objectName) {
		Set<ObjectInstance> instances = server.queryMBeans(objectName, null);
		ObjectInstance instance = (ObjectInstance) instances.toArray()[0];
		System.out.println("Class name:\t" + instance.getClassName());
		System.out.println("Object name:\t" + instance.getObjectName());
	}
}
