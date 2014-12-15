package com.gigaspaces.sbp.metrics;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ActorSystemEdenTest {

    ActorSystemEden testInstance;

    @Before
    public void init(){
        testInstance = new ActorSystemEden();
    }

    @Test
    public void testGetSystem() throws Exception {
        assertNotNull(testInstance.getSystem());
    }
}