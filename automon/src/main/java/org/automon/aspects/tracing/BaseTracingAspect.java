package org.automon.aspects.tracing;

import org.automon.jmx.TracingMXBean;
import org.automon.utils.LogTracingHelper;
import org.automon.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base aspect for tracing using AspectJ.
 * This class provides common functionality for tracing method executions, including:
 * <ul>
 *     <li>Enabling/disabling tracing and logging</li>
 *     <li>Logging 'BEFORE' and 'AFTER' messages with contextual information</li>
 *     <li>Handling exceptions and logging error messages</li>
 *     <li>Registering a JMX controller for dynamic configuration</li>
 * </ul>
 *
 * <p>Subclasses need to implement the `select()` pointcut to define the specific methods or classes to trace.</p>
 */
public abstract class BaseTracingAspect implements TracingMXBean {

    /**
     * Logger instance for the aspect.
     */
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());

    /**
     * Constant indicating the "BEFORE" phase in tracing.
     */
    protected static final String BEFORE = "BEFORE";

    /**
     * Constant indicating the "AFTER" phase in tracing.
     */
    protected static final String AFTER = "AFTER";

    /**
     * Flag indicating whether tracing is enabled.
     */
    private boolean enabled = true;

    /**
     * Flag indicating whether logging is enabled.
     */
    private boolean loggingEnabled = true;

    /**
     * Helper instance for log tracing operations.
     */
    protected final LogTracingHelper helper = LogTracingHelper.getInstance();

    /**
     * The purpose associated with this JMX registration.
     */
    private String purpose = "trace";

    /**
     * Initializes the aspect with the given purpose, enabled state, and logging enabled state.
     * It also registers the JMX controller and logs configuration information.
     *
     * @param purpose        The purpose of the aspect for JMX registration.
     * @param enable         Whether tracing is initially enabled.
     * @param enableLogging Whether logging is initially enabled.
     */
    protected void initialize(String purpose, boolean enable, boolean enableLogging) {
        setPurpose(purpose);
        enable(enable);
        enableLogging(enableLogging);
        registerJmxController();

        LOGGER.info("Aspect configuration and JMX registration - AspectPurpose: {}, isEnabled: {}, isLoggingEnabled: {}",
                purpose, isEnabled(), isLoggingEnabled());
    }

    /**
     * AfterThrowing advice for handling exceptions.
     * <p>
     * This advice is executed when an exception is thrown within the traced methods. It adds exception information to the
     * MDC/NDC context and logs an error message if logging is enabled.
     *
     * @param throwable The thrown exception.
     */
    protected void afterThrowing(Throwable throwable) {
        try (helper) { // Ensure cleanup of NDC/MDC even if an exception occurs
            helper.withException(throwable.getClass().getCanonicalName());
            if (isLoggingEnabled()) {
                LOGGER.error(AFTER, throwable); // Log the 'AFTER' message with the exception
            }
        }
    }

    /**
     * Logs a "BEFORE" message using the associated logger, only if logging is enabled.
     */
    protected void logBefore() {
        if (isLoggingEnabled()) {
            LOGGER.info(BEFORE);
        }
    }

    /**
     * Logs an "AFTER" message using the associated logger, only if logging is enabled.
     */
    protected void logAfter() {
        if (isLoggingEnabled()) {
            LOGGER.info(AFTER);
        }
    }

    /**
     * Registers the JMX controller associated with this aspect.
     */
    protected void registerJmxController() {
        Utils.registerWithJmx(getPurpose(), this, this);
    }

    /**
     * Converts an object to its string representation.
     *
     * @param obj The object to be converted.
     * @return The string representation of the object, or "null" if the object is null.
     */
    protected String objectToString(Object obj) {
        return obj == null ? "null" : obj.toString();
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
     * Enables or disables tracing.
     *
     * @param enable `true` to enable tracing, `false` to disable.
     */
    @Override
    public void enable(boolean enable) {
        this.enabled = enable;
    }

    /**
     * Checks if tracing is currently enabled.
     *
     * @return `true` if tracing is enabled, `false` otherwise.
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Enables or disables logging in this aspect.
     *
     * @param enabled `true` to enable logging, `false` to disable logging.
     */
    @Override
    public void enableLogging(boolean enabled) {
        loggingEnabled = enabled;
    }

    /**
     * Gets the current logging enabled status.
     *
     * @return `true` if logging is enabled, `false` otherwise.
     */
    @Override
    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }
}