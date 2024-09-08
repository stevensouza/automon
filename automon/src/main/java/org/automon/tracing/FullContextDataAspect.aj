package org.automon.tracing;


import org.automon.tracing.jmx.AspectJmxController;
import org.automon.utils.AutomonPropertiesLoader;
import org.automon.utils.LogTracingHelper;
import org.automon.utils.Utils;

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
public privileged abstract aspect FullContextDataAspect extends BaseContextAspect {
    static final String PURPOSE = "trace_nolog_full_context";

    /**
     *  Constructs a new `FullContextDataAspect` by looking in  automon properties and if it doesn't exist in there
     *  default to enable.
     */
    public FullContextDataAspect() {
        this(Utils.shouldEnable(FullContextDataAspect.class.getName()));
    }

    /**
     * Constructs a new `FullContextDataAspect` and sets the initial enable state of the associated
     * `AspectJmxController`.
     *
     * @param enable The initial enable state for tracing.
     */
    public FullContextDataAspect(boolean enable) {
        initialize(PURPOSE, enable);
    }

    /**
     * Pointcut that defines where the request ID should be added and removed.
     * This should be implemented to target the entry and exit points of requests in your application.
     * <p>
     * <p>**Examples:**</p>
     *
     *  <pre>
     *      pointcut select() : execution(* com.stevesouza.MyLoggerClassBasic.main(..));
     *  </pre>
     *
     * <pre>
     * pointcut select() : enabled() && execution(* com.example..*.*(..));
     * </pre>
     *
     * Alternatively the following equivalent approach could be used:
     * <pre>
     *  pointcut select() : if(isEnabled()) && execution(* com.example..*.*(..));
     * </pre>
     *
     */
    public abstract pointcut select();

    /**
     * A pointcut that matches if tracing is enabled.
     * <p>
     * This pointcut can be used in conjunction with other pointcuts to conditionally apply advice
     * only when tracing is enabled.
     *
     * <p>**Examples:**</p>
     *
     * <pre>
     * pointcut select() : enabled() && execution(* com.example..*.*(..));
     * </pre>
     *
     * Alternatively the following equivalent approach could be used:
     * <pre>
     *  pointcut select() : if(isEnabled()) && execution(* com.example..*.*(..));
     * </pre>
     */
    public pointcut enabled() : if(isEnabled());

    /**
     * Before advice: Executed before the join point defined by {@link #select()}.
     * Utilizes the `LogTracingHelper` to add contextual data (potentially including a request ID)
     * to the MDC and/or NDC.
     */
    before(): select() {
        helper.withFullContext(thisJoinPoint, thisJoinPointStaticPart, thisEnclosingJoinPointStaticPart);
    }

    /**
     * After advice: Executed after the join point defined by {@link #select()}.
     * Utilizes the `LogTracingHelper` to remove the previously added contextual data from the
     * MDC and/or NDC.
     */
    after(): select() {
        helper.removeFullContext();
    }

}