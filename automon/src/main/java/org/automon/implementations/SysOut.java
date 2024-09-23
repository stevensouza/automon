package org.automon.implementations;

import org.aspectj.lang.JoinPoint;
import org.automon.utils.Utils;

/**
 * <p>Implementation of the `OpenMon` interface that logs method start, stop, and exception events to the standard output (`System.out`).</p>
 *
 * <p>This implementation is primarily used for understanding the basic workings of Automon.
 * </p>
 */
public final class SysOut implements OpenMon<TimerContext> {

    /**
     * Starts monitoring a method execution by printing a "start" message to `System.out`.
     *
     * @param jp The static part of the JoinPoint representing the intercepted method.
     * @return A `TimerContext` object to track the execution time.
     */
    @Override
    public TimerContext start(JoinPoint.StaticPart jp) {
        System.out.println("SysOut.start(..): " + Utils.getLabel(jp));
        return new TimerContext(jp);
    }

    /**
     * Stops monitoring a method execution and prints the elapsed time in milliseconds to `System.out`.
     *
     * @param context The `TimerContext` containing the join point and start time information.
     */
    @Override
    public void stop(TimerContext context) {
        System.out.println("SysOut.stop(..) ms.: " + context.stop());
    }

    /**
     * Stops monitoring a method execution, prints the elapsed time, and the exception information to `System.out`.
     *
     * @param context   The `TimerContext` containing the join point and start time information.
     * @param throwable The exception that occurred during the monitored execution.
     */
    @Override
    public void stop(TimerContext context, Throwable throwable) {
        System.out.println("SysOut.stop(..) ms.: " + context.stop() + " - Exception: " + throwable);
    }

    /**
     * Handles an exception by printing the join point and exception information to `System.out`.
     *
     * @param jp        The `JoinPoint` where the exception occurred.
     * @param throwable The exception that was thrown.
     */
    @Override
    public void exception(JoinPoint jp, Throwable throwable) {
        System.out.println("SysOut.exception(..): JoinPoint=" + jp + ", Exception=" + throwable);
    }
}