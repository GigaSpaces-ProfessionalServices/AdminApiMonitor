/**
 * 
 */
package com.gigaspaces.monitoring.xap;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import net.jini.core.transaction.Transaction;

import org.openspaces.core.ExecutorBuilder;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceTypeManager;
import org.openspaces.core.IteratorBuilder;
import org.openspaces.core.exception.ExceptionTranslator;
import org.openspaces.core.executor.DistributedTask;
import org.openspaces.core.executor.Task;
import org.openspaces.core.transaction.TransactionProvider;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;

import com.gigaspaces.async.AsyncFuture;
import com.gigaspaces.async.AsyncFutureListener;
import com.gigaspaces.async.AsyncResultsReducer;
import com.gigaspaces.client.ChangeModifiers;
import com.gigaspaces.client.ChangeResult;
import com.gigaspaces.client.ChangeSet;
import com.gigaspaces.client.ClearModifiers;
import com.gigaspaces.client.CountModifiers;
import com.gigaspaces.client.ReadByIdsResult;
import com.gigaspaces.client.ReadModifiers;
import com.gigaspaces.client.TakeByIdsResult;
import com.gigaspaces.client.TakeModifiers;
import com.gigaspaces.client.WriteModifiers;
import com.gigaspaces.monitoring.MeasurementExposer;
import com.gigaspaces.monitoring.MeasurementExposerMBean;
import com.gigaspaces.monitoring.PerformanceCallback;
import com.gigaspaces.monitoring.PerformanceIntercept;
import com.gigaspaces.monitoring.PerformanceSource;
import com.gigaspaces.query.ISpaceQuery;
import com.gigaspaces.query.IdQuery;
import com.gigaspaces.query.IdsQuery;
import com.j_spaces.core.IJSpace;
import com.j_spaces.core.LeaseContext;

/**
 * The bean that intercepts each call to the actual GigaSpace,
 * and generates a JMX event containing the datetime, how long it took, and other info about the call.
 * 
 * @author Toby Sarver
 *
 */
public class GigaSpacePerfSource extends PerformanceSource implements GigaSpace {

	private static final Logger LOGGER = Logger.getLogger(GigaSpacePerfSource.class.getName());
	protected GigaSpace actualSpace;

	/**
	 * The actual space that is doing the work. This could be a remote space (i.e., Space proxy) or
	 * the embedded space.
	 * @return the actual Space
	 */
	public GigaSpace getActualSpace() {
		return actualSpace;
	}

	@Required
	public void setActualSpace(GigaSpace actualSpace) {
		this.actualSpace = actualSpace;
	}

	/*
	 * Would love to write a generic for handling all exceptions, but can't.
	 **
	protected <T> T handleIntercept(PerformanceCallback<T> callback, Object parameters[]) throws Exception {
		PerformanceIntercept<T> intercept = PerformanceIntercept.measure(callback, parameters);
		Exception e = null;
		if ((e = intercept.getException()) != null) {
			throw e;
		}
	}
	*/
	
	/* (non-Javadoc)
	 * @see org.openspaces.core.GigaSpace#asyncChange(com.gigaspaces.query.ISpaceQuery, com.gigaspaces.client.ChangeSet)
	 */
	@Override
	public <T> Future<ChangeResult<T>> asyncChange(ISpaceQuery<T> arg0,
			ChangeSet arg1) {
		// TODO Auto-generated method stub
		return this.actualSpace.asyncChange(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.openspaces.core.GigaSpace#asyncChange(java.lang.Object, com.gigaspaces.client.ChangeSet)
	 */
	@Override
	public <T> Future<ChangeResult<T>> asyncChange(T arg0, ChangeSet arg1) {
		// TODO Auto-generated method stub
		return this.actualSpace.asyncChange(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.openspaces.core.GigaSpace#asyncChange(com.gigaspaces.query.ISpaceQuery, com.gigaspaces.client.ChangeSet, com.gigaspaces.async.AsyncFutureListener)
	 */
	@Override
	public <T> Future<ChangeResult<T>> asyncChange(ISpaceQuery<T> arg0,
			ChangeSet arg1, AsyncFutureListener<ChangeResult<T>> arg2) {
		// TODO Auto-generated method stub
		return this.actualSpace.asyncChange(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see org.openspaces.core.GigaSpace#asyncChange(com.gigaspaces.query.ISpaceQuery, com.gigaspaces.client.ChangeSet, long)
	 */
	@Override
	public <T> Future<ChangeResult<T>> asyncChange(ISpaceQuery<T> arg0,
			ChangeSet arg1, long arg2) {
		// TODO Auto-generated method stub
		return this.actualSpace.asyncChange(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see org.openspaces.core.GigaSpace#asyncChange(com.gigaspaces.query.ISpaceQuery, com.gigaspaces.client.ChangeSet, com.gigaspaces.client.ChangeModifiers)
	 */
	@Override
	public <T> Future<ChangeResult<T>> asyncChange(ISpaceQuery<T> arg0,
			ChangeSet arg1, ChangeModifiers arg2) {
		// TODO Auto-generated method stub
		return this.actualSpace.asyncChange(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see org.openspaces.core.GigaSpace#asyncChange(java.lang.Object, com.gigaspaces.client.ChangeSet, com.gigaspaces.async.AsyncFutureListener)
	 */
	@Override
	public <T> Future<ChangeResult<T>> asyncChange(T arg0, ChangeSet arg1,
			AsyncFutureListener<ChangeResult<T>> arg2) {
		// TODO Auto-generated method stub
		return this.actualSpace.asyncChange(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see org.openspaces.core.GigaSpace#asyncChange(java.lang.Object, com.gigaspaces.client.ChangeSet, long)
	 */
	@Override
	public <T> Future<ChangeResult<T>> asyncChange(T arg0, ChangeSet arg1,
			long arg2) {
		// TODO Auto-generated method stub
		return this.actualSpace.asyncChange(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see org.openspaces.core.GigaSpace#asyncChange(java.lang.Object, com.gigaspaces.client.ChangeSet, com.gigaspaces.client.ChangeModifiers)
	 */
	@Override
	public <T> Future<ChangeResult<T>> asyncChange(T arg0, ChangeSet arg1,
			ChangeModifiers arg2) {
		// TODO Auto-generated method stub
		return this.actualSpace.asyncChange(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see org.openspaces.core.GigaSpace#asyncChange(com.gigaspaces.query.ISpaceQuery, com.gigaspaces.client.ChangeSet, long, com.gigaspaces.async.AsyncFutureListener)
	 */
	@Override
	public <T> Future<ChangeResult<T>> asyncChange(ISpaceQuery<T> arg0,
			ChangeSet arg1, long arg2, AsyncFutureListener<ChangeResult<T>> arg3) {
		// TODO Auto-generated method stub
		return this.actualSpace.asyncChange(arg0, arg1, arg2, arg3);
	}

	/* (non-Javadoc)
	 * @see org.openspaces.core.GigaSpace#asyncChange(com.gigaspaces.query.ISpaceQuery, com.gigaspaces.client.ChangeSet, com.gigaspaces.client.ChangeModifiers, com.gigaspaces.async.AsyncFutureListener)
	 */
	@Override
	public <T> Future<ChangeResult<T>> asyncChange(ISpaceQuery<T> arg0,
			ChangeSet arg1, ChangeModifiers arg2,
			AsyncFutureListener<ChangeResult<T>> arg3) {
		// TODO Auto-generated method stub
		return this.actualSpace.asyncChange(arg0, arg1, arg2, arg3);
	}

	public <T> Future<ChangeResult<T>> asyncChange(ISpaceQuery<T> arg0,
			ChangeSet arg1, ChangeModifiers arg2, long arg3,
			AsyncFutureListener<ChangeResult<T>> arg4) {
		return actualSpace.asyncChange(arg0, arg1, arg2, arg3, arg4);
	}

	public <T> Future<ChangeResult<T>> asyncChange(ISpaceQuery<T> arg0,
			ChangeSet arg1, ChangeModifiers arg2, long arg3) {
		return actualSpace.asyncChange(arg0, arg1, arg2, arg3);
	}

	public <T> Future<ChangeResult<T>> asyncChange(T arg0, ChangeSet arg1,
			ChangeModifiers arg2, AsyncFutureListener<ChangeResult<T>> arg3) {
		return actualSpace.asyncChange(arg0, arg1, arg2, arg3);
	}

	public <T> Future<ChangeResult<T>> asyncChange(T arg0, ChangeSet arg1,
			ChangeModifiers arg2, long arg3,
			AsyncFutureListener<ChangeResult<T>> arg4) {
		return actualSpace.asyncChange(arg0, arg1, arg2, arg3, arg4);
	}

	public <T> Future<ChangeResult<T>> asyncChange(T arg0, ChangeSet arg1,
			ChangeModifiers arg2, long arg3) {
		return actualSpace.asyncChange(arg0, arg1, arg2, arg3);
	}

	public <T> Future<ChangeResult<T>> asyncChange(T arg0, ChangeSet arg1,
			long arg2, AsyncFutureListener<ChangeResult<T>> arg3) {
		return actualSpace.asyncChange(arg0, arg1, arg2, arg3);
	}

	public <T> AsyncFuture<T> asyncRead(ISpaceQuery<T> arg0,
			AsyncFutureListener<T> arg1) throws DataAccessException {
		return actualSpace.asyncRead(arg0, arg1);
	}

	public <T> AsyncFuture<T> asyncRead(ISpaceQuery<T> arg0, long arg1,
			AsyncFutureListener<T> arg2) throws DataAccessException {
		return actualSpace.asyncRead(arg0, arg1, arg2);
	}

	public <T> AsyncFuture<T> asyncRead(ISpaceQuery<T> arg0, long arg1,
			int arg2, AsyncFutureListener<T> arg3) throws DataAccessException {
		return actualSpace.asyncRead(arg0, arg1, arg2, arg3);
	}

	public <T> AsyncFuture<T> asyncRead(ISpaceQuery<T> arg0, long arg1, int arg2)
			throws DataAccessException {
		return actualSpace.asyncRead(arg0, arg1, arg2);
	}

	public <T> AsyncFuture<T> asyncRead(ISpaceQuery<T> arg0, long arg1,
			ReadModifiers arg2, AsyncFutureListener<T> arg3)
			throws DataAccessException {
		return actualSpace.asyncRead(arg0, arg1, arg2, arg3);
	}

	public <T> AsyncFuture<T> asyncRead(ISpaceQuery<T> arg0, long arg1,
			ReadModifiers arg2) throws DataAccessException {
		return actualSpace.asyncRead(arg0, arg1, arg2);
	}

	public <T> AsyncFuture<T> asyncRead(ISpaceQuery<T> arg0, long arg1)
			throws DataAccessException {
		return actualSpace.asyncRead(arg0, arg1);
	}

	public <T> AsyncFuture<T> asyncRead(ISpaceQuery<T> arg0)
			throws DataAccessException {
		return actualSpace.asyncRead(arg0);
	}

	public <T> AsyncFuture<T> asyncRead(T arg0, AsyncFutureListener<T> arg1)
			throws DataAccessException {
		return actualSpace.asyncRead(arg0, arg1);
	}

	public <T> AsyncFuture<T> asyncRead(T arg0, long arg1,
			AsyncFutureListener<T> arg2) throws DataAccessException {
		return actualSpace.asyncRead(arg0, arg1, arg2);
	}

	public <T> AsyncFuture<T> asyncRead(T arg0, long arg1, int arg2,
			AsyncFutureListener<T> arg3) throws DataAccessException {
		return actualSpace.asyncRead(arg0, arg1, arg2, arg3);
	}

	public <T> AsyncFuture<T> asyncRead(T arg0, long arg1, int arg2)
			throws DataAccessException {
		return actualSpace.asyncRead(arg0, arg1, arg2);
	}

	public <T> AsyncFuture<T> asyncRead(T arg0, long arg1, ReadModifiers arg2,
			AsyncFutureListener<T> arg3) throws DataAccessException {
		return actualSpace.asyncRead(arg0, arg1, arg2, arg3);
	}

	public <T> AsyncFuture<T> asyncRead(T arg0, long arg1, ReadModifiers arg2)
			throws DataAccessException {
		return actualSpace.asyncRead(arg0, arg1, arg2);
	}

	public <T> AsyncFuture<T> asyncRead(T arg0, long arg1)
			throws DataAccessException {
		return actualSpace.asyncRead(arg0, arg1);
	}

	public <T> AsyncFuture<T> asyncRead(T arg0) throws DataAccessException {
		return actualSpace.asyncRead(arg0);
	}

	public <T> AsyncFuture<T> asyncTake(ISpaceQuery<T> arg0,
			AsyncFutureListener<T> arg1) throws DataAccessException {
		return actualSpace.asyncTake(arg0, arg1);
	}

	public <T> AsyncFuture<T> asyncTake(ISpaceQuery<T> arg0, long arg1,
			AsyncFutureListener<T> arg2) throws DataAccessException {
		return actualSpace.asyncTake(arg0, arg1, arg2);
	}

	public <T> AsyncFuture<T> asyncTake(ISpaceQuery<T> arg0, long arg1,
			int arg2, AsyncFutureListener<T> arg3) throws DataAccessException {
		return actualSpace.asyncTake(arg0, arg1, arg2, arg3);
	}

	public <T> AsyncFuture<T> asyncTake(ISpaceQuery<T> arg0, long arg1, int arg2)
			throws DataAccessException {
		return actualSpace.asyncTake(arg0, arg1, arg2);
	}

	public <T> AsyncFuture<T> asyncTake(ISpaceQuery<T> arg0, long arg1,
			TakeModifiers arg2, AsyncFutureListener<T> arg3)
			throws DataAccessException {
		return actualSpace.asyncTake(arg0, arg1, arg2, arg3);
	}

	public <T> AsyncFuture<T> asyncTake(ISpaceQuery<T> arg0, long arg1,
			TakeModifiers arg2) throws DataAccessException {
		return actualSpace.asyncTake(arg0, arg1, arg2);
	}

	public <T> AsyncFuture<T> asyncTake(ISpaceQuery<T> arg0, long arg1)
			throws DataAccessException {
		return actualSpace.asyncTake(arg0, arg1);
	}

	public <T> AsyncFuture<T> asyncTake(ISpaceQuery<T> arg0)
			throws DataAccessException {
		return actualSpace.asyncTake(arg0);
	}

	public <T> AsyncFuture<T> asyncTake(T arg0, AsyncFutureListener<T> arg1)
			throws DataAccessException {
		return actualSpace.asyncTake(arg0, arg1);
	}

	public <T> AsyncFuture<T> asyncTake(T arg0, long arg1,
			AsyncFutureListener<T> arg2) throws DataAccessException {
		return actualSpace.asyncTake(arg0, arg1, arg2);
	}

	public <T> AsyncFuture<T> asyncTake(T arg0, long arg1, int arg2,
			AsyncFutureListener<T> arg3) throws DataAccessException {
		return actualSpace.asyncTake(arg0, arg1, arg2, arg3);
	}

	public <T> AsyncFuture<T> asyncTake(T arg0, long arg1, int arg2)
			throws DataAccessException {
		return actualSpace.asyncTake(arg0, arg1, arg2);
	}

	public <T> AsyncFuture<T> asyncTake(T arg0, long arg1, TakeModifiers arg2,
			AsyncFutureListener<T> arg3) throws DataAccessException {
		return actualSpace.asyncTake(arg0, arg1, arg2, arg3);
	}

	public <T> AsyncFuture<T> asyncTake(T arg0, long arg1, TakeModifiers arg2)
			throws DataAccessException {
		return actualSpace.asyncTake(arg0, arg1, arg2);
	}

	public <T> AsyncFuture<T> asyncTake(T arg0, long arg1)
			throws DataAccessException {
		return actualSpace.asyncTake(arg0, arg1);
	}

	public <T> AsyncFuture<T> asyncTake(T arg0) throws DataAccessException {
		return actualSpace.asyncTake(arg0);
	}

	public <T> ChangeResult<T> change(ISpaceQuery<T> arg0, ChangeSet arg1,
			ChangeModifiers arg2, long arg3) {
		return actualSpace.change(arg0, arg1, arg2, arg3);
	}

	public <T> ChangeResult<T> change(ISpaceQuery<T> arg0, ChangeSet arg1,
			ChangeModifiers arg2) {
		return actualSpace.change(arg0, arg1, arg2);
	}

	public <T> ChangeResult<T> change(ISpaceQuery<T> arg0, ChangeSet arg1,
			long arg2) {
		return actualSpace.change(arg0, arg1, arg2);
	}

	public <T> ChangeResult<T> change(ISpaceQuery<T> arg0, ChangeSet arg1) {
		return actualSpace.change(arg0, arg1);
	}

	public <T> ChangeResult<T> change(T arg0, ChangeSet arg1,
			ChangeModifiers arg2, long arg3) {
		return actualSpace.change(arg0, arg1, arg2, arg3);
	}

	public <T> ChangeResult<T> change(T arg0, ChangeSet arg1,
			ChangeModifiers arg2) {
		return actualSpace.change(arg0, arg1, arg2);
	}

	public <T> ChangeResult<T> change(T arg0, ChangeSet arg1, long arg2) {
		return actualSpace.change(arg0, arg1, arg2);
	}

	public <T> ChangeResult<T> change(T arg0, ChangeSet arg1) {
		return actualSpace.change(arg0, arg1);
	}

	public int clear(Object arg0, ClearModifiers arg1) {
		return actualSpace.clear(arg0, arg1);
	}

	public int clear(Object arg0, int arg1) {
		return actualSpace.clear(arg0, arg1);
	}

	public void clear(Object arg0) throws DataAccessException {
		actualSpace.clear(arg0);
	}

	public int count(Object arg0, CountModifiers arg1)
			throws DataAccessException {
		return actualSpace.count(arg0, arg1);
	}

	public int count(Object arg0, int arg1) throws DataAccessException {
		return actualSpace.count(arg0, arg1);
	}

	public int count(Object arg0) throws DataAccessException {
		return actualSpace.count(arg0);
	}

	public <T extends Serializable, R> AsyncFuture<R> execute(
			DistributedTask<T, R> arg0, Object... arg1) {
		return actualSpace.execute(arg0, arg1);
	}

	public <T extends Serializable, R> AsyncFuture<R> execute(
			DistributedTask<T, R> arg0) {
		return actualSpace.execute(arg0);
	}

	public <T extends Serializable> AsyncFuture<T> execute(Task<T> arg0,
			AsyncFutureListener<T> arg1) {
		return actualSpace.execute(arg0, arg1);
	}

	public <T extends Serializable> AsyncFuture<T> execute(Task<T> arg0,
			Object arg1, AsyncFutureListener<T> arg2) {
		return actualSpace.execute(arg0, arg1, arg2);
	}

	public <T extends Serializable> AsyncFuture<T> execute(Task<T> arg0,
			Object arg1) {
		return actualSpace.execute(arg0, arg1);
	}

	public <T extends Serializable> AsyncFuture<T> execute(Task<T> arg0) {
		return actualSpace.execute(arg0);
	}

	public <T extends Serializable, R> ExecutorBuilder<T, R> executorBuilder(
			AsyncResultsReducer<T, R> arg0) {
		return actualSpace.executorBuilder(arg0);
	}

	public GigaSpace getClustered() {
		return actualSpace.getClustered();
	}

	public Transaction getCurrentTransaction() {
		return actualSpace.getCurrentTransaction();
	}

	public ChangeModifiers getDefaultChangeModifiers() {
		return actualSpace.getDefaultChangeModifiers();
	}

	public ClearModifiers getDefaultClearModifiers() {
		return actualSpace.getDefaultClearModifiers();
	}

	public CountModifiers getDefaultCountModifiers() {
		return actualSpace.getDefaultCountModifiers();
	}

	public ReadModifiers getDefaultReadModifiers() {
		return actualSpace.getDefaultReadModifiers();
	}

	public TakeModifiers getDefaultTakeModifiers() {
		return actualSpace.getDefaultTakeModifiers();
	}

	public WriteModifiers getDefaultWriteModifiers() {
		return actualSpace.getDefaultWriteModifiers();
	}

	public ExceptionTranslator getExceptionTranslator() {
		return actualSpace.getExceptionTranslator();
	}

	public int getModifiersForIsolationLevel() {
		return actualSpace.getModifiersForIsolationLevel();
	}

	public String getName() {
		return actualSpace.getName();
	}

	public IJSpace getSpace() {
		return actualSpace.getSpace();
	}

	public TransactionProvider getTxProvider() {
		return actualSpace.getTxProvider();
	}

	public GigaSpaceTypeManager getTypeManager() {
		return actualSpace.getTypeManager();
	}

	public IteratorBuilder iterator() {
		return actualSpace.iterator();
	}

	public <T> T read(ISpaceQuery<T> arg0, long arg1, int arg2)
			throws DataAccessException {
		return actualSpace.read(arg0, arg1, arg2);
	}

	public <T> T read(ISpaceQuery<T> arg0, long arg1, ReadModifiers arg2)
			throws DataAccessException {
		return actualSpace.read(arg0, arg1, arg2);
	}

	public <T> T read(ISpaceQuery<T> arg0, long arg1)
			throws DataAccessException {
		return actualSpace.read(arg0, arg1);
	}

	public <T> T read(ISpaceQuery<T> arg0) throws DataAccessException {
		return actualSpace.read(arg0);
	}

	public <T> T read(T arg0, long arg1, int arg2) throws DataAccessException {
		return actualSpace.read(arg0, arg1, arg2);
	}

	public <T> T read(T arg0, long arg1, ReadModifiers arg2)
			throws DataAccessException {
		return actualSpace.read(arg0, arg1, arg2);
	}

	public <T> T read(T arg0, long arg1) throws DataAccessException {
		return actualSpace.read(arg0, arg1);
	}

	public <T> T read(T arg0) throws DataAccessException {
		return actualSpace.read(arg0);
	}

	public <T> T readById(Class<T> arg0, Object arg1, Object arg2, long arg3,
			int arg4) throws DataAccessException {
		return actualSpace.readById(arg0, arg1, arg2, arg3, arg4);
	}

	public <T> T readById(Class<T> arg0, Object arg1, Object arg2, long arg3,
			ReadModifiers arg4) throws DataAccessException {
		return actualSpace.readById(arg0, arg1, arg2, arg3, arg4);
	}

	public <T> T readById(Class<T> arg0, Object arg1, Object arg2, long arg3)
			throws DataAccessException {
		return actualSpace.readById(arg0, arg1, arg2, arg3);
	}

	public <T> T readById(Class<T> arg0, Object arg1, Object arg2)
			throws DataAccessException {
		return actualSpace.readById(arg0, arg1, arg2);
	}

	public <T> T readById(Class<T> arg0, Object arg1)
			throws DataAccessException {
		return actualSpace.readById(arg0, arg1);
	}

	public <T> T readById(IdQuery<T> arg0, long arg1, int arg2)
			throws DataAccessException {
		return actualSpace.readById(arg0, arg1, arg2);
	}

	public <T> T readById(IdQuery<T> arg0, long arg1, ReadModifiers arg2)
			throws DataAccessException {
		return actualSpace.readById(arg0, arg1, arg2);
	}

	public <T> T readById(IdQuery<T> arg0, long arg1)
			throws DataAccessException {
		return actualSpace.readById(arg0, arg1);
	}

	public <T> T readById(IdQuery<T> arg0) throws DataAccessException {
		return actualSpace.readById(arg0);
	}

	public <T> ReadByIdsResult<T> readByIds(Class<T> arg0, Object[] arg1,
			int arg2) throws DataAccessException {
		return actualSpace.readByIds(arg0, arg1, arg2);
	}

	public <T> ReadByIdsResult<T> readByIds(Class<T> arg0, Object[] arg1,
			Object arg2, int arg3) throws DataAccessException {
		return actualSpace.readByIds(arg0, arg1, arg2, arg3);
	}

	public <T> ReadByIdsResult<T> readByIds(Class<T> arg0, Object[] arg1,
			Object arg2, ReadModifiers arg3) throws DataAccessException {
		return actualSpace.readByIds(arg0, arg1, arg2, arg3);
	}

	public <T> ReadByIdsResult<T> readByIds(Class<T> arg0, Object[] arg1,
			Object arg2) throws DataAccessException {
		return actualSpace.readByIds(arg0, arg1, arg2);
	}

	public <T> ReadByIdsResult<T> readByIds(Class<T> arg0, Object[] arg1,
			Object[] arg2, int arg3) throws DataAccessException {
		return actualSpace.readByIds(arg0, arg1, arg2, arg3);
	}

	public <T> ReadByIdsResult<T> readByIds(Class<T> arg0, Object[] arg1,
			Object[] arg2, ReadModifiers arg3) throws DataAccessException {
		return actualSpace.readByIds(arg0, arg1, arg2, arg3);
	}

	public <T> ReadByIdsResult<T> readByIds(Class<T> arg0, Object[] arg1,
			Object[] arg2) throws DataAccessException {
		return actualSpace.readByIds(arg0, arg1, arg2);
	}

	public <T> ReadByIdsResult<T> readByIds(Class<T> arg0, Object[] arg1)
			throws DataAccessException {
		return actualSpace.readByIds(arg0, arg1);
	}

	public <T> ReadByIdsResult<T> readByIds(IdsQuery<T> arg0, int arg1)
			throws DataAccessException {
		return actualSpace.readByIds(arg0, arg1);
	}

	public <T> ReadByIdsResult<T> readByIds(IdsQuery<T> arg0, ReadModifiers arg1)
			throws DataAccessException {
		return actualSpace.readByIds(arg0, arg1);
	}

	public <T> ReadByIdsResult<T> readByIds(IdsQuery<T> arg0)
			throws DataAccessException {
		return actualSpace.readByIds(arg0);
	}

	public <T> T readIfExists(ISpaceQuery<T> arg0, long arg1, int arg2)
			throws DataAccessException {
		return actualSpace.readIfExists(arg0, arg1, arg2);
	}

	public <T> T readIfExists(ISpaceQuery<T> arg0, long arg1, ReadModifiers arg2)
			throws DataAccessException {
		return actualSpace.readIfExists(arg0, arg1, arg2);
	}

	public <T> T readIfExists(ISpaceQuery<T> arg0, long arg1)
			throws DataAccessException {
		return actualSpace.readIfExists(arg0, arg1);
	}

	public <T> T readIfExists(ISpaceQuery<T> arg0) throws DataAccessException {
		return actualSpace.readIfExists(arg0);
	}

	public <T> T readIfExists(T arg0, long arg1, int arg2)
			throws DataAccessException {
		return actualSpace.readIfExists(arg0, arg1, arg2);
	}

	public <T> T readIfExists(T arg0, long arg1, ReadModifiers arg2)
			throws DataAccessException {
		return actualSpace.readIfExists(arg0, arg1, arg2);
	}

	public <T> T readIfExists(T arg0, long arg1) throws DataAccessException {
		return actualSpace.readIfExists(arg0, arg1);
	}

	public <T> T readIfExists(T arg0) throws DataAccessException {
		return actualSpace.readIfExists(arg0);
	}

	public <T> T readIfExistsById(Class<T> arg0, Object arg1, Object arg2,
			long arg3, int arg4) throws DataAccessException {
		return actualSpace.readIfExistsById(arg0, arg1, arg2, arg3, arg4);
	}

	public <T> T readIfExistsById(Class<T> arg0, Object arg1, Object arg2,
			long arg3, ReadModifiers arg4) throws DataAccessException {
		return actualSpace.readIfExistsById(arg0, arg1, arg2, arg3, arg4);
	}

	public <T> T readIfExistsById(Class<T> arg0, Object arg1, Object arg2,
			long arg3) throws DataAccessException {
		return actualSpace.readIfExistsById(arg0, arg1, arg2, arg3);
	}

	public <T> T readIfExistsById(Class<T> arg0, Object arg1, Object arg2)
			throws DataAccessException {
		return actualSpace.readIfExistsById(arg0, arg1, arg2);
	}

	public <T> T readIfExistsById(Class<T> arg0, Object arg1)
			throws DataAccessException {
		return actualSpace.readIfExistsById(arg0, arg1);
	}

	public <T> T readIfExistsById(IdQuery<T> arg0, long arg1, int arg2)
			throws DataAccessException {
		return actualSpace.readIfExistsById(arg0, arg1, arg2);
	}

	public <T> T readIfExistsById(IdQuery<T> arg0, long arg1, ReadModifiers arg2)
			throws DataAccessException {
		return actualSpace.readIfExistsById(arg0, arg1, arg2);
	}

	public <T> T readIfExistsById(IdQuery<T> arg0, long arg1)
			throws DataAccessException {
		return actualSpace.readIfExistsById(arg0, arg1);
	}

	public <T> T readIfExistsById(IdQuery<T> arg0) throws DataAccessException {
		return actualSpace.readIfExistsById(arg0);
	}

	public <T> T[] readMultiple(ISpaceQuery<T> arg0, int arg1, int arg2)
			throws DataAccessException {
		return actualSpace.readMultiple(arg0, arg1, arg2);
	}

	public <T> T[] readMultiple(ISpaceQuery<T> arg0, int arg1,
			ReadModifiers arg2) throws DataAccessException {
		return actualSpace.readMultiple(arg0, arg1, arg2);
	}

	public <T> T[] readMultiple(ISpaceQuery<T> arg0, int arg1)
			throws DataAccessException {
		return actualSpace.readMultiple(arg0, arg1);
	}

	public <T> T[] readMultiple(ISpaceQuery<T> arg0) throws DataAccessException {
		return actualSpace.readMultiple(arg0);
	}

	public <T> T[] readMultiple(T arg0, int arg1, int arg2)
			throws DataAccessException {
		return actualSpace.readMultiple(arg0, arg1, arg2);
	}

	public <T> T[] readMultiple(T arg0, int arg1, ReadModifiers arg2)
			throws DataAccessException {
		return actualSpace.readMultiple(arg0, arg1, arg2);
	}

	public <T> T[] readMultiple(T arg0, int arg1) throws DataAccessException {
		return actualSpace.readMultiple(arg0, arg1);
	}

	public <T> T[] readMultiple(T arg0) throws DataAccessException {
		return actualSpace.readMultiple(arg0);
	}

	public <T> ISpaceQuery<T> snapshot(Object arg0) throws DataAccessException {
		return actualSpace.snapshot(arg0);
	}

	public <T> T take(ISpaceQuery<T> arg0, long arg1, int arg2)
			throws DataAccessException {
		return actualSpace.take(arg0, arg1, arg2);
	}

	public <T> T take(ISpaceQuery<T> arg0, long arg1, TakeModifiers arg2)
			throws DataAccessException {
		return actualSpace.take(arg0, arg1, arg2);
	}

	public <T> T take(ISpaceQuery<T> arg0, long arg1)
			throws DataAccessException {
		return actualSpace.take(arg0, arg1);
	}

	public <T> T take(ISpaceQuery<T> arg0) throws DataAccessException {
		return actualSpace.take(arg0);
	}

	public <T> T take(T arg0, long arg1, int arg2) throws DataAccessException {
		return actualSpace.take(arg0, arg1, arg2);
	}

	public <T> T take(T arg0, long arg1, TakeModifiers arg2)
			throws DataAccessException {
		return actualSpace.take(arg0, arg1, arg2);
	}

	public <T> T take(T arg0, long arg1) throws DataAccessException {
		return actualSpace.take(arg0, arg1);
	}

	public <T> T take(T arg0) throws DataAccessException {
		return actualSpace.take(arg0);
	}

	public <T> T takeById(Class<T> arg0, Object arg1, Object arg2, long arg3,
			int arg4) throws DataAccessException {
		return actualSpace.takeById(arg0, arg1, arg2, arg3, arg4);
	}

	public <T> T takeById(Class<T> arg0, Object arg1, Object arg2, long arg3,
			TakeModifiers arg4) throws DataAccessException {
		return actualSpace.takeById(arg0, arg1, arg2, arg3, arg4);
	}

	public <T> T takeById(Class<T> arg0, Object arg1, Object arg2, long arg3)
			throws DataAccessException {
		return actualSpace.takeById(arg0, arg1, arg2, arg3);
	}

	public <T> T takeById(Class<T> arg0, Object arg1, Object arg2)
			throws DataAccessException {
		return actualSpace.takeById(arg0, arg1, arg2);
	}

	public <T> T takeById(Class<T> arg0, Object arg1)
			throws DataAccessException {
		return actualSpace.takeById(arg0, arg1);
	}

	public <T> T takeById(IdQuery<T> arg0, long arg1, int arg2)
			throws DataAccessException {
		return actualSpace.takeById(arg0, arg1, arg2);
	}

	public <T> T takeById(IdQuery<T> arg0, long arg1, TakeModifiers arg2)
			throws DataAccessException {
		return actualSpace.takeById(arg0, arg1, arg2);
	}

	public <T> T takeById(IdQuery<T> arg0, long arg1)
			throws DataAccessException {
		return actualSpace.takeById(arg0, arg1);
	}

	public <T> T takeById(IdQuery<T> arg0) throws DataAccessException {
		return actualSpace.takeById(arg0);
	}

	public <T> TakeByIdsResult<T> takeByIds(Class<T> arg0, Object[] arg1,
			int arg2) throws DataAccessException {
		return actualSpace.takeByIds(arg0, arg1, arg2);
	}

	public <T> TakeByIdsResult<T> takeByIds(Class<T> arg0, Object[] arg1,
			Object arg2, int arg3) throws DataAccessException {
		return actualSpace.takeByIds(arg0, arg1, arg2, arg3);
	}

	public <T> TakeByIdsResult<T> takeByIds(Class<T> arg0, Object[] arg1,
			Object arg2, TakeModifiers arg3) throws DataAccessException {
		return actualSpace.takeByIds(arg0, arg1, arg2, arg3);
	}

	public <T> TakeByIdsResult<T> takeByIds(Class<T> arg0, Object[] arg1,
			Object arg2) throws DataAccessException {
		return actualSpace.takeByIds(arg0, arg1, arg2);
	}

	public <T> TakeByIdsResult<T> takeByIds(Class<T> arg0, Object[] arg1,
			Object[] arg2, int arg3) throws DataAccessException {
		return actualSpace.takeByIds(arg0, arg1, arg2, arg3);
	}

	public <T> TakeByIdsResult<T> takeByIds(Class<T> arg0, Object[] arg1,
			Object[] arg2, TakeModifiers arg3) throws DataAccessException {
		return actualSpace.takeByIds(arg0, arg1, arg2, arg3);
	}

	public <T> TakeByIdsResult<T> takeByIds(Class<T> arg0, Object[] arg1,
			Object[] arg2) throws DataAccessException {
		return actualSpace.takeByIds(arg0, arg1, arg2);
	}

	public <T> TakeByIdsResult<T> takeByIds(Class<T> arg0, Object[] arg1)
			throws DataAccessException {
		return actualSpace.takeByIds(arg0, arg1);
	}

	public <T> TakeByIdsResult<T> takeByIds(IdsQuery<T> arg0, int arg1)
			throws DataAccessException {
		return actualSpace.takeByIds(arg0, arg1);
	}

	public <T> TakeByIdsResult<T> takeByIds(IdsQuery<T> arg0, TakeModifiers arg1)
			throws DataAccessException {
		return actualSpace.takeByIds(arg0, arg1);
	}

	public <T> TakeByIdsResult<T> takeByIds(IdsQuery<T> arg0)
			throws DataAccessException {
		return actualSpace.takeByIds(arg0);
	}

	public <T> T takeIfExists(ISpaceQuery<T> arg0, long arg1, int arg2)
			throws DataAccessException {
		return actualSpace.takeIfExists(arg0, arg1, arg2);
	}

	public <T> T takeIfExists(ISpaceQuery<T> arg0, long arg1, TakeModifiers arg2)
			throws DataAccessException {
		return actualSpace.takeIfExists(arg0, arg1, arg2);
	}

	public <T> T takeIfExists(ISpaceQuery<T> arg0, long arg1)
			throws DataAccessException {
		return actualSpace.takeIfExists(arg0, arg1);
	}

	public <T> T takeIfExists(ISpaceQuery<T> arg0) throws DataAccessException {
		return actualSpace.takeIfExists(arg0);
	}

	public <T> T takeIfExists(T arg0, long arg1, int arg2)
			throws DataAccessException {
		return actualSpace.takeIfExists(arg0, arg1, arg2);
	}

	public <T> T takeIfExists(T arg0, long arg1, TakeModifiers arg2)
			throws DataAccessException {
		return actualSpace.takeIfExists(arg0, arg1, arg2);
	}

	public <T> T takeIfExists(T arg0, long arg1) throws DataAccessException {
		return actualSpace.takeIfExists(arg0, arg1);
	}

	public <T> T takeIfExists(T arg0) throws DataAccessException {
		return actualSpace.takeIfExists(arg0);
	}

	public <T> T takeIfExistsById(Class<T> arg0, Object arg1, Object arg2,
			long arg3, int arg4) throws DataAccessException {
		return actualSpace.takeIfExistsById(arg0, arg1, arg2, arg3, arg4);
	}

	public <T> T takeIfExistsById(Class<T> arg0, Object arg1, Object arg2,
			long arg3, TakeModifiers arg4) throws DataAccessException {
		return actualSpace.takeIfExistsById(arg0, arg1, arg2, arg3, arg4);
	}

	public <T> T takeIfExistsById(Class<T> arg0, Object arg1, Object arg2,
			long arg3) throws DataAccessException {
		return actualSpace.takeIfExistsById(arg0, arg1, arg2, arg3);
	}

	public <T> T takeIfExistsById(Class<T> arg0, Object arg1, Object arg2)
			throws DataAccessException {
		return actualSpace.takeIfExistsById(arg0, arg1, arg2);
	}

	public <T> T takeIfExistsById(Class<T> arg0, Object arg1)
			throws DataAccessException {
		return actualSpace.takeIfExistsById(arg0, arg1);
	}

	public <T> T takeIfExistsById(IdQuery<T> arg0, long arg1, int arg2)
			throws DataAccessException {
		return actualSpace.takeIfExistsById(arg0, arg1, arg2);
	}

	public <T> T takeIfExistsById(IdQuery<T> arg0, long arg1, TakeModifiers arg2)
			throws DataAccessException {
		return actualSpace.takeIfExistsById(arg0, arg1, arg2);
	}

	public <T> T takeIfExistsById(IdQuery<T> arg0, long arg1)
			throws DataAccessException {
		return actualSpace.takeIfExistsById(arg0, arg1);
	}

	public <T> T takeIfExistsById(IdQuery<T> arg0) throws DataAccessException {
		return actualSpace.takeIfExistsById(arg0);
	}

	public <T> T[] takeMultiple(ISpaceQuery<T> arg0, int arg1, int arg2)
			throws DataAccessException {
		return actualSpace.takeMultiple(arg0, arg1, arg2);
	}

	public <T> T[] takeMultiple(ISpaceQuery<T> arg0, int arg1,
			TakeModifiers arg2) throws DataAccessException {
		return actualSpace.takeMultiple(arg0, arg1, arg2);
	}

	public <T> T[] takeMultiple(ISpaceQuery<T> arg0, int arg1)
			throws DataAccessException {
		return actualSpace.takeMultiple(arg0, arg1);
	}

	public <T> T[] takeMultiple(ISpaceQuery<T> arg0) throws DataAccessException {
		return actualSpace.takeMultiple(arg0);
	}

	public <T> T[] takeMultiple(T arg0, int arg1, int arg2)
			throws DataAccessException {
		return actualSpace.takeMultiple(arg0, arg1, arg2);
	}

	public <T> T[] takeMultiple(T arg0, int arg1, TakeModifiers arg2)
			throws DataAccessException {
		return actualSpace.takeMultiple(arg0, arg1, arg2);
	}

	public <T> T[] takeMultiple(T arg0, int arg1) throws DataAccessException {
		return actualSpace.takeMultiple(arg0, arg1);
	}

	public <T> T[] takeMultiple(T arg0) throws DataAccessException {
		return actualSpace.takeMultiple(arg0);
	}

	public <T> LeaseContext<T> write(T arg0, long arg1, long arg2, int arg3)
			throws DataAccessException {
		return actualSpace.write(arg0, arg1, arg2, arg3);
	}

	public <T> LeaseContext<T> write(T arg0, long arg1, long arg2,
			WriteModifiers arg3) throws DataAccessException {
		return actualSpace.write(arg0, arg1, arg2, arg3);
	}

	public <T> LeaseContext<T> write(T arg0, long arg1)
			throws DataAccessException {
		return actualSpace.write(arg0, arg1);
	}

	public <T> LeaseContext<T> write(T arg0, WriteModifiers arg1)
			throws DataAccessException {
		return actualSpace.write(arg0, arg1);
	}

	public <T> LeaseContext<T> write(T arg0) throws DataAccessException {
		//This really complex code should be replaced by Spring AOP
		PerformanceIntercept<LeaseContext<T>> intercept = PerformanceIntercept.measure(
				new PerformanceCallback<LeaseContext<T>>() {

			@SuppressWarnings("unchecked")
			@Override
			public LeaseContext<T> call(Object[] parameters) {
				return actualSpace.write((T)parameters[0]);
			}
			//This really should find out the caller and log as if from that class and method.
		}, new Object [] {arg0}, this.actualSpace.getClass().getName(), "write(T)");
		
		Object e = null;
		if ((e = intercept.getException()) != null) {
			//This is why this is complex:
			//A generic method can't predict which Exception class will be thrown,
			//and you can't throw Exception, and it could be multiple, unrelated Exceptions.
			throw (DataAccessException)e;
		}
//		LOGGER.log(Level.SEVERE, intercept.toString());
		try {
			super.getExposer().expose(intercept.getPerformanceItem());
		} catch (Exception e1) {
			LOGGER.log(Level.INFO,
					"Exception while exposing the performance measurement: "
							+ e1.getMessage());
			if (isGoodConfiguration())
				e1.printStackTrace(System.err);
		}
		return intercept.getResult();
	}

	public void checkConfiguration() {
		System.out.println(this.isGoodConfiguration() ? "Configuration is fine." : "Configuration is broken.");
	}
	
	protected MBeanServer server;
	
	public MBeanServer getMbeanServer() {
		return server;
	}
	
	public void setMbeanServer(MBeanServer server) {
		this.server = server;
		//lookup the MBean
		Set<ObjectInstance> instances = null;
		try {
			instances = server.queryMBeans(new ObjectName(MeasurementExposer.BEAN_ID), null);
			if (null != instances && instances.size() > 0) {
				ObjectInstance instance = (ObjectInstance) instances.toArray()[0];
				this.setExposer((MeasurementExposerMBean) instance);
			} else {
				System.out.println("Couldn't find the MeasurementExposer MBean.");
			}
		} catch (MalformedObjectNameException e) {
			e.printStackTrace(System.err);
		}

	}
	
	public <T> LeaseContext<T>[] writeMultiple(T[] arg0, long arg1, int arg2)
			throws DataAccessException {
		return actualSpace.writeMultiple(arg0, arg1, arg2);
	}

	public <T> LeaseContext<T>[] writeMultiple(T[] arg0, long arg1,
			WriteModifiers arg2) throws DataAccessException {
		return actualSpace.writeMultiple(arg0, arg1, arg2);
	}

	public <T> LeaseContext<T>[] writeMultiple(T[] arg0, long arg1)
			throws DataAccessException {
		return actualSpace.writeMultiple(arg0, arg1);
	}

	public <T> LeaseContext<T>[] writeMultiple(T[] arg0, long[] arg1, int arg2)
			throws DataAccessException {
		return actualSpace.writeMultiple(arg0, arg1, arg2);
	}

	public <T> LeaseContext<T>[] writeMultiple(T[] arg0, long[] arg1,
			WriteModifiers arg2) throws DataAccessException {
		return actualSpace.writeMultiple(arg0, arg1, arg2);
	}

	public <T> LeaseContext<T>[] writeMultiple(T[] arg0, WriteModifiers arg1)
			throws DataAccessException {
		return actualSpace.writeMultiple(arg0, arg1);
	}

	public <T> LeaseContext<T>[] writeMultiple(T[] arg0)
			throws DataAccessException {
		return actualSpace.writeMultiple(arg0);
	}


}
