package com.gigaspaces.sbp.metrics.metric;

import com.gigaspaces.sbp.metrics.visitor.StatsVisitor;
import org.openspaces.admin.space.Space;
import org.openspaces.admin.space.SpaceInstance;

import java.util.*;

public enum CacheContentMetric implements NamedMetric{

    NUMBER_OF_OBJECTS_IN_CACHE("instanceCount"){
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
    },
    NUMBER_OF_OBJECTS_IN_CACHE_BY_SPACE_INSTANCE("cacheNum"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            for (SpaceInstance spaceInstance : statsVisitor.spaceInstance()){
                Map<String, Integer> countPerClassName = spaceInstance.getRuntimeDetails().getCountPerClassName();
                for (String className : countPerClassName.keySet()){
                    FullMetric metric = new FullMetric.FullMetricBuilder().metric(this).metricValue(String.valueOf(countPerClassName.get(className)))
                            .qualifier(className).spaceInstanceID(spaceInstance.getSpaceInstanceName()).create();
                    statsVisitor.saveStat(metric);
                }
            }
        }
    },
    TOTAL_NOTIFY_TEMPLATES("total_notify_templates"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            Long templateCount = 0l;
            for (SpaceInstance spaceInstance : statsVisitor.spaceInstance()){
                templateCount += spaceInstance.getStatistics().getNotifyTemplateCount();
            }
            FullMetric metric = new FullMetric.FullMetricBuilder().metric(this).metricValue(String.valueOf(templateCount)).create();
            statsVisitor.saveStat(metric);
        }
    },
    NOTIFY_TEMPLATES("notify_templates"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            for (SpaceInstance spaceInstance : statsVisitor.spaceInstance()){
                Long templateCount = spaceInstance.getStatistics().getNotifyTemplateCount();
                FullMetric metric = new FullMetric.FullMetricBuilder().metric(this).metricValue(String.valueOf(templateCount)).
                        spaceInstanceID(spaceInstance.getSpaceInstanceName()).create();
                statsVisitor.saveStat(metric);
            }
        }
    },
    TOTAL_CLASSES("total_classes"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            Set<String> classes = new HashSet<>();
            for (Space space : statsVisitor.admin().getSpaces()){
                String[] classNames = space.getRuntimeDetails().getClassNames();
                classes.addAll(Arrays.asList(classNames));
            }
            FullMetric metric = new FullMetric.FullMetricBuilder().metric(this).metricValue(String.valueOf(classes.size())).create();
            statsVisitor.saveStat(metric);
        }
    },
    CLASSES("classes"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            for (Space space : statsVisitor.admin().getSpaces()){
                Integer classesCount = space.getRuntimeDetails().getClassNames().length;
                FullMetric metric = new FullMetric.FullMetricBuilder().metric(this).metricValue(String.valueOf(classesCount))
                        .qualifier(space.getName()).create();
                statsVisitor.saveStat(metric);
            }
        }
    }
    ;



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
