package org.automon.aspects.tracing.spring;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.aspects.tracing.BaseContextAspect;
import org.automon.utils.Utils;

/**
 * <p>Aspect for managing request IDs in the SLF4J MDC (Mapped Diagnostic Context). This aspect works for
 * both Spring and non-Spring applications.</p>
 * <p>This aspect adds a unique request ID to the MDC at the beginning of a request and removes it at the end.</p>
 *
 * <p>This aspect can be dynamically enabled or disabled at runtime using the {@link org.automon.jmx.EnableMXBean}.</p>
 * <p>AspectJ aspects are singletons by default.</p>
 */
@Aspect // Indicates that this class is a Spring AOP aspect
public abstract class RequestIdAspect extends BaseContextAspect {

    /**
     * The purpose of this aspect for JMX registration (default: "request_id_spring").
     */
    static final String PURPOSE = "request_id_spring";

    /**
     * Constructs a new `RequestIdAspect`. It checks the `automon.properties` file to determine if the aspect
     * should be enabled. If the property is not found, it defaults to enabled.
     */
    public RequestIdAspect() {
        initialize(PURPOSE, Utils.shouldEnable(getClass().getName()));
    }

    /**
     * Constructs a new `RequestIdAspect` and sets the initial enabled state.
     *
     * @param enable The initial enable state for the aspect.
     */
    public RequestIdAspect(boolean enable) {
        initialize(PURPOSE, enable);
    }

    /**
     * Abstract pointcut that defines the points in your application where the request ID should be added and removed.
     * This should be implemented by subclasses to target the entry and exit points of requests.
     * <p>
     * **Examples:**
     * </p>
     *
     * <pre>
     * &#64;Pointcut("execution(* com.stevesouza.MyLoggerClassBasic.main(..))")
     * </pre>
     */
    @Pointcut
    public abstract void select();

    /**
     * Before advice: Executed before the join point defined by {@link #select()}.
     * Adds a unique request ID to the MDC if the aspect is enabled.
     */
    @Before("select()")
    public void beforeAdvice() {
        if (isEnabled()) {
            helper.withRequestId();
        }
    }

    /**
     * After advice: Executed after the join point defined by {@link #select()}.
     * Removes the request ID from the MDC if the aspect is enabled.
     */
    @After("select()")
    public void afterAdvice() {
        if (isEnabled()) {
            helper.removeRequestId();
        }
    }
}