package com.gigaspaces.sbp.metrics.visitor;

import com.gigaspaces.sbp.metrics.FullMetric;
import com.gigaspaces.sbp.metrics.NamedMetric;
import org.openspaces.admin.Admin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CsvVisitor extends AbstractStatsVisitor{

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<NamedMetric, String> metrics = new HashMap<>();

    public CsvVisitor(Admin admin, String spaceName){
        super(admin, spaceName);
    }

    @Override
    public void saveStat(NamedMetric metric, String value) {
        metrics.put(metric, value);
    }

    @Override
    public void saveStat(FullMetric fullMetric) {

    }

    @Override
    public boolean isSavedOnce(NamedMetric metric) {
        return metrics.containsKey(metric);
    }

    @Override
    public void saveOnce(NamedMetric metric, String value) {

    }

    @Override
    public void saveOnce(FullMetric fullMetric) {

    }
}
