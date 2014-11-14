package com.gigaspaces.sbp.metrics.bootstrap.cli;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 11/12/14
 * Time: 6:42 PM
 */
public class PrintHelpAndDie {

    private static final int DIE_CODE = 666;
    private static final int TERMINAL_WIDTH = 130;
    private static final String CMD_LINE_SYNTAX = "java -jar gs-monitor.jar";

    private final AbstractApplicationContext context;

    public PrintHelpAndDie(AbstractApplicationContext context) {
        assert context != null : "need a context";
        this.context = context;
    }

    public void invoke(ParseException e){
        PrintStream out = System.err;
        out.println(e.getMessage());
        final PrintWriter writer = new PrintWriter(out);

        String headsAndTails = "\n.\n\n\n";

        final HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setSyntaxPrefix("usage:\n\n");

        CalculateSettingsFromCliArgs calculateSettingsFromCliArgs = (CalculateSettingsFromCliArgs) context.getBean("calculateSettingsFromCliArgs");
        Options allOpts = calculateSettingsFromCliArgs.allOptions();
        helpFormatter.printHelp(writer, TERMINAL_WIDTH, CMD_LINE_SYNTAX, headsAndTails, allOpts, 5, 5, headsAndTails, true);
        writer.flush();

        System.exit(DIE_CODE);

    }

}