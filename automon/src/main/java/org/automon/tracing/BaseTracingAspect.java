package org.automon.tracing;

import org.automon.tracing.jmx.TraceJmxController;
import org.automon.utils.LogTracingHelper;
import org.automon.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract base aspect for tracing using AspectJ.
 * This class provides common functionality for tracing method executions.
 *
 * <p>Logging can be enableLogging or disabled using the `enableLogging` method or by providing the `enableLogging` flag in the constructor.</p>
 *
 * <p>This aspect has an associated JMX MBean that can be used to configure it.
 * Note this aspect is created as a singleton as always is the case by default in aspectj.
 * </p>
 */
public class BaseTracingAspect {
    /**
     * Logger instance for the aspect, using the aspect's class name.
     */
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());

    /**
     * Constant to indicate the "BEFORE" phase in tracing. For example entering a method.
     */
    protected static final String BEFORE = "BEFORE";

    /**
     * Constant to indicate the "AFTER" phase in tracing. For example exiting a method.
     */
    protected static final String AFTER = "AFTER";

    /**
     * Helper instance for log tracing operations.
     */
    protected final LogTracingHelper helper = LogTracingHelper.getInstance();

    /**
     * The JMX controller responsible for managing tracing aspects.
     * This controller is created as a singleton and provides access to
     * tracing-related functionalities (such as enable/disable) through JMX.
     */
    protected static final TraceJmxController jmxController = new TraceJmxController();

    /**
     * The value associated with the key 'purpose' in jmx registration.
     */
    private String purpose = "trace";

    protected void initialize(String purpose, boolean enable, boolean enableLogging) {
        setPurpose(purpose);
        jmxController.enable(enable);     // Set overall tracing enabled state
        jmxController.enableLogging(enableLogging); // Set logging enabled state
        registerJmxController();

        LOGGER.info("Aspect configuration and JMX registration - AspectPurpose: {}, isEnabled: {}, isLoggingEnabled: {}",
                 purpose, jmxController.isEnabled(), jmxController.isLoggingEnabled());
    }


    /**
     * AfterThrowing advice for handling exceptions.
     * Adds the collection context to the MDC/NDC context for the exception event to the existing
     * context already added from the method entry from the {@link #around} method and also conditionally logs the information if
     * logging is enableLogging for this class.
     * The following Example outputs which use SLF4J's MDC and NDC.
     * <p>
     * basic context example
     * <p>
     *  024-08-18 10:28:35,811 ERROR c.s.a.l.a.b.BasicContextTracingAspect - AFTER: MDC={NDC0=MyLoggerClassBasic.main(..), NDC1=MyLoggerClassBasic.checkedException(), exception=java.lang.Exception, kind=method-execution}
     *  java.lang.Exception: checkedException
     *  at com.stevesouza.aspectj.logging.automon.basic.MyLoggerClassBasic.checkedException_aroundBody8(MyLoggerClassBasic.java:22) ~[classes/:?]
     *  at com.stevesouza.aspectj.logging.automon.basic.MyLoggerClassBasic.checkedException_aroundBody9$advice(MyLoggerClassBasic.java:22) ~[classes/:?]
     *  at com.stevesouza.aspectj.logging.automon.basic.MyLoggerClassBasic.checkedException(MyLoggerClassBasic.java:1) [classes/:?]
     *  at com.stevesouza.aspectj.logging.automon.basic.MyLoggerClassBasic.main_aroundBody10(MyLoggerClassBasic.java:39) [classes/:?]
     *  at com.stevesouza.aspectj.logging.automon.basic.MyLoggerClassBasic.main_aroundBody11$advice(MyLoggerClassBasic.java:22) [classes/:?]
     *  at com.stevesouza.aspectj.logging.automon.basic.MyLoggerClassBasic.main(MyLoggerClassBasic.java:26) [classes/:?]
     * </p>
     *
     * full context example
     *      <p>
     *       2024-08-18 10:45:31,129 ERROR c.s.a.l.a.a.TracingAllAspect - AFTER: MDC={NDC0=MyLoggerClassAll.main(..), NDC1=MyLoggerClassAll.runtimeException(), enclosingSignature=MyLoggerClassAll.runtimeException(), exception=java.lang.RuntimeException, kind=method-execution, parameters={message=steve}, target=com.stevesouza.aspectj.logging.automon.all.MyLoggerClassAll@6ed3f258, this=com.stevesouza.aspectj.logging.automon.all.MyLoggerClassAll@6ed3f258}
     *        java.lang.RuntimeException: runtimeException
     *         at com.stevesouza.aspectj.logging.automon.all.MyLoggerClassAll.runtimeException_aroundBody6(MyLoggerClassAll.java:21) ~[classes/:?]
     *         at com.stevesouza.aspectj.logging.automon.all.MyLoggerClassAll.runtimeException_aroundBody7$advice(MyLoggerClassAll.java:22) ~[classes/:?]
     *         at com.stevesouza.aspectj.logging.automon.all.MyLoggerClassAll.runtimeException(MyLoggerClassAll.java:1) [classes/:?]
     *         at com.stevesouza.aspectj.logging.automon.all.MyLoggerClassAll.main_aroundBody8(MyLoggerClassAll.java:37) [classes/:?]
     *         at com.stevesouza.aspectj.logging.automon.all.MyLoggerClassAll.main_aroundBody9$advice(MyLoggerClassAll.java:22) [classes/:?]
     *         at com.stevesouza.aspectj.logging.automon.all.MyLoggerClassAll.main(MyLoggerClassAll.java:1) [classes/:?]     * </p>
     *      </p>
     *
     * @param throwable The thrown exception.
     */
    protected void afterThrowing(Throwable throwable) {
        // note the helper object is AutoCloseable which will clear up the NDC/MDC appropriately.
        // using this ensures if an exception is thrown it is still cleaned up.
        try (helper) {
            helper.withException(throwable.getClass().getCanonicalName());
            if (jmxController.isLoggingEnabled()) {
                LOGGER.error(AFTER, throwable);
            }
        }
    }

    /**
     * Logs a "BEFORE" message using the associated logger,
     * but only if logging is currently enableLogging.
     */
    protected void logBefore() {
        if (jmxController.isLoggingEnabled()) {
            LOGGER.info(BEFORE);
        }
    }

    /**
     * Logs an "AFTER" message using the associated logger,
     * but only if logging is currently enableLogging.
     */
    protected void logAfter() {
        if (jmxController.isLoggingEnabled()) {
            LOGGER.info(AFTER);
        }
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

    /**
     * Retrieves the singleton instance of the {@link TraceJmxController}.
     *
     * @return The JMX controller for tracing aspects.
     */
    public static TraceJmxController getJmxController() {
        return jmxController;
    }

    /**
     * Checks if tracing is currently enabled.
     *
     * @return {@code true} if tracing is enabled, {@code false} otherwise.
     */
    public static boolean isEnabled() {
        return jmxController.isEnabled();
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