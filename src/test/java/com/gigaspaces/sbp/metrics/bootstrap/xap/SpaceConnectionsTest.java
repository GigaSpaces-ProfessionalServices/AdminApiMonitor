package com.gigaspaces.sbp.metrics.bootstrap.xap;

import com.jasonnerothin.testing.Strings;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class SpaceConnectionsTest {

    private Strings strings;

    @Before
    public void setup(){
        strings = new Strings();
    }

    @Test
    public void testConnect() throws Exception {
        assertNotNull(new SpaceConnections().connect(strings.alphabetic()));
    }

}