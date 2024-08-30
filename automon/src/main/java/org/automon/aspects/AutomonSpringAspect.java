package org.automon.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

/**
 * <p>This aspect should contain pointcut language that is compatible with Spring.   Use this as your Base class if you use Spring.
 * * It will also work with any AspectJ program, but will be more limited in how expressive the pointcuts can be.</p>
 *
 * <p>Note a developer should implement and provide pointcuts that you want to monitor by implementing {@link org.automon.aspects.AutomonAspectBase}#user_monitor()
 * and {@link org.automon.aspects.AutomonAspectBase}#user_exceptions()</p>
 */

@Component
public class AutomonSpringAspect extends AutomonAspectBase {

    /**
     * _monitor() advice - Wraps the given pointcut and calls the appropriate {@link org.automon.implementations.OpenMon} method
     * at the beginning and end of the method call.
     *
     * @param pjp
     * @return The advised methods value or void.
     * @throws Throwable If the method throws a {@link java.lang.Throwable} the advice will rethrow it.
     */


    public Object monitor(ProceedingJoinPoint pjp) throws Throwable {
        // Note: context is typically a Timer/Monitor object returned by the monitoring implementation (Jamon, JavaSimon, Metrics,...)
        // though to this advice it is simply an object and the advice doesn't care what the intent of the context/object is.
        Object context = getOpenMon().start(pjp.getStaticPart());
        try {
            Object retVal = pjp.proceed();
            getOpenMon().stop(context);
            return retVal;
        } catch (Throwable throwable) {
            getOpenMon().stop(context, throwable);
            throw throwable;
        }
    }

    /**
     * exceptions() advice - Takes action on any Exception thrown.  It typically Tracks/Counts any exceptions thrown by the pointcut.
     * Note arguments are passed on to {@link org.automon.implementations.OpenMon#exception(org.aspectj.lang.JoinPoint, Throwable)}
     *
     * @param pjp
     * @param exceptionArg
     */
    public void throwing(JoinPoint pjp, Throwable exceptionArg) {
        getOpenMon().exception(pjp, exceptionArg);
    }


}
