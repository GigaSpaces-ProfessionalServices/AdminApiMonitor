package com.gigaspaces.sbp.metrics.metric;

import com.gigaspaces.cluster.replication.async.mirror.MirrorStatistics;
import com.gigaspaces.sbp.metrics.visitor.StatsVisitor;
import com.j_spaces.core.filters.ReplicationStatistics;
import org.openspaces.admin.gsc.GridServiceContainer;
import org.openspaces.admin.space.SpaceInstance;
import org.openspaces.admin.space.SpaceInstanceStatistics;
import org.openspaces.admin.vm.VirtualMachineDetails;
import org.openspaces.admin.vm.VirtualMachineStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 8/4/14
 * Time: 5:47 PM
 *
 * A collection of a bunch of names for metrics we can easily gather from Admin API.
 * And a few that customers want us to gather anyway.
 */

/**
 * An interface for metrics that have names...
 */
public interface NamedMetric {

    /**
     * @return human-friendly name for this metric
     */
    String displayName();

    /**
     * Accepts a statsVisitor and saves of a statistic...
     * @param statsVisitor visitor
     */
    void accept(StatsVisitor statsVisitor);
}

class JmxUtils{

    static final String OS_SEARCH_STRING = "java.lang:type=OperatingSystem";

    MBeanServerConnection mbeanServer(VirtualMachineDetails details, String queryString) throws IOException, MalformedObjectNameException {
        JMXServiceURL jmxUrl = new JMXServiceURL(details.getJmxUrl());
        JMXConnector connection = JMXConnectorFactory.newJMXConnector(jmxUrl, null);
        connection.connect(null);
        return connection.getMBeanServerConnection();
    }

}

