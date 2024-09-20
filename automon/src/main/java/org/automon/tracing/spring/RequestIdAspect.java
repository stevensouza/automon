package org.automon.tracing.spring;


import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.tracing.BaseContextAspect;
import org.automon.jmx.AspectJmxController;
import org.automon.utils.Utils;

/**
 * Spring AOP aspect for managing request IDs in the SLF4J MDC (Mapped Diagnostic Context).
 * This aspect adds a unique request ID to the MDC at the beginning of a request and removes it at the end.
 *
 * <p>
 * Note it is best to use {@link org.automon.tracing.aspectj.RequestIdAspect} when using aspectj directly as it allows for more pointcuts to be specified
 * such as non-public methods, variable assignment, calls and more that are not allowed in spring.  Use the Spring
 * version of this class when working with Spring AOP.  Note this class
 * can be used in spring apps if the full power of aspectj is required.  See Automon documentation for examples.
 * </p>
 *
 * <p>Note this object can be controlled (enabled/disabled at runtime) by using {@link AspectJmxController}</p>
 * <p>Note by default AspectJ aspects are singletons.</p>
 */
@Aspect
public abstract class RequestIdAspect extends BaseContextAspect {

    static final String PURPOSE = "request_id_spring";

    /**
     * Constructs a new `RequestIdAspect` by looking in  automon properties and if it doesn't exist in there
     * default to enable.
     */
    public RequestIdAspect() {
        initialize(PURPOSE, Utils.shouldEnable(getClass().getName()));
    }

    /**
     * Constructs a new `RequestIdAspect` and sets the initial enable state of the associated
     * `AspectJmxController`.
     *
     * @param enable The initial enable state for tracing.
     */
    public RequestIdAspect(boolean enable) {
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
    @Pointcut
    public abstract void select();

    /**
     * Advice to add a request ID to the MDC before the request is processed.
     */
    @Before("select()")
    public void beforeAdvice() {
        if (isEnabled()) {
            helper.withRequestId();
        }
    }

    /**
     * Advice to remove the request ID from the MDC after the request is processed.
     */
    @After("select()")
    public void afterAdvice() {
        if (isEnabled()) {
            helper.removeRequestId();
        }
    }

}
