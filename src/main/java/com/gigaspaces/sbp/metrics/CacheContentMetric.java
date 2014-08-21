package com.gigaspaces.sbp.metrics;

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
                statsVisitor.saveStat(new FullMetric(this, String.valueOf(numberOfObjects.get(className)), className));
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
