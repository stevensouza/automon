package org.automon.implementations;

import com.newrelic.api.agent.NewRelic;
import org.aspectj.lang.JoinPoint;

/**
 * This class provides an `OpenMon` implementation that utilizes New Relic for monitoring method executions and exceptions.
 * It offers functionalities for starting and stopping timers to measure method execution time,
 * and for tracking exceptions using New Relic's error reporting capabilities.
 */
public class NewRelicImp extends OpenMonBase<TimerContext> {

    /**
     * HACK ALERT: This variable is a workaround to force a `NoClassDefFoundError` to be thrown during the creation
     * of this `NewRelicImp` object if the New Relic agent is not present in the classpath.
     * <p>
     * For some reason, when New Relic jars are missing, the `NoClassDefFoundError` is not thrown immediately upon
     * creating a `NewRelic` instance, but rather when the `trackException` method is called later.
     * This behavior differs from other `OpenMon` implementations, where the exception is detected earlier by the
     * `OpenMonFactory` and a `NullImp` is returned, allowing the program to continue gracefully.
     * <p>
     * By creating a `NewRelic` instance here, we force the exception to occur at object creation time, enabling
     * the `OpenMonFactory` to handle it and fall back to the `NullImp` behavior, ensuring the program's stability.
     */
    private final NewRelic hackToCauseNoClassDefFoundErrorOnCreation = new NewRelic();

    /**
     * Starts monitoring a method execution by creating a `TimerContext` for the given join point.
     *
     * @param jp The join point representing the intercepted method.
     * @return The created `TimerContext` used to track the execution time.
     */
    @Override
    public TimerContext start(JoinPoint.StaticPart jp) {
        return new TimerContext(jp);
    }

    /**
     * Stops the timer and records the response time metric using New Relic.
     *
     * @param context The `TimerContext` containing the join point and start time information.
     */
    @Override
    public void stop(TimerContext context) {
        NewRelic.recordResponseTimeMetric("Custom/" + context.getJoinPoint().toString(), context.stop());
    }

    /**
     * Tracks an exception by reporting it to New Relic.
     *
     * @param jp        The join point where the exception occurred.
     * @param throwable The exception that was thrown.
     */
    @Override
    protected void trackException(JoinPoint jp, Throwable throwable) {
        NewRelic.noticeError(throwable);
    }
}