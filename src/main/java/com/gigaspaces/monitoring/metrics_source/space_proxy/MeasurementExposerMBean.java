/**
 * 
 */
package com.gigaspaces.monitoring.metrics_source.space_proxy;

/**
 * Exposes performance measurements to the world. The means by which it does this is
 * up to the implementation.
 * @author Toby Sarver
 *
 */
public interface MeasurementExposerMBean {
	/**
	 * Expose the performance item to whoever is listening.
	 * @param item contains info about what was called and what happened
	 * @throws Exception if anything goes wrong
	 */
	void expose(PerformanceItem item) throws Exception;
	
	/**
	 * Check the configuration of the <tt>MeasurementExposer</tt>.
	 * @return false if there is a problem
	 */
	boolean isGoodConfiguration();
}
