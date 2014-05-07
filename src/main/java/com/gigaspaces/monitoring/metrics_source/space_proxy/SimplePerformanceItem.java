/**
 * 
 */
package com.gigaspaces.monitoring.metrics_source.space_proxy;

import java.io.Serializable;

/**
 * @author tsarver
 *
 */
public class SimplePerformanceItem implements Serializable {

	private static final long serialVersionUID = -1483287039798071568L;

	protected String sourceMethodName, sourceClassName;
	protected long startTime;
	protected Integer elapsedTime;
	protected Boolean inException;
	protected String exceptionStack;
	protected Boolean cacheHit;

	/**
	 * Creates a <tt>PerformanceItem</tt> with the start as the current time.
	 */
	public SimplePerformanceItem() {
		this(System.currentTimeMillis());
	}
	
	//Between the two of these, you can't create an instance without the start time being set.
	//It can however be reset by the caller using "setStartTime()".
	
	/**
	 * Creates a <tt>PerformanceItem</tt> with the given start time.
	 * @param startTime when the method started executing.
	 */
	public SimplePerformanceItem(long startTime) {
		super();
		this.setStartTime(startTime);
	}

	/* (non-Javadoc)
	 * @see com.gigaspaces.monitoring.metrics_source.space_proxy.PerformanceSource#getSourceMethodName()
	 */
	public String getSourceMethodName() {
		return this.sourceMethodName;
	}

	/* (non-Javadoc)
	 * @see com.gigaspaces.monitoring.metrics_source.space_proxy.PerformanceSource#getSourceClassName()
	 */
	public String getSourceClassName() {
		return this.sourceClassName;
	}

	/* (non-Javadoc)
	 * @see com.gigaspaces.monitoring.metrics_source.space_proxy.PerformanceSource#getStartTime()
	 */
	public long getStartTime() {
		return this.startTime;
	}

	/* (non-Javadoc)
	 * @see com.gigaspaces.monitoring.metrics_source.space_proxy.PerformanceSource#getElapsedTime()
	 */
	public Integer getElapsedTime() {
		return this.elapsedTime;
	}

	/* (non-Javadoc)
	 * @see com.gigaspaces.monitoring.metrics_source.space_proxy.PerformanceSource#isInException()
	 */
	public Boolean isInException() {
		return this.inException;
	}

	/* (non-Javadoc)
	 * @see com.gigaspaces.monitoring.metrics_source.space_proxy.PerformanceSource#getExceptionStack()
	 */
	public String getExceptionStack() {
		return this.exceptionStack;
	}

    public Boolean getCacheHit() {
        return cacheHit;
    }

    public Boolean getInException() {
		return inException;
	}

	public void setInException(Boolean inException) {
		this.inException = inException;
	}

	public void setSourceMethodName(String sourceMethodName) {
		this.sourceMethodName = sourceMethodName;
	}

	public void setSourceClassName(String sourceClassName) {
		this.sourceClassName = sourceClassName;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void setElapsedTime(Integer elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public void setExceptionStack(String exceptionStack) {
		this.exceptionStack = exceptionStack;
	}

    public void setCacheHit(Boolean cacheHit) {
        this.cacheHit = cacheHit;
    }
}
