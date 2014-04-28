package com.gigaspaces.monitoring.metrics_source.adminapi;

import com.j_spaces.core.IJSpace;
import org.junit.Before;
import org.junit.Test;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

import static org.junit.Assert.*;

public class TestAdminAPI {

    private IJSpace space;

    private GigaSpace gigaSpace;

    private ApplicationContext applicationContext;

    @Before
    public void setupSpace(){
        space = new UrlSpaceConfigurer("/./testSpace?groups=test").space();
        gigaSpace = new GigaSpaceConfigurer(space).gigaSpace();
        gigaSpace.write(new Message(1, "asd"));
        applicationContext = new ClassPathXmlApplicationContext("META-INF/spring/pu.xml");
    }

    @Test
    public void testAdminAPIStart() throws InterruptedException {
        AdminAPIMonitor spaceMonitor = applicationContext.getBean("spaceMonitor", AdminAPIMonitor.class);
        Map<Long, AverageStat> lastCollectedStat = spaceMonitor.getLastCollectedStat();
        Thread.sleep(1000);
        assertNotNull(lastCollectedStat);

    }

}
