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
    private static final String OUT_NEEDED = "Need a non-null output stream.";
    private static final String HEADS_AND_TAILS = "\n.\n\n\n";

    private final AbstractApplicationContext context;

    HelpFormatterFactory helpFormatterFactory = new HelpFormatterFactory();
    PrintWriterFactory printWriterFactory = new PrintWriterFactory(System.err);

    public PrintHelpAndDie(AbstractApplicationContext context) {
        assert context != null : "need a context";
        this.context = context;
    }

    public Integer invoke(ParseException e){

        PrintStream out = System.err;
        out.println(e.getMessage());

        final PrintWriter writer = printWriterFactory.build();
        final HelpFormatter helpFormatter = helpFormatterFactory.build();

        CalculateSettingsFromCliArgs calculateSettingsFromCliArgs = (CalculateSettingsFromCliArgs) context.getBean("calculateSettingsFromCliArgs");
        Options allOpts = calculateSettingsFromCliArgs.allOptions();
        helpFormatter.printHelp(writer, TERMINAL_WIDTH, CMD_LINE_SYNTAX, HEADS_AND_TAILS, allOpts, 5, 5, HEADS_AND_TAILS, true);
        writer.flush();

        return DIE_CODE;

    }

    class HelpFormatterFactory {

        private static final String SYNTAX_PREFIX = "usage:\n\n";

        HelpFormatter build(){
            HelpFormatter formatter = new HelpFormatter();
            formatter.setSyntaxPrefix(SYNTAX_PREFIX);
            return formatter;
        }

    }


    class PrintWriterFactory{

        final PrintStream out;

        PrintWriterFactory(PrintStream out) {
            assert out != null : OUT_NEEDED;
            this.out = out;
        }

        PrintWriter build(){
            return new PrintWriter(out);
        }

    }

}