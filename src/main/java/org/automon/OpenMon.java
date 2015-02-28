package org.automon;

/**
 * Implementations of this interface can be called automatically by aspectJ pointcuts to monitor your system.  
 */
public interface OpenMon<T> {
    /**
     * Called by aspectJ as part of aroundAdvice. It is typically used to start a timer, however anything could be done
     * such as logging the start of the method.
     *
     * @param label The method name or any identifying context.
     * @return Any Object can be returned.  It will be passed back into the stop(..) method.  Typically this would be a timer
     * that can later be stopped.
     */
    public T start(String label);

    /**
     * Called as part of AspectJ's 'around' advice.  It is called after the event such as a method has completed. Typically
     * a timer that was started in the 'start' method above would be stopped.  Although anything can be done.
     *
     * @param timer The object returned by 'start' is passed in.  Typically this would be a timer and should be stopped.
     *              Note although this variable is named 'timer' for the common case it can really be any object.
     */
    public void stop(T timer);

    /**
     * Nofitication that an exception has occurred. Typically the implementing class would count exceptions though anything is possible.
     *
     * @param label The name identifier for the exception.
     */
    public void exception(String label);

    /**
     * Enable/or disable monitoring.  At a minimum this capability should enable/disable the Advice for timing and counting exceptions.
     * It may also enable/disable the underlying monitoring implementations overall ability to monitor
     * (for example call Jamon's MontiorFactory.disable() method). This is the recommended implementation though not required.
     *
     * @param enable Enable/disable monitoring
     */
    public void enable(boolean enable);

    /**
     *
     * @return True indicates monitoring is enabled, false indicates monitoring is disabled.
     */
    public boolean isEnabled();
}
