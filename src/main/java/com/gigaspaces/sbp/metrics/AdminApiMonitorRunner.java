package com.gigaspaces.sbp.metrics;

import com.gigaspaces.sbp.metrics.cli.ProcessArgs;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.PrintWriter;
import java.util.EnumSet;

public class AdminApiMonitorRunner {

    private static EnumSet<Settings> settings; // from command line
    private static boolean applicationContextStarted;

    private static final String APPLICATION_CONTEXT_PATH = "/META-INF/spring/admin-api-context.xml";
    private static final int WAITING_FOR_GRID_PAUSE = 5000;
    private static final int TERMINAL_WIDTH = 110;

    public static void main(String[] args) throws InterruptedException {

        ProcessArgs processArgs = new ProcessArgs();

        try {
            settings = processArgs.invoke(args);
        } catch (ParseException e) {
            System.err.println("ERROR: User error. Please try again...");
            final PrintWriter writer = new PrintWriter(System.err);
            final HelpFormatter usageFormatter = new HelpFormatter();
            usageFormatter.printUsage(writer, TERMINAL_WIDTH, "java -DjavaOpt=foo -jar " + AdminApiMonitor.class.getSimpleName() + ".jar", processArgs.getOptions());
            writer.flush();
            System.exit(666);
        }

        while (!applicationContextStarted)
            attemptStart();

    }

    private static void attemptStart() throws InterruptedException {
        try {
            startApplicationContext();
            applicationContextStarted = true;
        }  catch (BeanCreationException e){
            System.out.println("===================================================");
            System.out.println("Unable to start " + AdminApiMonitor.class.getSimpleName() + ". Retrying in " + WAITING_FOR_GRID_PAUSE / 1000 + " seconds.");
            System.out.println("===================================================");
            Thread.sleep(WAITING_FOR_GRID_PAUSE);
        }
    }

    private static void startApplicationContext(){
        new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_PATH);
    }

}
