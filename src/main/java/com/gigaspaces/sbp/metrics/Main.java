package com.gigaspaces.sbp.metrics;

import com.gigaspaces.sbp.metrics.bootstrap.BootSpring;
import com.gigaspaces.sbp.metrics.bootstrap.CreateGsMonitorSettings;
import com.gigaspaces.sbp.metrics.bootstrap.GsMonitorSettings;
import com.gigaspaces.sbp.metrics.bootstrap.GsMonitorSettingsImpl;
import com.gigaspaces.sbp.metrics.bootstrap.cli.PrintHelpAndDie;
import com.gigaspaces.sbp.metrics.bootstrap.xap.ConnectToXap;
import org.apache.commons.cli.ParseException;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 11/10/14
 * Time: 3:49 PM
 */
public class Main {

    BootSpring bootSpring = new BootSpring();

    AbstractApplicationContext context;
    ConnectToXap connectToXap;
    CollectMetrics collectMetrics;
    PrintHelpAndDie printHelpAndDie;

    public static void main(String[] args) throws Exception {

        Main main = new Main();

        main.bootSpring();
        main.processCommandLine(args);
        main.connectToXapGrids();

        main.run();

    }

    void bootSpring() {
        context = bootSpring.readySetGo();
        connectToXap = (ConnectToXap) context.getBean(beanNameForClass(ConnectToXap.class));
        collectMetrics = (CollectMetrics) context.getBean(beanNameForClass(CollectMetrics.class));
        printHelpAndDie = new PrintHelpAndDie(context);
    }

    /**
     * This little method is a necessary evil that enables this class to know as little about the
     * Spring graph as possible. Because many settings are known at runtime (statically, through CLI),
     * we have to defer some initialization until after the CLI is processed.
     */
    static String beanNameForClass(Class<?> clazz){
        String name = clazz.getSimpleName();
        String firstChar = name.substring(0,1);
        return firstChar.toLowerCase() + name.substring(1, name.length());
    }

    /**
     * This command is what interprets the CLI input and injects it into the spring context
     * (which holds a reference to the output of the {@link com.gigaspaces.sbp.metrics.bootstrap.CreateGsMonitorSettings#invokeOrThrow(String[])}
     * method call in a {@link com.gigaspaces.sbp.metrics.bootstrap.GsMonitorSettingsImpl} instance.
     *
     * @param args CLI parameters
     */
    void processCommandLine(String[] args){

        CreateGsMonitorSettings createSettings
                = (CreateGsMonitorSettings) context.getBean(beanNameForClass(CreateGsMonitorSettings.class));
        try {
            createSettings.invokeOrThrow(args);
        } catch( ParseException e ){
            int code = printHelpAndDie.invoke(e);
            System.exit(code);
        }

        setOutputFile();

    }

    void connectToXapGrids() {
        connectToXap.invoke();
    }

    void run() {
        collectMetrics.readySetGo();
    }

    /**
     * This setting is passed from CLI or gs.monitor.properties. Set it into
     * ENV so that logback.xml can pick it up.
     */
    void setOutputFile() {
        GsMonitorSettings settings = (GsMonitorSettings) context.getBean(beanNameForClass(GsMonitorSettingsImpl.class));
        System.setProperty("outputFile", settings.outputFilePath());
    }

}