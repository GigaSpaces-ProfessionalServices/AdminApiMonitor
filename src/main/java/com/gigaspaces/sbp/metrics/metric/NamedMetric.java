package com.gigaspaces.sbp.metrics.metric;

import com.gigaspaces.sbp.metrics.visitor.StatsVisitor;
import org.openspaces.admin.vm.VirtualMachineDetails;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.*;

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

    static final Map<String,MBeanServerConnection> connections = new HashMap<>();

    MBeanServerConnection mbeanServer(VirtualMachineDetails details) throws IOException, MalformedObjectNameException {
        String jmxUrl1 = details.getJmxUrl();
        if (!connections.containsKey(jmxUrl1)){
            JMXServiceURL jmxUrl = new JMXServiceURL(jmxUrl1);
            JMXConnector connection = JMXConnectorFactory.connect(jmxUrl);
            connection.connect(null);
            MBeanServerConnection mBeanServerConnection = connection.getMBeanServerConnection();
            connections.put(jmxUrl1, mBeanServerConnection);
            return mBeanServerConnection;
        }   else {
            return connections.get(jmxUrl1);
        }

    }

}

