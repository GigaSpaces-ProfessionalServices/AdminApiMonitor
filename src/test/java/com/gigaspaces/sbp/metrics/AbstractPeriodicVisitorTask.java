package com.gigaspaces.sbp.metrics;

import com.gigaspaces.sbp.metrics.metric.*;
import org.openspaces.admin.alert.Alert;
import org.openspaces.admin.alert.AlertManager;
import org.openspaces.admin.alert.events.AlertTriggeredEventListener;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO replace me
 */
@Deprecated
public class AbstractPeriodicVisitorTask {

    /**
     * TODO And where does this go?
     */
    protected ConcurrentHashMap<String, AtomicInteger> alerts = new ConcurrentHashMap<>();

    @Deprecated
    public void init(){
        AlertManager alertManager = null;// = admin.getAlertManager();
        alertManager.getAlertTriggered().add(new AlertTriggeredEventListener() {
            @Override
            public void alertTriggered(Alert alert) {
                String alertName = alert.getName();
                alerts.putIfAbsent(alertName, new AtomicInteger(0));
                alerts.get(alertName).incrementAndGet();
            }
        });
    }

}
