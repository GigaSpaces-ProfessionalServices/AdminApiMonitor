package com.gigaspaces.sbp.metrics.metric;

import org.openspaces.admin.GridComponent;

public interface GridComponentMetric extends NamedMetric{

    String getMetricValue(GridComponent gridComponent);

}
