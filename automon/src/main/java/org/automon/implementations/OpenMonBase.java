package org.automon.implementations;


import org.aspectj.lang.JoinPoint;
import org.automon.utils.AutomonExpirable;
import org.automon.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base implementation of {@link org.automon.implementations.OpenMon} that has useful standard behavior for any class
 * that inherits from it.  For example it tracks and reuses any Exceptions that are thrown and cleans them up after
 * a certain amount of time to avoid memory leaks.
 */
public abstract class OpenMonBase<T> implements OpenMon<T> {

    /** map containing recently thrown exceptions so they can be reused if needed */
    private  Map<Throwable, AutomonExpirable>  exceptionsMap = Utils.createExceptionMap();

    /**
     * Stop the timer and put the the {@link java.lang.Throwable} argument in a map so it can be reused by other methods
     * in the call stack.
     *
     * @param context The object returned by 'start' is passed in.  Typically this would be a timer and should be stopped.
     * @param throwable This argument is put in the exception map, and otherwise ignored in the default implementation.
     */
    @Override
    public void stop(T context, Throwable throwable) {
        stop(context);
        put(throwable);
    }

    /**
     * Override {@link #trackException(org.aspectj.lang.JoinPoint, Throwable)} instead of this method unless the default behavior
     * is not desired.  The useful capability this method provides is to store the thrown exception in the exceptions map.
     *
     * @param jp The {@link org.aspectj.lang.JoinPoint} associated with where the exception was thrown.
     * @param throwable The thrown exception
     */
    @Override
    public void exception(JoinPoint jp, Throwable throwable) {
        // note for the jamon implementation the order of the following methods is important.  That way the stacktrace can be available
        // to be put in all monitors.
        put(throwable);
        trackException(jp, throwable);
    }

    /**
     * Can be overridden to perform the action required by the implementation class.  For example JAMon specific actions
     * are taken here.
     * @param jp
     * @param throwable
     */
    protected void trackException(JoinPoint jp, Throwable throwable) {

    }

    /**
     * @param throwable The exception that was thrown
     * @return One or more labels that should be created that represent the exception being thrown.  By default this is the specific
     * exception label as well as a more general label that represents all exceptions.
     */
    protected List<String> getLabels(Throwable throwable) {
        List<String> labels = new ArrayList<String>();
        labels.add(Utils.getLabel(throwable));
        labels.add(EXCEPTION_LABEL);
        return labels;
    }


    /**
     * Put the thrown exception in a map.  The exception is given a timestamp so it can be removed after a reasonable
     * ammount of time. This ensures that any other method in the call stack doesn't keep putting in the same exception.
     * @param throwable
     */
    protected void put(Throwable throwable) {
        if (!exceptionsMap.containsKey(throwable)) {
            AutomonExpirable automonExpirable = new AutomonExpirable();
            automonExpirable.setThrowable(throwable);
            exceptionsMap.put(throwable, automonExpirable);
        }
    }

    protected AutomonExpirable get(Throwable throwable) {
        return exceptionsMap.get(throwable);
    }

    /** visible for testing */
    Map<Throwable, AutomonExpirable> getExceptionsMap() {
        return exceptionsMap;
    }

}
