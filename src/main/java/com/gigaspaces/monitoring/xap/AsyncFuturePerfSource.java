/**
 * 
 */
package com.gigaspaces.monitoring.xap;

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
	
	protected AsyncFuture<T> actual;
	/**
	 * The id of the future, so that we can bring the two method calls together:
	 * The method that created the AsyncFuture and when the application retrieves
	 * the value of the AsyncFuture.
	 */
	protected String id;

	public AsyncFuturePerfSource(AsyncFuture<T> actual, String id) {
		this.actual = actual;
		this.id = id;
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
		// TODO log the get
		return this.actual.get();
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		// TODO log the get
		try {
			return this.actual.get(timeout, unit);
		} catch (TimeoutException te) {
			// TODO log the timeout
			throw te;
		}
	}

	@Override
	public void setListener(AsyncFutureListener<T> arg0) {
		this.actual.setListener(arg0);
	}

}
