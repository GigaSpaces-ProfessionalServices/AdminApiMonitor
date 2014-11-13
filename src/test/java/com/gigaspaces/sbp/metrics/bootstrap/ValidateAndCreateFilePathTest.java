package com.gigaspaces.sbp.metrics.bootstrap;

import com.jasonnerothin.testing.Files;
import com.jasonnerothin.testing.Strings;
import org.apache.commons.cli.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

@RunWith(MockitoJUnitRunner.class)
public class ValidateAndCreateFilePathTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ValidateAndCreateFilePath testInstance;

    private File unwritableDirectory;

    private File cwdFileReference; // virtual
    private File tempFile;
    private File tempFileReference; // virtual

    private Files files = new Files();
    private Strings strings = new Strings();

    @Before
    public void setup() {

        unwritableDirectory = files.createDirectoryInTemp();
        boolean success = unwritableDirectory.setWritable(false);
        if (!success) logger.warn("Directory was maybe left writable. Ignoring.");

        cwdFileReference = files.fileInCwd();
        tempFile = files.createTempFile();
        tempFileReference = files.tempFile();

        testInstance = new ValidateAndCreateFilePath();

    }

    @Test
    public void testInvokeOnFullyQualifiedExistingFile() throws Exception {

        assert (tempFile.exists());

        File actual = systemUnderTestFullyQualified(tempFile);

        existsAndEqual(actual, tempFile);

    }

    private File systemUnderTestFullyQualified(File file) throws ParseException {
        return new File(testInstance.invoke(file.getAbsolutePath()));
    }

    private void existsAndEqual(File actual, File expected) {
        assert (actual.exists());
        assert (actual.getAbsolutePath().equals(expected.getAbsolutePath()));
    }

    @Test
    public void testInvokeOnFullyQualifiedNonExistingFile() throws Exception {

        assert (!tempFileReference.exists());

        File actual = systemUnderTestFullyQualified(tempFileReference);

        existsAndEqual(actual, tempFileReference);
    }

    @Test
    public void testInvokeOnLocalNonExistingFile() throws Exception {

        assert (!cwdFileReference.exists());
        assert (!new File(cwdFileReference, cwdFileReference.getName()).exists());

        File actual = new File(testInstance.invoke(cwdFileReference.getName()));

        existsAndEqual(actual, cwdFileReference);
    }


    @Test(expected = ParseException.class)
    public void testInvokeOnFullyQualifiedNonExistingFileWhereParentDirectoryIsNotWriteable() throws Exception {

        File testFile = new File(unwritableDirectory.getAbsoluteFile(), strings.alphabetic(12));

        testInstance.invoke(testFile.getAbsolutePath());
    }

}