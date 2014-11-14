package com.gigaspaces.sbp.metrics.bootstrap;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 11/12/14
 * Time: 6:30 PM
 */
public class BootSpring {

    String applicationContextPath = "/META-INF/spring/admin-api-context.xml";

    public AbstractApplicationContext readySetGo(){
        return new ClassPathXmlApplicationContext(applicationContextPath);
    }

}
