/**
 * 
 */
package com.gigaspaces.monitoring;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

/**
 * Call a method and capture timing info about the call.
 * @author Toby Sarver
 *
 */
public class PerformanceIntercept<T> implements PerformanceItem, Serializable {

	private static final long serialVersionUID = 8825243341345821395L;

	protected transient Exception exception;
	protected String exceptionMessage;
	protected transient T result;
	protected final SimplePerformanceItem performanceItem = new SimplePerformanceItem();

	/**
	 * Gather the metrics around calling a method, and capture the exception if one is thrown.
	 * 
	 * @param callback the method to call
	 * @param parameters the parameters to pass to the method
	 * @param className the class being called
	 * @param methodName the method being measured
	 * @return
	 */
	public static <T> PerformanceIntercept<T> measure(
			PerformanceCallback<T> callback, Object parameters[],
			String className, String methodName) {
		PerformanceIntercept<T> intercept = new PerformanceIntercept<T>();
		intercept.performanceItem.setSourceClassName(className);
		intercept.performanceItem.setSourceMethodName(methodName);
		try {
			intercept.result = callback.call(parameters);
			intercept.performanceItem.inferElapsedTime(true);
		} catch (Exception e) {
			intercept.performanceItem.inferElapsedTime(false);
			intercept.exception = e;
			intercept.exceptionMessage = e.getMessage();
			intercept.performanceItem.setExceptionStack(getStackTrace(e));
		}
		return intercept;
	}
	
	protected PerformanceIntercept() {
		super();
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Call took ");
		buffer.append(this.performanceItem.getElapsedTime());
		buffer.append(" millis.");
		return buffer.toString();
	}

	public static String getStackTrace(Exception e) {
		StringWriter sOut = new StringWriter(255);
		PrintWriter writer = new PrintWriter(sOut);
		e.printStackTrace(writer);
		return sOut.toString();
	}
	
	public T getResult() {
		return this.result;
	}

	@Override
	public Integer getElapsedTime() {
		return this.performanceItem.getElapsedTime();
	}

	public String getExceptionStack() {
		return this.performanceItem.getExceptionStack();
	}

	public Exception getException() {
		return exception;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	/**
	 * This is used to extract the simple version of <tt>PerformanceItem</tt>, primarily for moving across JVM's.
	 * Well-behaved callers will treat this as immutable by not casting it as <tt>SimplePerformanceItem</tt>.
	 * @return the <tt>PerformanceItem</tt> behind this <tt>Intercept</tt>
	 */
	public PerformanceItem getPerformanceItem() {
		return this.performanceItem;
	}

	@Override
	public String getSourceMethodName() {
		return this.performanceItem.getSourceMethodName();
	}

	@Override
	public String getSourceClassName() {
		return this.performanceItem.getSourceClassName();
	}

	@Override
	public long getStartTime() {
		return this.performanceItem.getStartTime();
	}

	@Override
	public Boolean isInException() {
		return this.performanceItem.getInException();
	}
}
