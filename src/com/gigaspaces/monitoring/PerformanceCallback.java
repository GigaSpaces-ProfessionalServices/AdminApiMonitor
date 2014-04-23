/**
 * 
 */
package com.gigaspaces.monitoring;

/**
 * The call to the actual method being measured.
 * @author Toby Sarver
 *
 */
public interface PerformanceCallback<T> {
	T call(Object parameters[]);
}
