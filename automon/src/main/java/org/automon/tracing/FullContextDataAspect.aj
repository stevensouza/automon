package org.automon.tracing;


import org.automon.tracing.jmx.AspectJmxController;
import org.automon.utils.LogTracingHelper;

/**
 * <p>AspectJ aspect for managing contextual data in the SLF4J MDC (Mapped Diagnostic Context) and NDC (Nested Diagnostic Context).
 * This aspect utilizes a `LogTracingHelper` to add relevant contextual information (potentially including request IDs)
 * to the MDC and/or NDC at the beginning of a request/operation, and removes it upon completion.
 * </p>
 *
 *  <p>If you would like to log on method entry (BEFORE) and exit (AFTER) then use {@link FullContextTracingAspect}.
 *  {@link FullContextTracingAspect} can enable/disable entry/exit logging.  If it is disabled it acts similarly
 *  to this class except it adds 'executionTime' and the methods 'returnValue' to the MDC too.
 *  </p>
 *
 *  <p>Note this object can be controlled (enabled/disabled at runtime) by using {@link AspectJmxController}</p>
 *
 *  <p>Note by default AspectJ aspects are singletons.</p>
 */
public abstract aspect FullContextDataAspect {
    private static final LogTracingHelper helper = LogTracingHelper.getInstance();

    /**
     * The JMX controller responsible for managing tracing aspects.
     * This controller is created as a singleton and provides access to
     * tracing-related functionalities (such as enable/disable) through JMX.
     */
    private static final AspectJmxController jmxController = new AspectJmxController();

    /**
     * Constructs a new `FullContextDataAspect` enabled by default.
     */
    public FullContextDataAspect() {
        this(true); // Call the parameterized constructor with default 'true' value
    }

    /**
     * Constructs a new `FullContextDataAspect` and sets the initial enable state of the associated
     * `AspectJmxController`.
     *
     * @param enable The initial enable state for tracing.
     */
    public FullContextDataAspect(boolean enable) {
        jmxController.enable(enable);
    }

    /**
     * Abstract pointcut that defines the join points (method executions, etc.) where contextual data
     * should be managed (added and removed). This needs to be implemented in concrete subclasses
     * to target specific entry and exit points within your application.
     * <p>
     * <p>**Examples:**</p>
     *
     *  <pre>
     *      pointcut trace() : execution(* com.stevesouza.MyLoggerClassBasic.main(..));
     *  </pre>
     *
     * <pre>
     * pointcut trace() : enabled() && execution(* com.example..*.*(..));
     * </pre>
     *
     * Alternatively the following equivalent approach could be used:
     * <pre>
     *  pointcut trace() : if(isEnabled()) && execution(* com.example..*.*(..));
     * </pre>
     *
     */
    public abstract pointcut trace();

    /**
     * A pointcut that matches if tracing is enabled.
     * <p>
     * This pointcut can be used in conjunction with other pointcuts to conditionally apply advice
     * only when tracing is enabled.
     *
     * <p>**Examples:**</p>
     *
     * <pre>
     * pointcut trace() : enabled() && execution(* com.example..*.*(..));
     * </pre>
     *
     * Alternatively the following equivalent approach could be used:
     * <pre>
     *  pointcut trace() : if(isEnabled()) && execution(* com.example..*.*(..));
     * </pre>
     */
    public pointcut enabled() : if(isEnabled());

    /**
     * Before advice: Executed before the join point defined by {@link #trace()}.
     * Utilizes the `LogTracingHelper` to add contextual data (potentially including a request ID)
     * to the MDC and/or NDC.
     */
    before(): trace() {
        helper.withFullContext(thisJoinPoint, thisJoinPointStaticPart, thisEnclosingJoinPointStaticPart);
    }

    /**
     * After advice: Executed after the join point defined by {@link #trace()}.
     * Utilizes the `LogTracingHelper` to remove the previously added contextual data from the
     * MDC and/or NDC.
     */
    after(): trace() {
        helper.removeFullContext();
    }


    /**
     * Retrieves the singleton instance of the {@link AspectJmxController}.
     *
     * @return The JMX controller for tracing aspects.
     */
    protected static AspectJmxController getJmxController() {
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
}