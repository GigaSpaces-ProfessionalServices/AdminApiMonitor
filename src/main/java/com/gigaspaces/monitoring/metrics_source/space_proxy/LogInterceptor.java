package com.gigaspaces.monitoring.metrics_source.space_proxy;

import com.gigaspaces.async.AsyncFuture;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Required;


public class LogInterceptor implements MethodInterceptor {

    private MeasurementExposerMBean exposer;

    @Required
    public void setExposer(MeasurementExposerMBean exposer) {
        this.exposer = exposer;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        boolean asyncCall = false;
        long startTime = System.currentTimeMillis();
        SimplePerformanceItem performanceItem = new SimplePerformanceItem();
        performanceItem.setSourceClassName(methodInvocation.getMethod().getDeclaringClass().getName());
        performanceItem.setSourceMethodName(methodInvocation.getMethod().getName());
        performanceItem.setStartTime(startTime);
        try {
            Object result = methodInvocation.proceed();
            performanceItem.setCacheHitOrMiss(result);
            if (result instanceof AsyncFuture){
                asyncCall = true;
                result = new AsyncFuturePerfSource((AsyncFuture)result, performanceItem, exposer);
            }
            return result;
        } catch (Exception e) {
            performanceItem.setInException(true);
            performanceItem.setStackTrace(e);
            throw e;
        }   finally {
            //if call was async => performance item will be exposed later after AsyncFuture.get call
            if (!asyncCall){
                try {
                    performanceItem.setElapsedTime((int)(System.currentTimeMillis() - startTime));
                    exposer.expose(performanceItem);
                }   catch (Exception e){

                }
            }
        }
    }

}
