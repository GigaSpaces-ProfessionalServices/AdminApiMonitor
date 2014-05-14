/**
 * 
 */
package com.gigaspaces.monitoring.metrics_source.space_proxy;

/**
 * The abstract class for all performance sources. Requires a <tt>MeasurementExposer</tt>
 * to expose the performance measurements.
 * 
 * @author Toby Sarver
 *
 */
public abstract class PerformanceSource {

	private MeasurementExposerInterface exposer;

	public MeasurementExposerInterface getExposer() {
		if (null == this.exposer) {
			throw new IllegalStateException("The MeasurementExposer can not be null.");
		}
		return exposer;
	}

	public void setExposer(MeasurementExposerInterface exposer) {
		this.exposer = exposer;
	}
	
	public boolean isGoodConfiguration() {
		return (this.exposer != null);
	}
}
