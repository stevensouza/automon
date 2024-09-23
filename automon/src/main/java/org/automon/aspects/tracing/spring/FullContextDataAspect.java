package org.automon.aspects.tracing.spring;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.aspects.tracing.BaseContextAspect;
import org.automon.utils.Utils;

/**
 * <p>AOP aspect for managing contextual data in the SLF4J MDC (Mapped Diagnostic Context) and NDC (Nested Diagnostic Context).
 * Can be used by spring and even non-spring applications.</p>
 *
 * <p>This aspect adds relevant contextual information to the MDC and/or NDC at the beginning
 * of a request/operation, and removes it upon completion.</p>
 *
 * <p>If you would like to log on method entry (BEFORE) and exit (AFTER), then use `FullContextTracingAspect`.
 * `FullContextTracingAspect` can enable/disable entry/exit logging. If it is disabled, it acts similarly
 * to this class except it adds 'executionTime' and the methods 'returnValue' to the MDC too.</p>
 *
 * <p>Note: This aspect can be controlled (enabled/disabled at runtime) by using {@link org.automon.jmx.EnableMXBean}.</p>
 *
 * <p>Note: By default, aspects are singletons.</p>
 */
@Aspect
public abstract class FullContextDataAspect extends BaseContextAspect {

    /**
     * The purpose of this aspect for JMX registration.
     */
    static final String PURPOSE = "trace_nolog_full_context_spring";

    /**
     * Constructs a new `FullContextDataAspect`. It checks the `automon.properties` file to determine if the aspect
     * should be enabled. If the property is not found, it defaults to enabled.
     */
    public FullContextDataAspect() {
        initialize(PURPOSE, Utils.shouldEnable(getClass().getName()));
    }

    /**
     * Constructs a new `FullContextDataAspect` and sets the initial enabled state.
     *
     * @param enable The initial enable state for tracing.
     */
    public FullContextDataAspect(boolean enable) {
        initialize(PURPOSE, enable);
    }

    /**
     * Pointcut that defines where the contextual data should be added and removed.
     * This should be implemented to target the entry and exit points of requests in your application.
     * <p>
     * **Examples:**
     * </p>
     *
     * <pre>
     * &#64;Pointcut("execution(* com.stevesouza.MyLoggerClassBasic.main(..))")
     * </pre>
     *
     * <pre>
     * &#64;Pointcut("execution(* com.example..*.*(..))")
     * </pre>
     */
    @Pointcut
    public abstract void select();

    /**
     * Before advice: Executed before the join point defined by {@link #select()}.
     * <p>
     * Utilizes the `LogTracingHelper` to add contextual data
     * to the MDC. Note that it doesn't include `staticEnclosingPart` as it's not available in Spring AOP.
     * </p>
     *
     * @param joinPoint The JoinPoint representing the intercepted method call
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
     * After advice: Executed after the join point defined by {@link #select()}.
     * Utilizes the `LogTracingHelper` to remove the previously added contextual data from the MDC.
     */
    @After("select()")
    public void afterAdvice() {
        if (isEnabled()) {
            helper.removeFullContext();
        }
    }
}