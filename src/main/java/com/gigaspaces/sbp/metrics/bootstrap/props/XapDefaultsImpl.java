package com.gigaspaces.sbp.metrics.bootstrap.props;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 11/10/14
 * Time: 2:13 PM
 * Provides a simple way of loading defaults.
 */
class XapDefaultsImpl extends PropertyFileValues implements XapDefaults {

    private static final String LOOKUP_LOCATORS_PROP_NAME = "xap.locators";
    private static final String SPACE_NAMES_PROP_NAME = "xap.spaceNames";

    private static final String XAP_SECURITY_PROP_NAME = "xap.security.enabled";

    private static final String NUM_HOST_MACHINES = "xap.default.num.host.machines";
    private static final String NUM_GSCS = "xap.default.num.GSCs";

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

    @Override
    public Integer hostMachineCount() {
        return Integer.valueOf(getPropOrThrow(NUM_HOST_MACHINES));
    }

    @Override
    public Integer gscCount() {
        return Integer.valueOf(getPropOrThrow(NUM_GSCS));
    }

}