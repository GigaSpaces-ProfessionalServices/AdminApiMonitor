package com.gigaspaces.sbp.metrics.bootstrap.props;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 11/10/14
 * Time: 2:49 PM
 */
abstract class PropertyFileValues {

    private static final String LOADING_PROPERTIES_FILE_MESSAGE = "Loading properties from classpath resource: '%s'";
    private static final String PROPERTY_FILE_LOAD_ERROR = "Error loading properties file '%s' from classpath.";
    private static final String PROPERTY_FILE_INVARIANT = "Require a property file to load properties from.";
    private static final String PROPERTY_NOT_PRESENT_ERROR
            = "Classpath resource (properties file '%s') does not define required property '%s'.";

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Properties properties = new Properties();

    private final String propertyFileName;

    PropertyFileValues(String propertyFileName) {
        assert propertyFileName != null : PROPERTY_FILE_INVARIANT;
        this.propertyFileName = propertyFileName;
        logger.debug(String.format(LOADING_PROPERTIES_FILE_MESSAGE, propertyFileName));
        try {
            properties.load(PropertyFileValues.class.getClassLoader().getResourceAsStream(propertyFileName));
        } catch (IOException | NullPointerException e) {
            ExceptionInInitializerError err = new ExceptionInInitializerError(e);
            logger.error(String.format(PROPERTY_FILE_LOAD_ERROR, propertyFileName), err);
            throw err;
        }
    }

    String getPropOrThrow(String propertyName) {
        String propValue = properties.getProperty(propertyName);
        if (propValue == null) {
            IllegalStateException ise = new IllegalStateException(String.format(PROPERTY_NOT_PRESENT_ERROR, propertyFileName, propertyName));
            logger.warn("ERROR", ise);
            throw ise;
        }

        return propValue;
    }

}
