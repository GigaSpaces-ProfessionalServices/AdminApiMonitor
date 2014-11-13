package com.gigaspaces.sbp.metrics.bootstrap;

import com.gigaspaces.sbp.metrics.bootstrap.CreateGsMonitorSettings;
import com.gigaspaces.sbp.metrics.bootstrap.GsMonitorSettings;
import org.apache.commons.cli.ParseException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 11/10/14
 * Time: 3:49 PM
 */
public class Main {

    private static final String APPLICATION_CONTEXT_PATH = "/META-INF/spring/admin-api-context.xml";

    private static AbstractApplicationContext context;

    public static void main(String[] args) throws Exception {

        context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_PATH);

        processSettings(args);

        connectToXapGrids();

        run();

    }

    private static void processSettings(String[] args) throws ParseException {

        CreateGsMonitorSettings createSettings
                = (CreateGsMonitorSettings) context.getBean("createGsMonitorSettings");
        createSettings.invoke(args);

        setOutputFile(); // is the order of operations correct here?

    }

    /**
     * This setting is passed from CLI or gs.monitor.properties. Set it into
     * ENV so that logback.xml can pick it up.
     */
    private static void setOutputFile() {
        GsMonitorSettings settings = (GsMonitorSettings) context.getBean("gsMonitorSettings");
        System.setProperty("outputFile", settings.getOutputFilePath());
    }

    private static void connectToXapGrids() {

    }

    private static void run() {

    }


}