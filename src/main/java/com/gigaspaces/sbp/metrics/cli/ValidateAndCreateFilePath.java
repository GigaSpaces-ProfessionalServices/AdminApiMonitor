package com.gigaspaces.sbp.metrics.cli;

import org.apache.commons.cli.ParseException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 11/7/14
 * Time: 9:30 AM
 */
@Component
public class ValidateAndCreateFilePath {

    private static final String CANNOT_WRITE_TO_CWD = "Illegal outputFile. Cannot write to current working directory '%s'";
    private static final String CANNOT_CREATE_OUTPUT_FILE = "Illegal outputFile. Cannot create outputFile '%s'";

    /**
     * @param outputFile some String, supposedly representing a file path
     *
     * @return an absolute file path, either the original outputFile (if absolute) or the
     * path to a file in the current working directory. In either case, if this file did
     * not exist at the outset, one will be created.
     *
     * @throws org.apache.commons.cli.ParseException if file does not exist and cannot
     * be created (from passed file path or a like-named file in the current working directory)
     */
    public String invoke(String outputFile) throws ParseException {

        File file = new File(outputFile);
        if (file.exists()) return file.getAbsolutePath();

        File parent = file.getParentFile();
        if (parent == null) parent = new File("");

        if (!parent.canWrite())
            throw new ParseException(String.format(CANNOT_WRITE_TO_CWD, parent.getAbsolutePath()));

        File candidateFile = new File(parent, outputFile);

        String candidateFilePath = candidateFile.getAbsolutePath();

        if( candidateFile.exists() ) return candidateFilePath;

        ParseException noCanCreateThatFile =
                new ParseException(String.format(CANNOT_CREATE_OUTPUT_FILE, candidateFile.getAbsolutePath()));

        if( !candidateFile.exists() ) {
            boolean success;
            try {
                success = candidateFile.createNewFile();
            } catch (IOException e) {
                throw noCanCreateThatFile;
            }
            if( !success ) throw noCanCreateThatFile;
        }

        return candidateFilePath;

    }

}