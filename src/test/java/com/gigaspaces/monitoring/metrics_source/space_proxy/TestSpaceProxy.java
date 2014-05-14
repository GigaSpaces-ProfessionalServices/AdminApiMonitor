package com.gigaspaces.monitoring.metrics_source.space_proxy;

import com.gigaspaces.monitoring.metrics_source.feeder.Feeder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openspaces.pu.container.integrated.IntegratedProcessingUnitContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.Set;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration(locations = {"/META-INF/spring/pu.xml", "/META-INF/spring/mbean-server.xml"})
public class TestSpaceProxy {

    private MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

    @Autowired
    private MeasurementExposerImpl exposer;

    @Autowired
    private SpaceProxyNotificationListener proxyNotificationListener;

    @Autowired
    private Feeder feeder;

    @Value( "${bean_id}" )
    private String beanId;

    private ObjectName objectName ;

    private IntegratedProcessingUnitContainer processingUnitContainer;

    @Before
    public void setup() throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException {
        objectName = new ObjectName(beanId);
        mBeanServer.registerMBean(exposer, objectName);
        exposer.startSpewing();
        feeder.startFeed();
    }

    @Test
    public void testSpaceProxy(){
        System.out.println("TEST");
        queryMBean(mBeanServer, objectName);
        //Wait for 10 minutes
        try {
            Thread.sleep(2*60*1000);
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
    }

    protected static void queryMBean(MBeanServer server, ObjectName objectName) {
        Set<ObjectInstance> instances = server.queryMBeans(objectName, null);
        ObjectInstance instance = (ObjectInstance) instances.toArray()[0];
        System.out.println("Class name:\t" + instance.getClassName());
        System.out.println("Object name:\t" + instance.getObjectName());
    }

}
