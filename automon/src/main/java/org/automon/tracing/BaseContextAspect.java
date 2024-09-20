package org.automon.tracing;

import org.automon.jmx.AspectJmxController;
import org.automon.utils.LogTracingHelper;
import org.automon.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseContextAspect {
    /**
     * Logger instance for the aspect, using the aspect's class name.
     */
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());

    protected static final LogTracingHelper helper = LogTracingHelper.getInstance();
    /**
     * The JMX controller responsible for managing tracing aspects.
     * This controller is created as a singleton and provides access to
     * tracing-related functionalities (such as enable/disable) through JMX.
     */
    protected  final AspectJmxController jmxController = new AspectJmxController();
    /**
     * The value associated with the key 'purpose' in jmx registration.
     */
    protected String purpose;

    /**
     * Retrieves the singleton instance of the {@link AspectJmxController}.
     *
     * @return The JMX controller for tracing aspects.
     */
    public AspectJmxController getJmxController() {
        return jmxController;
    }

    /**
     * Checks if tracing is currently enabled.
     *
     * @return {@code true} if tracing is enabled, {@code false} otherwise.
     */
    public boolean isEnabled() {
        return jmxController.isEnabled();
    }

    /**
     * Registers the JMX controller associated with this aspect.
     * <p>
     * This method utilizes the `Utils.registerWithJmx` utility to register the JMX controller with the platform MBeanServer,
     * using the current `purpose` as part of the MBean's ObjectName.
     */
    protected void registerJmxController() {
        Utils.registerWithJmx(getPurpose(), this, jmxController);
    }

    protected void initialize(String purpose, boolean enable) {
        setPurpose(purpose);
        jmxController.enable(enable);
        registerJmxController();

        LOGGER.info("Aspect configuration and JMX registration - AspectPurpose: {}, isEnabled: {}",
                purpose, jmxController.isEnabled());
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
}
