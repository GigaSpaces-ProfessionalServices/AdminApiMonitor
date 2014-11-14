package com.gigaspaces.sbp.metrics;

import com.gigaspaces.sbp.metrics.bootstrap.BootSpring;
import com.gigaspaces.sbp.metrics.bootstrap.xap.ConnectToXap;
import com.gigaspaces.sbp.metrics.bootstrap.CreateGsMonitorSettings;
import com.gigaspaces.sbp.metrics.bootstrap.GsMonitorSettings;
import com.gigaspaces.sbp.metrics.bootstrap.cli.PrintHelpAndDie;
import org.apache.commons.cli.ParseException;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 11/10/14
 * Time: 3:49 PM
 */
public class Main {

    private BootSpring bootSpring = new BootSpring();

    private AbstractApplicationContext context;
    private ConnectToXap connectToXap;
    private CollectMetrics collectMetrics;
    private PrintHelpAndDie printHelpAndDie;

    public static void main(String[] args) throws Exception {

        Main main = new Main();

        main.bootSpring();
        main.processCommandLine(args);
        main.connectToXapGrids();

        main.run();

    }

    void bootSpring() {
        context = bootSpring.readySetGo();
        connectToXap = (ConnectToXap) context.getBean("connectToXap");
        collectMetrics = (CollectMetrics) context.getBean("collectMetrics");
        printHelpAndDie = new PrintHelpAndDie(context);
    }

    /**
     * This command is what interprets the CLI input and injects it into the spring context
     * (which holds a reference to the output of the {@link com.gigaspaces.sbp.metrics.bootstrap.CreateGsMonitorSettings#invokeOrThrow(String[])}
     * method call in a {@link com.gigaspaces.sbp.metrics.bootstrap.GsMonitorSettings} instance.
     *
     * @param args CLI parameters
     */
    void processCommandLine(String[] args){

        CreateGsMonitorSettings createSettings
                = (CreateGsMonitorSettings) context.getBean("createGsMonitorSettings");
        try {
            createSettings.invokeOrThrow(args);
        } catch( ParseException e ){
            printHelpAndDie.invoke(e);
        }

        setOutputFile();

    }

    void connectToXapGrids() {
        connectToXap.connect();
    }

    void run() {
        collectMetrics.readySetGo();
    }

    /**
     * This setting is passed from CLI or gs.monitor.properties. Set it into
     * ENV so that logback.xml can pick it up.
     */
    void setOutputFile() {
        GsMonitorSettings settings = (GsMonitorSettings) context.getBean("gsMonitorSettings");
        System.setProperty("outputFile", settings.outputFilePath());
    }

}