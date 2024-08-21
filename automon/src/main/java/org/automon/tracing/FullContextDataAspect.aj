package org.automon.tracing;


import org.automon.utils.LogTracingHelper;

/**
 * AspectJ aspect for managing contextual data in the SLF4J MDC (Mapped Diagnostic Context) and NDC (Nested Diagnostic Context).
 * This aspect utilizes a `LogTracingHelper` to add relevant contextual information (potentially including request IDs)
 * to the MDC and/or NDC at the beginning of a request/operation, and removes it upon completion.
 */
public abstract aspect FullContextDataAspect {
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