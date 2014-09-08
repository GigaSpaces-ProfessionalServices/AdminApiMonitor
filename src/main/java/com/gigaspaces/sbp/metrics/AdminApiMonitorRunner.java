package com.gigaspaces.sbp.metrics;

import com.gigaspaces.sbp.metrics.cli.ProcessArgs;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.gsc.GridServiceContainer;
import org.openspaces.admin.gsc.GridServiceContainers;
import org.openspaces.admin.machine.Machines;
import org.openspaces.admin.space.Spaces;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class AdminApiMonitorRunner {

    private static EnumSet<Settings> settings; // from command line
    private static boolean applicationContextStarted;

    private static final String APPLICATION_CONTEXT_PATH = "/META-INF/spring/admin-api-context.xml";
    private static final int WAITING_FOR_GRID_PAUSE = 5000;
    private static final int TERMINAL_WIDTH = 110;

    // GigaSpaces configuration elements (from context.xml or properties file)
    private Admin admin;
    private String adminUser;
    private String adminPassword;
    private String locators;
    private String groups;

    // Fields to hold data that we'll report on
    private ExponentialMovingAverage averageCounter;
    private Map<Long,AdminApiMetrics> pidMetricMap = new HashMap<>();

    public void init(){
        AdminFactory factory = new AdminFactory();
        if(settings.contains(Settings.Secured)){
            factory.credentials(adminUser,adminPassword);
        }
        factory.addLocators(locators);
        factory.addGroups(groups);
        factory.discoverUnmanagedSpaces();
        admin = factory.createAdmin();

        Machines machines = admin.getMachines();
        machines.waitFor(1);
        GridServiceContainers gscs = admin.getGridServiceContainers();

        gscs.waitFor(1, 500, TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args) throws InterruptedException {

        ProcessArgs processArgs = new ProcessArgs();

        try {
            settings = processArgs.invoke(args);
            System.setProperty("csv_format", String.valueOf(settings.contains(Settings.Csv)));
        } catch (ParseException e) {
            System.err.println("ERROR: User error. Please try again...");
            final PrintWriter writer = new PrintWriter(System.err);
            final HelpFormatter usageFormatter = new HelpFormatter();
            usageFormatter.printUsage(writer, TERMINAL_WIDTH, "java -DjavaOpt=foo -jar " + AdminApiMonitorRunner.class.getSimpleName() + ".jar", processArgs.getOptions());
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
            System.out.println("Unable to start " + AdminApiMonitorRunner.class.getSimpleName() + ". Retrying in " + WAITING_FOR_GRID_PAUSE / 1000 + " seconds.");
            System.out.println("===================================================");
            Thread.sleep(WAITING_FOR_GRID_PAUSE);
        }
    }

    public Admin getAdmin() {
        return admin;
    }

    private static void startApplicationContext(){
        new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_PATH);
    }

    public void setAdminUser(String adminUser) {
        this.adminUser = adminUser;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public void setLocators(String locators) {
        this.locators = locators;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public void setAverageCounter(ExponentialMovingAverage averageCounter) {
        this.averageCounter = averageCounter;
    }
}
