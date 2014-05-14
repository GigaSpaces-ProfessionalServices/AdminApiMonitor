/**
 * 
 */
package com.gigaspaces.monitoring.metrics_source.space_proxy;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.gigaspaces.async.AsyncFuture;
import com.gigaspaces.async.AsyncFutureListener;

/**
 * This is a wrapper for the actual AsyncFuture returned by GigaSpace methods.
 * When a method returns an actual AsyncFuture, it is wrapped with an instance of this class.
 * The objective is to determine when the application retrieves the result, and how long it
 * eventually takes.
 * 
 * @author Toby Sarver
 *
 */
public class AsyncFuturePerfSource<T> implements AsyncFuture<T> {

    protected SimplePerformanceItem performanceItem;

    protected MeasurementExposerInterface exposer;

	protected AsyncFuture<T> actual;

	public AsyncFuturePerfSource(AsyncFuture<T> actual, SimplePerformanceItem performanceItem, MeasurementExposerInterface exposer) {
		this.actual = actual;
        this.performanceItem = performanceItem;
		this.exposer = exposer;
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		//TODO log the cancel
		return this.actual.cancel(mayInterruptIfRunning);
	}

	@Override
	public boolean isCancelled() {
		return this.actual.isCancelled();
	}

	@Override
	public boolean isDone() {
		return this.actual.isDone();
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
        T result = null;
        try {
            result = internalGet(false, 0, TimeUnit.MICROSECONDS);
        } catch (TimeoutException e) {
            //this exception will not be reached, because internalGet uses V get() throws InterruptedException, ExecutionException;
        }
        return result;
    }

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
        return internalGet(true, timeout, unit);
	}

    public T internalGet(boolean withTimeout, long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, TimeoutException {

        long startTime = System.currentTimeMillis();
        performanceItem.setStartTime(startTime);
        try {
            T result;
            if (withTimeout) {
                result = this.actual.get(timeout, unit);
            }   else {
                result = this.actual.get();
            }
            performanceItem.setCacheHitOrMiss(result);
            return result;
        } catch (Exception e) {
            performanceItem.setInException(true);
            performanceItem.setStackTrace(e);
            throw e;
        }   finally {
            performanceItem.setElapsedTime((int)(System.currentTimeMillis() - startTime));
            try {
                exposer.expose(performanceItem);
            }   catch (Exception e){

            }
        }
    }

	@Override
	public void setListener(AsyncFutureListener<T> arg0) {
		this.actual.setListener(arg0);
	}

}
