package org.automon.implementations;

import org.aspectj.lang.JoinPoint;

/**
 * <p>This class is a no-operation (noop) implementation of the `OpenMon` interface.</p>
 * <p>It is used when Automon monitoring is disabled, providing placeholder methods that do nothing.</p>
 */
public final class NullImp implements OpenMon<Object> {

    /**
     * A constant representing a no-operation object.
     */
    private static final Object NOOP = new Object();

    /**
     * Starts monitoring (noop).
     *
     * @param label The join point label (ignored in this implementation).
     * @return A no-operation object.
     */
    @Override
    public Object start(JoinPoint.StaticPart label) {
        return NOOP;
    }

    /**
     * Stops monitoring (noop).
     *
     * @param context The monitoring context (ignored in this implementation).
     */
    @Override
    public void stop(Object context) {
        // No operation
    }

    /**
     * Stops monitoring and handles exceptions (noop).
     *
     * @param context   The monitoring context (ignored in this implementation).
     * @param throwable The exception that occurred (ignored in this implementation).
     */
    @Override
    public void stop(Object context, Throwable throwable) {
        // No operation
    }

    /**
     * Handles an exception (noop).
     *
     * @param jp        The join point where the exception occurred (ignored in this implementation).
     * @param throwable The exception that was thrown (ignored in this implementation).
     */
    @Override
    public void exception(JoinPoint jp, Throwable throwable) {
        // No operation
    }
}