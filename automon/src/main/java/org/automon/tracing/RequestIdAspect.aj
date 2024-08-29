package org.automon.tracing;


import org.automon.tracing.jmx.AspectJmxController;
import org.automon.utils.LogTracingHelper;

/**
 * AspectJ aspect for managing request IDs in the SLF4J MDC (Mapped Diagnostic Context).
 * This aspect adds a unique request ID to the MDC at the beginning of a request and removes it at the end.
 *
 *  <p>Note this object can be controlled (enabled/disabled at runtime) by using {@link AspectJmxController}</p>
 *  <p>Note by default AspectJ aspects are singletons.</p>
 */
public abstract aspect RequestIdAspect {
    private static final LogTracingHelper helper = LogTracingHelper.getInstance();

    /**
     * The JMX controller responsible for managing tracing aspects.
     * This controller is created as a singleton and provides access to
     * tracing-related functionalities (such as enable/disable) through JMX.
     */
    private static final AspectJmxController jmxController = new AspectJmxController();

    /**
     * Pointcut that defines where the request ID should be added and removed.
     * This should be implemented to target the entry and exit points of requests in your application.
     * <p>
     * <p>**Examples:**</p>
     *
     *  <pre>
     *      pointcut requestStart() : execution(* com.stevesouza.MyLoggerClassBasic.main(..));
     *  </pre>
     *
     * <pre>
     * pointcut requestStart() : enabled() && execution(* com.example..*.*(..));
     * </pre>
     *
     * Alternatively the following equivalent approach could be used:
     * <pre>
     *  pointcut requestStart() : if(isEnabled()) && execution(* com.example..*.*(..));
     * </pre>
     *
     */
    public abstract pointcut requestStart();

    /**
     * A pointcut that matches if tracing is enabled.
     * <p>
     * This pointcut can be used in conjunction with other pointcuts to conditionally apply advice
     * only when tracing is enabled.
     *
     * <p>**Examples:**</p>
     *
     * <pre>
     * pointcut requestStart() : enabled() && execution(* com.example..*.*(..));
     * </pre>
     *
     * Alternatively the following equivalent approach could be used:
     * <pre>
     *  pointcut requestStart() : if(isEnabled()) && execution(* com.example..*.*(..));
     * </pre>
     */
    public pointcut enabled() : if(isEnabled());

    /**
     * Advice to add a request ID to the MDC before the request is processed.
     */
    before(): requestStart() {
        helper.withRequestId();
    }

    /**
     * Advice to remove the request ID from the MDC after the request is processed.
     */
    after(): requestStart() {
        helper.removeRequestId();
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
