/**
 * 
 */
package com.gigaspaces.monitoring;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * A log Handler that raises each <tt>LogRecord</tt> as a JMX event, with maximum
 * asynchrony. In other words, the caller will wait a minimum amount of time.
 * 
 * @author Toby Sarver
 *
 */
public class AsyncJmxEventLogHandler extends Handler {

	@Override
	public void publish(LogRecord record) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws SecurityException {
		// TODO Auto-generated method stub
		
	}

}
