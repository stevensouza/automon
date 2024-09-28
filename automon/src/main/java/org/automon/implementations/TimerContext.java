package org.automon.implementations;

import org.aspectj.lang.JoinPoint;

/**
 * This class extends `BasicTimer` to provide context information for monitoring join point executions, specifically for New Relic integration.
 * It stores the `JoinPoint.StaticPart` associated with the intercepted method, which is used to generate labels for New Relic metrics.
 */
public class TimerContext extends BasicTimer {

    /**
     * The static part of the JoinPoint representing the intercepted method.
     */
    private final JoinPoint.StaticPart jp;

    /**
     * Constructs a new `TimerContext` with the given `JoinPoint.StaticPart`.
     *
     * @param jp The static part of the JoinPoint representing the intercepted method.
     */
    public TimerContext(JoinPoint.StaticPart jp) {
        this.jp = jp;
    }

    /**
     * Retrieves the `JoinPoint.StaticPart` associated with the intercepted method.
     *
     * @return The `JoinPoint.StaticPart`.
     */
    public JoinPoint.StaticPart getJoinPoint() {
        return jp;
    }
}