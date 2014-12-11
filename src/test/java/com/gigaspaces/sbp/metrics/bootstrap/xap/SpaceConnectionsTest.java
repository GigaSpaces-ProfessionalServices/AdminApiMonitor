package com.gigaspaces.sbp.metrics.bootstrap.xap;

import com.jasonnerothin.testing.Strings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openspaces.admin.Admin;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class SpaceConnectionsTest {

    private Strings strings;
    @Mock
    private Admin admin;

    @Before
    public void setup(){
        strings = new Strings();
    }

    @Test
    public void testConnect() throws Exception {
        assertNotNull(new SpaceConnections().connect(strings.alphabetic(), admin));
    }

}