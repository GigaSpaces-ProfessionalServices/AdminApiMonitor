package com.gigaspaces.sbp.metrics.metric;

import com.gigaspaces.sbp.metrics.FullMetric;
import com.gigaspaces.sbp.metrics.visitor.StatsVisitor;
import org.openspaces.admin.space.SpaceInstance;

import java.util.HashMap;
import java.util.Map;

public enum CacheContentMetric implements NamedMetric{

    NUMBER_OF_OBJECTS_IN_CACHE("cacheNum"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            Map<String, Integer> numberOfObjects = new HashMap<>();
            for (SpaceInstance spaceInstance : statsVisitor.spaceInstance()){
                Map<String, Integer> countPerClassName = spaceInstance.getRuntimeDetails().getCountPerClassName();
                for (String className : countPerClassName.keySet()){
                    if (!numberOfObjects.containsKey(className)){
                        numberOfObjects.put(className, countPerClassName.get(className));
                    }   else {
                        Integer existingNumber = numberOfObjects.get(className);
                        numberOfObjects.put(className, existingNumber + countPerClassName.get(className));
                    }
                }
            }
            for (String className : numberOfObjects.keySet()){
                FullMetric.FullMetricBuilder builder = new FullMetric.FullMetricBuilder();
                builder.metric(this).qualifier(className).metricValue(String.valueOf(numberOfObjects.get(className)));
                statsVisitor.saveStat(builder.create());
            }
        }
    };



    private final String displayName;

    CacheContentMetric(String displayName){
        this.displayName = displayName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String displayName() {
        return displayName;
    }

}
