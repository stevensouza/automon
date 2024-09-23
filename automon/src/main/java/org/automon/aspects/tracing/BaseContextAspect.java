package org.automon.aspects.tracing;

import org.automon.jmx.EnableMXBean;
import org.automon.utils.LogTracingHelper;
import org.automon.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for aspects that manage contextual data in the SLF4J MDC (Mapped Diagnostic Context) and NDC (Nested Diagnostic Context).
 * It provides basic enable/disable functionality and JMX registration for dynamic configuration. This class only
 * writes to the context and does not log/trace the information.
 */
public class BaseContextAspect implements EnableMXBean {

    /**
     * Logger instance for the aspect.
     */
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());

    /**
     * Flag indicating whether the aspect is enabled.
     */
    private boolean enabled = true;

    /**
     * Helper instance for log tracing operations.
     */
    protected static final LogTracingHelper helper = LogTracingHelper.getInstance();

    /**
     * The purpose associated with this JMX registration.
     */
    protected String purpose;

    /**
     * Registers the JMX controller associated with this aspect.
     */
    protected void registerJmxController() {
        Utils.registerWithJmx(getPurpose(), this, this);
    }

    /**
     * Initializes the aspect with the specified purpose and enabled state.
     * It also registers the JMX controller.
     *
     * @param purpose The purpose of the aspect for JMX registration.
     * @param enable  Whether the aspect is initially enabled.
     */
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
     * @return The purpose string.
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * Sets the purpose associated with this JMX registration.
     *
     * @param purpose The purpose string.
     */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    /**
     * Enables or disables the aspect.
     *
     * @param enable `true` to enable, `false` to disable.
     */
    @Override
    public void enable(boolean enable) {
        this.enabled = enable;
    }

    /**
     * Checks if the aspect is currently enabled.
     *
     * @return `true` if enabled, `false` otherwise.
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}