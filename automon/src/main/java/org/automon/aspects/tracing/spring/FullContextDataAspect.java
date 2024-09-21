package org.automon.aspects.tracing.spring;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.aspects.tracing.BaseContextAspect;
import org.automon.utils.Utils;

/**
 * <p>Spring aspect for managing contextual data in the SLF4J MDC (Mapped Diagnostic Context) and NDC (Nested Diagnostic Context).
 * This aspect utilizes a `LogTracingHelper` to add relevant contextual information (potentially including request IDs)
 * to the MDC and/or NDC at the beginning of a request/operation, and removes it upon completion.
 * </p>
 *
 * <p>If you would like to log on method entry (BEFORE) and exit (AFTER) then use 'FullContextTracingAspect'.
 * It can enable/disable entry/exit logging.  If it is disabled it acts similarly
 * to this class except it adds 'executionTime' and the methods 'returnValue' to the MDC too.
 * </p>
 *
 * <p>Note this object can be controlled (enabled/disabled at runtime) by using {@link org.automon.jmx.EnableMXBean}</p>
 *
 * <p>Note by default AspectJ aspects are singletons.</p>
 */
@Aspect
public abstract class FullContextDataAspect extends BaseContextAspect {

    static final String PURPOSE = "trace_nolog_full_context_spring";

    /**
     * Constructs a new `FullContextDataAspect` by looking in  automon properties and if it doesn't exist in there
     * default to enable.
     */
    public FullContextDataAspect() {
        initialize(PURPOSE, Utils.shouldEnable(getClass().getName()));
    }

    /**
     * Constructs a new `FullContextDataAspect` and sets the initial enabled state
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
     * <pre>
     *      pointcut select() : execution(* com.stevesouza.MyLoggerClassBasic.main(..));
     *  </pre>
     *
     * <pre>
     * pointcut select() : enabled() && execution(* com.example..*.*(..));
     * </pre>
     * <p>
     * Alternatively the following equivalent approach could be used:
     * <pre>
     *  pointcut select() : if(isEnabled()) && execution(* com.example..*.*(..));
     * </pre>
     */
    @Pointcut
    public abstract void select();

    /**
     * Advice to full context to the MDC before the request is processed. Note it doesn't have staticEnclosingPart.
     */
    @Before("select()")
    public void beforeAdvice(JoinPoint joinPoint) {
        if (isEnabled()) {
            JoinPoint.StaticPart thisJoinPointStaticPart = joinPoint.getStaticPart();
            helper.withKind(thisJoinPointStaticPart).
                    withParameters(joinPoint).
                    withSignature(thisJoinPointStaticPart).
                    withTarget(joinPoint).
                    withThis(joinPoint);
        }
    }

    /**
     * Advice to remove the full context from the MDC after the request is processed.
     */
    @After("select()")
    public void afterAdvice() {
        if (isEnabled()) {
            helper.removeFullContext();
        }
    }

}