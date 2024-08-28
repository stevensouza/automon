package org.automon.tracing;


import org.automon.tracing.jmx.AspectControl;
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
 */
public abstract aspect FullContextDataAspect extends AspectControl {
    private static final LogTracingHelper helper = LogTracingHelper.getInstance();

    /**
     * Abstract pointcut that defines the join points (method executions, etc.) where contextual data
     * should be managed (added and removed). This needs to be implemented in concrete subclasses
     * to target specific entry and exit points within your application.
     *
     * Example implementation (replace with your actual target):
     *   pointcut fullContextData() : execution(* com.stevesouza.MyLoggerClassBasic.main(..));
     */
    public abstract pointcut fullContextData();

    /**
     * Before advice: Executed before the join point defined by `fullContextData()`.
     * Utilizes the `LogTracingHelper` to add contextual data (potentially including a request ID)
     * to the MDC and/or NDC.
     */
    before(): fullContextData() {
        helper.withFullContext(thisJoinPoint, thisJoinPointStaticPart, thisEnclosingJoinPointStaticPart);
    }

    /**
     * After advice: Executed after the join point defined by `fullContextData()`.
     * Utilizes the `LogTracingHelper` to remove the previously added contextual data from the
     * MDC and/or NDC.
     */
    after(): fullContextData() {
        helper.removeFullContext();
    }
}