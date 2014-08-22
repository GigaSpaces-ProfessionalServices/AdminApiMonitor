package com.gigaspaces.sbp.metrics.metric;

import com.gigaspaces.sbp.metrics.FullMetric;
import com.gigaspaces.sbp.metrics.visitor.StatsVisitor;
import org.openspaces.admin.space.SpaceInstance;

public enum GigaSpacesClusterInfo implements NamedMetric {

    SPACE_MODE("space_mode"){
        public void accept(StatsVisitor statsVisitor){
            SpaceInstance spaceInstance = statsVisitor.spaceInstance().get(0);
            if( spaceInstance != null ){
                statsVisitor.saveStat(new FullMetric.FullMetricBuilder().metric(this).metricValue(spaceInstance.getMode().name()).create());
            }
        }
    }
    ;

    private final String displayName;

    GigaSpacesClusterInfo(String displayName){
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
