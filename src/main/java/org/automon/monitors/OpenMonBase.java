package org.automon.monitors;


import org.aspectj.lang.JoinPoint;
import org.automon.utils.Expirable;
import org.automon.utils.TimeExpirable;
import org.automon.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by stevesouza on 3/2/15.
 */
public abstract class OpenMonBase<T> implements OpenMon<T> {

    private  Map<Throwable, Expirable>  exceptionsMap = Utils.createExceptionMap();

    /**
     * Note the default implementation simply calls {@link #stop(T)} and doesn't do anything with the {@link java.lang.Throwable} argument
     *
     * @param context The object returned by 'start' is passed in.  Typically this would be a timer and should be stopped.
     * @param throwable This argument is ignored in the default implementation.
     */
    @Override
    public void stop(T context, Throwable throwable) {
        stop(context);
        put(throwable);
    }

    /**
     * Override {@link #trackException(org.aspectj.lang.JoinPoint, Throwable)} instead of this method unless the default behavior
     * is not desired
     *
     * @param jp The {@link org.aspectj.lang.JoinPoint} associated with where the exception was thrown.
     * @param throwable The thrown exception
     */
    @Override
    public void exception(JoinPoint jp, Throwable throwable) {
        trackException(jp, throwable);
        put(throwable);
    }

    /**
     * Can be overridden to perform the action required by the implementation class.  For example JAMon specific actions
     * could be taken here.
     * @param jp
     * @param throwable
     */
    protected void trackException(JoinPoint jp, Throwable throwable) {

    }

    /**
     * @param throwable The exception that was thrown
     * @return Labels that should be created that represent the exception being thrown.  By default this is the specific
     * exception label as well as a more general label that represents all exceptions.
     */
    protected List<String> getLabels(Throwable throwable) {
        List<String> labels = new ArrayList<String>();
        labels.add(Utils.getLabel(throwable));
        labels.add(EXCEPTION_LABEL);
        return labels;
    }


    protected void put(Throwable throwable) {
        // note 'get' is used instead of 'containsKey' as we want to update the LRU information for each access.
        if (!exceptionsMap.containsKey(throwable)) {
            exceptionsMap.put(throwable, new TimeExpirable());
        }
    }

    /** visible for testing */
    Map<Throwable, Expirable> getExceptionsMap() {
        return exceptionsMap;
    }

}
