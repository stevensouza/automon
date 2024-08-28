package org.automon.tracing;


import org.automon.tracing.jmx.AspectControl;
import org.automon.tracing.jmx.AspectMBean;
import org.automon.utils.LogTracingHelper;

/**
 * AspectJ aspect for managing request IDs in the SLF4J MDC (Mapped Diagnostic Context).
 * This aspect adds a unique request ID to the MDC at the beginning of a request and removes it at the end.
 */
public abstract aspect RequestIdAspect extends AspectControl {
    private static final LogTracingHelper helper = LogTracingHelper.getInstance();

    /**
     * Pointcut that defines where the request ID should be added and removed.
     * This should be implemented to target the entry and exit points of requests in your application.
     *
     * Example: : execution(* com.stevesouzaMyLoggerClassBasic.main(..))
     */
    public abstract pointcut requestStart();

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

}
