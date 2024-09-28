package org.automon.implementations;

import org.aspectj.lang.JoinPoint;

/**
 * <p>This interface defines the core methods for interacting with monitoring systems in Automon.</p>
 * <p>Implementations of this interface can be automatically invoked by AspectJ pointcuts to monitor your system.
 * Typical use cases include timing methods and counting exceptions, but implementations can also log method calls and exceptions or perform other custom monitoring actions.
 * The implementation should ideally be stateless or, at a minimum, thread-safe.</p>
 *
 * @param <T> The type of context object used for monitoring (e.g., a timer or other stateful object).
 */
public interface OpenMon<T> {

    /**
     * Label used for tracking exceptions.
     */
    String EXCEPTION_LABEL = "org.automon.Exceptions";

    /**
     * <p>Called by AspectJ at the beginning of an advised method execution (around advice).</p>
     * <p>This method is typically used to start a timer or initiate monitoring for the method.
     * However, it can be used for any action, such as logging the method's start.</p>
     *
     * @param jp The static part of the JoinPoint representing the intercepted method.
     * @return A context object that will be passed to the `stop` method. This is typically a timer
     * or another object to track the monitoring state.
     */
    T start(JoinPoint.StaticPart jp);

    /**
     * <p>Called by AspectJ after the successful completion of an advised method execution (around advice).</p>
     * <p>This method is typically used to stop a timer or finalize monitoring for the method.
     * It can also be used for any other action, such as logging the method's completion.</p>
     *
     * @param context The context object returned by the `start` method.
     */
    void stop(T context);

    /**
     * <p>Called by AspectJ after an advised method execution completes with an exception (around advice).</p>
     * <p>This method is typically used to stop a timer, record the exception, and finalize monitoring for the method.
     * Ensure that exceptions are not double-counted if you also handle them in the `exception` method.</p>
     *
     * @param context   The context object returned by the `start` method.
     * @param throwable The exception that was thrown.
     */
    void stop(T context, Throwable throwable);

    /**
     * <p>Called when an exception occurs within the monitored code.</p>
     * <p>Implementations typically use this method to count or log exceptions.</p>
     *
     * @param jp        The `JoinPoint` where the exception was thrown. This provides access to
     *                  argument names and values.
     * @param throwable The thrown exception.
     */
    void exception(JoinPoint jp, Throwable throwable);
}