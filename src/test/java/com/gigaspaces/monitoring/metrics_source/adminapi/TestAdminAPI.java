package com.gigaspaces.monitoring.metrics_source.adminapi;

import com.j_spaces.core.IJSpace;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openspaces.core.GigaSpace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static org.junit.Assert.*;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration(locations = {"/META-INF/spring/pu.xml"})
public class TestAdminAPI {

    @Autowired
    private IJSpace space;

    @Autowired
    private GigaSpace gigaSpace;

    @Autowired
    private ApplicationContext applicationContext;

    @Before
    public void setupSpace() {

        gigaSpace.write(new Message(1, "asd"));
    }

    @Test
    public void testAdminAPIStart() throws InterruptedException {
        AdminAPIMonitor spaceMonitor = applicationContext.getBean("spaceMonitor", AdminAPIMonitor.class);
        Map<Long, AverageStat> lastCollectedStat = spaceMonitor.getLastCollectedStat();
        Thread.sleep(1000);
        assertNotNull(lastCollectedStat);

    }

    public void setGigaSpace(GigaSpace gigaSpace) {
        this.gigaSpace = gigaSpace;
    }

    public void setSpace(IJSpace space) {
        this.space = space;
    }
}
