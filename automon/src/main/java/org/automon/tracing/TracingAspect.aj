package org.automon.tracing;

import org.automon.utils.LogTracingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract base aspect for tracing using AspectJ.
 * This class provides common functionality for tracing method executions.
 *
 * <p>Logging can be enabled or disabled using the `enableLogging` method or by providing the `enabled` flag in the constructor.</p>
 */
public abstract aspect TracingAspect {
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
     * The JMX MBean that controls whether this class is enabled or not (when disabled
     * it is the equivalent of a noop)
     */
    protected final TraceControlMBean traceControl = new TraceControl();

    private boolean loggingEnabled = true; // Default to logging enabled

    /**
     * Constructs a new `TracingAspect` with logging enabled by default.
     */
    public TracingAspect() {
    }

    /**
     * Constructs a new `BasicContextTracingAspect` with the specified logging enabled flag.
     *
     * @param enabled `true` to enable logging, `false` to disable logging.
     */
    public TracingAspect(boolean enabled) {
        this.loggingEnabled = enabled;
    }

    /**
     * Enables or disables logging in this aspect.
     *
     * @param enabled `true` to enable logging, `false` to disable logging.
     */
    public void enableLogging(boolean enabled) {
        this.loggingEnabled = enabled;
    }

    /**
     * Gets the current logging enabled status.
     *
     * @return `true` if logging is enabled, `false` otherwise.
     */
    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }


    /**
     * Checks if tracing is currently enabled via the JMX MBean. Note
     * this can be embedded in pointcuts in the following way:
     *  <pre>
     *      pointcut enabledPointcut() : if(isEnabled()) && execution(* *(..));
     *  </pre>
     *
     * @return `true` if tracing is enabled, `false` otherwise.
     */
    private boolean isEnabled() {
        return traceControl.isEnabled();
    }

    /**
     * Enables or disables tracing.
     *
     * @param enable `true` to enable tracing, `false` to disable.
     */
    public void enable(boolean enable) {
        traceControl.enable(enable);
    }

    /**
     * Abstract pointcut to be defined in concrete subaspects.
     * This pointcut determines which methods will be traced.
     */
    public abstract pointcut trace();

    /**
     * AfterThrowing advice for handling exceptions.
     * Adds the collection context to the MDC/NDC context for the exception event to the existing
     * context already added from the method entry from the {@link #around} method and also conditionally logs the information if
     * logging is enabled for this class.
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
    after() throwing(Throwable throwable) : trace() {
        // note the helper object is AutoCloseable which will clear up the NDC/MDC appropriately.
        // using this ensures if an exception is thrown it is still cleaned up.
        try (helper) {
            helper.withException(throwable.getClass().getCanonicalName());
            if (loggingEnabled) {
                LOGGER.error(AFTER, throwable);
            }
        }
    }

    /**
     * Logs a "BEFORE" message using the associated logger,
     * but only if logging is currently enabled.
     */
    protected void logBefore() {
        if (loggingEnabled) {
            LOGGER.info(BEFORE);
        }
    }

    /**
     * Logs an "AFTER" message using the associated logger,
     * but only if logging is currently enabled.
     */
    protected void logAfter() {
        if (loggingEnabled) {
            LOGGER.info(AFTER);
        }
    }

    protected String objectToString(Object obj) {
        return obj == null ? "null" : obj.toString();
    }

}
