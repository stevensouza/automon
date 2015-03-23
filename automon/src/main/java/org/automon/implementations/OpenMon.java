package org.automon.implementations;

import org.aspectj.lang.JoinPoint;

/**
 * Implementations of this interface can be called automatically by aspectJ pointcuts to monitor your system.  Implementations could
 * time methods and count exceptions.  Other implementations could simply log method calls and exceptions.  The implementation should
 * be stateless or at a minimum thread safe.
 */
public interface OpenMon<T> {
    /**
     * Label used for tracking exceptions.
     */
    public static final String EXCEPTION_LABEL = "org.automon.Exceptions";

    /**
     * Called by aspectJ as part of aroundAdvice. It is typically used to start a timer, however anything could be done
     * such as logging the start of the method.
     *
     * @param jp The method name or any identifying context.
     * @return Any Object can be returned.  It will be passed back into the stop(..) method.  Typically this would be a timer
     * that can later be stopped.
     */
    public T start(JoinPoint.StaticPart jp);

    /**
     * Called as part of AspectJ's 'around' advice.  It is called after the event such as a method has completed. Typically
     * a timer that was started in the 'start' method above would be stopped.  Although anything can be done.
     *
     * @param context The object returned by 'start' is passed into this parameter.  Typically this would be a timer and should be stopped.
     *    Note although this variable is typically a 'timer' it can really be any object, or state needed.
     */
    public void stop(T context);


    /**
     * Called as part of AspectJ's 'around' advice.  It is called after the event such as a method has completed. Typically
     * a timer that was started in the 'start' method above would be stopped.  Although anything can be done.
     * Make sure not to double count exceptions which can happen if you also handle them in {@link #exception}
     *
     * @param context The object returned by 'start' is passed in.  Typically this would be a timer and should be stopped.
     *              Note although this variable is typically a 'timer' it can really be any object, or state needed.
     */
    public void stop(T context, Throwable throwable);

    /**
     * Notification that an exception has occurred. Typically the implementing class would count exceptions though anything is possible.
     *
     * @param jp The {@link org.aspectj.lang.JoinPoint} associated with where the exception was thrown.  Note this gives you access
     *           to the argument names and values of the pointcut that threw the exception
     * @param throwable The thrown exception
     */
    public void exception(JoinPoint jp, Throwable throwable);

}
