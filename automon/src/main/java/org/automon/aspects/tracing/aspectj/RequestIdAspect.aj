package org.automon.aspects.tracing.aspectj;

import org.automon.aspects.tracing.BaseContextAspect;
import org.automon.utils.Utils;

/**
 * AspectJ aspect for managing request IDs in the SLF4J MDC (Mapped Diagnostic Context).
 * This aspect adds a unique request ID to the MDC at the beginning of a request and removes it at the end.
 * <p>
 * It's recommended to use `RequestIdAspect` when working directly with AspectJ as it allows for defining
 * pointcuts on non-public methods, variable assignments, calls, etc., which are not possible with Spring AOP.
 * However, the Spring version of this class (`org.automon.aspects.tracing.spring.RequestIdAspect`) is preferred
 * when using Spring AOP. This class can still be used in Spring applications if the full power of AspectJ is required.
 * Refer to the Automon documentation for examples.
 * </p>
 *
 * <p>Note: This aspect can be controlled (enabled/disabled at runtime) using {@link org.automon.jmx.EnableMXBean}.</p>
 * <p>Note: By default, AspectJ aspects are singletons.</p>
 */
public privileged abstract aspect RequestIdAspect extends BaseContextAspect {

    /**
     * The purpose of this aspect for JMX registration.
     */
    static final String PURPOSE = "request_id_native";

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
     * Pointcut that defines where the request ID should be added and removed.
     * This should be implemented to target the entry and exit points of requests in your application.
     * <p>
     * **Examples:**
     * </p>
     *
     * <pre>
     * pointcut select() : execution(* com.stevesouza.MyLoggerClassBasic.main(..));
     * </pre>
     *
     * <pre>
     * pointcut select() : execution(* com.example..*.*(..));
     * </pre>
     */
    public abstract pointcut select();

    /**
     * Before advice: Executed before the join point defined by {@link #select()}.
     * Adds a unique request ID (UUID) to the MDC using the `LogTracingHelper`.
     */
    before(): select() {
        if (isEnabled()) {
            helper.withRequestId();
        }
    }

    /**
     * After advice: Executed after the join point defined by {@link #select()}.
     * Removes the request ID from the MDC using the `LogTracingHelper`.
     */
    after(): select() {
        if (isEnabled()) {
            helper.removeRequestId();
        }
    }
}
