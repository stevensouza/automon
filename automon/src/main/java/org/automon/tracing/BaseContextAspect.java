package org.automon.tracing;

import org.automon.jmx.EnableMXBean;
import org.automon.utils.LogTracingHelper;
import org.automon.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseContextAspect implements EnableMXBean {
    /**
     * Logger instance for the aspect, using the aspect's class name.
     */
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());
    /**
     * Flag indicating whether tracing is enabled.
     */
    private boolean enabled = true;

    protected static final LogTracingHelper helper = LogTracingHelper.getInstance();
    /**
     * The value associated with the key 'purpose' in jmx registration.
     */
    protected String purpose;

    /**
     * Registers the JMX controller associated with this aspect.
     * <p>
     * This method utilizes the `Utils.registerWithJmx` utility to register the JMX controller with the platform MBeanServer,
     * using the current `purpose` as part of the MBean's ObjectName.
     */
    protected void registerJmxController() {
        Utils.registerWithJmx(getPurpose(), this, this);
    }

    protected void initialize(String purpose, boolean enable) {
        setPurpose(purpose);
        enable(enable);
        registerJmxController();

        LOGGER.info("Aspect configuration and JMX registration - AspectPurpose: {}, isEnabled: {}",
                purpose, isEnabled());
    }

    /**
     * Gets the purpose associated with this JMX registration.
     *
     * @return The value associated with the key 'purpose' in JMX registration.
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * Sets the purpose associated with this JMX registration.
     *
     * @param purpose The value to be associated with the key 'purpose' in JMX registration.
     */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enable(boolean enable) {
        this.enabled = enable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
