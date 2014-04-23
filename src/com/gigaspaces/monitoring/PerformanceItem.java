/**
 * Copyright (C) 2014 GigaSpaces Technologies
 */
package com.gigaspaces.monitoring;

/**
 * Defines detailed performance logging at the method level.
 * <br><b>Not yet implemented</b> below this line.
 * Allows for <tt>AsyncFuture</tt> by generating a UUID to track the original method call
 * and any subsequent calls on the future.
 * 
 * @author Toby Sarver
 *
 */
public interface PerformanceItem {
	/**
	 * @return The method we are measuring
	 */
	String getSourceMethodName();
	/**
	 * @return The class containing the method we're measuring (most concrete in hierarchy)
	 */
	String getSourceClassName();
	/**
	 * @return The absolute time when the method started executing.
	 */
	long getStartTime();
	/**
	 * @return The total time it took to complete executing the method, or null if it hasn't completed
	 */
	Integer getElapsedTime();
	/**
	  @return true if the method threw an exception, false if it completed without exception, or null if it hasn't completed
	 */
	Boolean isInException();

	/**
	 * @return the stack trace of the exception (and all "caused by"), or null if hasn't completed, or didn't end in exception 
	 */
	String getExceptionStack();
}
