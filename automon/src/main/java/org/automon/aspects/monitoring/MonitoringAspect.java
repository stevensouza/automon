package org.automon.aspects.monitoring;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.utils.Utils;

/**
 * <p>This aspect should contain pointcut language that is compatible with Spring.   Use this as your Base class if you use Spring.
 * * It will also work with any AspectJ program, but will be more limited in how expressive the pointcuts can be.</p>
 *
 * <p>Note a developer should implement and provide pointcuts that you want to monitor by implementing {@link BaseMonitoringAspect}#user_monitor()
 * and {@link BaseMonitoringAspect}#user_exceptions()</p>
 */

@Aspect
public abstract class MonitoringAspect extends BaseMonitoringAspect {
    static final String PURPOSE = "monitor_spring";

    public MonitoringAspect() {
        initialize(PURPOSE, Utils.shouldEnable(getClass().getName()));
    }

    public MonitoringAspect(boolean enable) {
        initialize(PURPOSE, enable);
    }

    @Pointcut
    public abstract void select();

    /**
     * _monitor() advice - Wraps the given pointcut and calls the appropriate {@link org.automon.implementations.OpenMon} method
     * at the beginning and end of the method call.
     *
     * @param joinPoint
     * @return The advised methods value or void.
     * @throws Throwable If the method throws a {@link java.lang.Throwable} the advice will rethrow it.
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
     * @param pjp
     * @param throwable
     */
    @AfterThrowing(pointcut = "select()", throwing = "throwable")
    public void afterThrowingAdvice(JoinPoint pjp, Throwable throwable) {
        if (isEnabled()) {
            getOpenMon().exception(pjp, throwable);
        }
    }


}
