package com.gigaspaces.sbp.metrics.metric;

import com.gigaspaces.sbp.metrics.FullMetric;
import com.gigaspaces.sbp.metrics.visitor.StatsVisitor;
import org.openspaces.admin.pu.ProcessingUnitInstance;
import org.openspaces.admin.space.SpaceInstance;

public enum GigaSpacesClusterInfo implements NamedMetric {

    SPACE_MODE("space_mode"){
        public void accept(StatsVisitor statsVisitor){
            for (SpaceInstance spaceInstance : statsVisitor.spaceInstance()){
                statsVisitor.saveStat(new FullMetric.FullMetricBuilder().metric(this).metricValue(spaceInstance.getMode().name()).
                        spaceInstanceID(spaceInstance.getSpaceInstanceName()).create());
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
