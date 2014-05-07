package com.gigaspaces.monitoring.metrics_source.space_proxy;

import com.j_spaces.core.LeaseContext;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Required;

import java.io.PrintWriter;
import java.io.StringWriter;


public class LogInterceptor implements MethodInterceptor {

    private MeasurementExposerMBean exposer;

    @Required
    public void setExposer(MeasurementExposerMBean exposer) {
        this.exposer = exposer;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        long startTime = System.currentTimeMillis();
        SimplePerformanceItem performanceItem = new SimplePerformanceItem();
        performanceItem.setSourceClassName(methodInvocation.getMethod().getDeclaringClass().getName());
        performanceItem.setSourceMethodName(methodInvocation.getMethod().getName());
        performanceItem.setStartTime(startTime);
        try {
            Object result = methodInvocation.proceed();
            performanceItem.setCacheHit(checkCacheHitOrMiss(result));
            return result;
        } catch (Exception e) {
            performanceItem.setInException(true);
            performanceItem.setExceptionStack(getStackTrace(e));
            throw e;
        }   finally {
            performanceItem.setElapsedTime((int)(System.currentTimeMillis() - startTime));
            try {
                exposer.expose(performanceItem);
            }   catch (Exception e){

            }
        }
    }

    /**
     * This method checks whether cache hit or not based on method invocation result
     * @param result
     * @return
     */
    private Boolean checkCacheHitOrMiss(Object result) {
        if (result == null){
            return false;
        }   else if (result instanceof LeaseContext){
            return null;
        }   else {
            return true;
        }
    }

    private String getStackTrace(Exception e) {
        StringWriter sOut = new StringWriter(255);
        PrintWriter writer = new PrintWriter(sOut);
        e.printStackTrace(writer);
        return sOut.toString();
    }

}
