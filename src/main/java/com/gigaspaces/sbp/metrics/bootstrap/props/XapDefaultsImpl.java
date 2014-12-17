package com.gigaspaces.sbp.metrics.bootstrap.props;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 11/10/14
 * Time: 2:13 PM
 * Provides a simple way of loading defaults.
 */
class XapDefaultsImpl extends PropertyFileValues implements XapDefaults {

    private static final String LOOKUP_LOCATORS_PROP_NAME = "spaceMonitor.locators";
    private static final String SPACE_NAMES_PROP_NAME = "spaceMonitor.spaceName";

    private static final String XAP_SECURITY_PROP_NAME = "spaceMonitor.secured";

    XapDefaultsImpl(String propertyFilename) {
        super(propertyFilename);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String lookupLocators() {
        return getPropOrThrow(LOOKUP_LOCATORS_PROP_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String spaceNames() {
        return getPropOrThrow(SPACE_NAMES_PROP_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isSecured() {
        return Boolean.TRUE.toString().equalsIgnoreCase(getPropOrThrow(XAP_SECURITY_PROP_NAME));
    }

}