package org.automon.aspects.tracing.aspectj;

/**
 * AspectJ aspect that declares precedence for other aspects in the system.
 * This aspect ensures that the `RequestIdAspect` is applied before any other aspects.
 */
public aspect PrecedenceAspect {

    /**
     * Declares the order in which aspects are applied.
     * <p>
     * `RequestIdAspect` is given the highest precedence, followed by all other aspects ('*').
     * This ensures that the `RequestIdAspect` is always applied first, allowing it to set the request ID
     * in the MDC before any other aspects potentially utilize it for logging or tracing.
     * </p>
     */
    declare precedence: RequestIdAspect, *; // RequestIdAspect first, then all other aspects
}