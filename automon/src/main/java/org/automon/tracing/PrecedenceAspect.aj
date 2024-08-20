package org.automon.tracing;

/**
 * AspectJ aspect that declares precedence for other aspects in the system.
 * This aspect ensures that the RequestIdAspect is applied before any other aspects.
 */
public aspect PrecedenceAspect {

    /**
     * Declares the order in which aspects are applied.
     * RequestIdAspect is given highest precedence, followed by all other aspects.
     * <p>
     * This ensures that the RequestIdAspect is always applied first, which is typically
     * desirable when tracking requests across multiple aspects or components.
     */
    declare precedence: RequestIdAspect, *; // RequestIdAspect first, then all other aspects
}