package org.automon.aspects.monitoring;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.utils.Utils;

/**
 * This aspect is designed for monitoring method executions and exceptions in both Spring and non-spring applications.
 * It extends `BaseMonitoringAspect` to provide pointcut language and integrates with
 * `OpenMon` implementations for collecting performance metrics.
 *
 * <p>Developers should inherit from this class and implement the `select()` pointcut to define the specific methods or classes to monitor.</p>
 */
@Aspect // Indicates that this class is an AspectJ aspect
public abstract class MonitoringAspect extends BaseMonitoringAspect {

    /**
     * The purpose associated with this JMX registration (default: "monitor_spring").
     */
    static final String PURPOSE = "monitor_spring";

    /**
     * Constructs a new `MonitoringAspect` with monitoring enabled based on configuration properties.
     */
    public MonitoringAspect() {
        initialize(PURPOSE, Utils.shouldEnable(getClass().getName()));
    }

    /**
     * Constructs a new `MonitoringAspect` with the specified enabled state.
     *
     * @param enable Whether monitoring is initially enabled.
     */
    public MonitoringAspect(boolean enable) {
        initialize(PURPOSE, enable);
    }

    /**
     * Abstract pointcut to be implemented by subclasses to define the methods or classes to monitor.
     */
    @Pointcut
    public abstract void select();

    /**
     * Around advice for monitoring method execution.
     * <p>
     * This advice wraps the execution of the selected methods and performs the following:
     * <ol>
     *     <li>Starts a timer or monitor using the appropriate `OpenMon` implementation.</li>
     *     <li>Proceeds with the method execution.</li>
     *     <li>Stops the timer/monitor if the method completes successfully.</li>
     *     <li>Stops the timer/monitor and records an exception if the method throws an exception.</li>
     *     <li>Rethrows any exception thrown by the method.</li>
     * </ol>
     *
     * @param joinPoint The `ProceedingJoinPoint` representing the intercepted method call.
     * @return The return value of the advised method.
     * @throws Throwable If the advised method throws an exception.
     */
    @Around("select()")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        // Note: context is typically a Timer/Monitor object returned by the monitoring implementation (Jamon, JavaSimon, Metrics,...)
        // though to this advice it is simply an object and the advice doesn't care what the intent of the context/object is.
        if (isEnabled()) {
            Object context = getOpenMon().start(joinPoint.getStaticPart());
            try {
                Object retVal = joinPoint.proceed();
                getOpenMon().stop(context);
                return retVal;
            } catch (Throwable throwable) {
                getOpenMon().stop(context, throwable);
                throw throwable;
            }
        } else {
            return joinPoint.proceed();
        }
    }

    /**
     * exceptions() advice - Takes action on any Exception thrown.  It typically Tracks/Counts any exceptions thrown by the pointcut.
     * Note arguments are passed on to {@link org.automon.implementations.OpenMon#exception(org.aspectj.lang.JoinPoint, Throwable)}
     *
     * @param joinPoint The JoinPoint representing the intercepted method call where the exception was thrown
     * @param throwable The thrown exception
     */
    @AfterThrowing(pointcut = "select()", throwing = "throwable")
    public void afterThrowingAdvice(JoinPoint joinPoint, Throwable throwable) {
        if (isEnabled()) {
            getOpenMon().exception(joinPoint, throwable);
        }
    }
}