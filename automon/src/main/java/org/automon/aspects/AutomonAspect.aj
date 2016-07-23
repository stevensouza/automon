package org.automon.aspects;

import org.automon.implementations.NullImp;
import org.automon.implementations.OpenMon;
import org.automon.implementations.OpenMonFactory;
import org.automon.utils.AutomonPropertiesLoader;
import org.automon.utils.Utils;

import java.util.Properties;



/**
 * <p>Aspect that advises the {@link org.aspectj.lang.annotation.Around} and {@link org.aspectj.lang.annotation.AfterThrowing} annotations.
 * The appropriate methods on {@link org.automon.implementations.OpenMon} methods are called. The advice typically times methods
 * and counts any exceptions  thrown, however other behavior such as logging is also possible.</p>
 *
 * <p>Note a developer should implement and provide any methods for this class or {@link org.automon.aspects.AspectJBase} or
 * {@link org.automon.aspects.SpringBase}.</p>
 *
 * <p>Note: I used native aspect style instead of the @AspectJ style because @Around in @AspectJ style doesn't seem to allow for the
 * more performant use of the static part of the JoinPoint. The static part only seems to be available without first
 * creating the dynamic JoinPoint in native aspects. Native style aspects are more powerful and can later be extended by developers
 * with @AspectJ style, so it is probably the best option anyway.  </p>
 */
privileged public abstract aspect AutomonAspect extends AutomonAspectBase {
    /**
     * _monitor() advice - Wraps the given pointcut and calls the appropriate {@link org.automon.implementations.OpenMon} method
     * at the beginning and end of the method call.
     *
     * @return The advised methods value or void.
     * @throws Throwable If the method throws a {@link java.lang.Throwable} the advice will rethrow it.
     */
    Object around() throws Throwable : _monitor()  {
        // Note: context is typically a Timer/Monitor object returned by the monitoring implementation (Jamon, JavaSimon, Metrics,...)
        // though to this advice it is simply an object and the advice doesn't care what the intent of the context/object is.
        Object context = getOpenMon().start(thisJoinPointStaticPart);
        try {
            Object retVal = proceed();
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
     */
    after() throwing(Throwable throwable): exceptions() {
        getOpenMon().exception(thisJoinPoint, throwable);
    }

    /** pointcut that determines what is monitored for performance/time */
    public pointcut _monitor() : user_monitor() && _sys_monitor();

    /** User should implement this pointcut to determine what should be monitored for performance/time */
    public abstract pointcut user_monitor();

    /** reserved pointcut for Automon team */
    public pointcut _sys_monitor();


    /** pointcut that determines what is monitored for exceptions.  It can be the same as the {@link #_monitor()} pointcut */
    public pointcut exceptions() : user_exceptions() && _sys_exceptions();

    /** User should implement this pointcut to determine what should be monitored for performance/time */
    public abstract pointcut user_exceptions();

    /** reserved pointcut for Automon team */
    public  pointcut _sys_exceptions();

}
