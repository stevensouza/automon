package org.automon.monitors;

import org.aspectj.lang.JoinPoint;

/**
 * Created by stevesouza on 3/2/15.
 */
public abstract class OpenMonBase<T> implements OpenMon<T> {

    /**
     * @param jp The pointcut from the @Around advice that was intercepted.
     * @return A label suitable for a monitoring/timer label.  It is a convenience method and need not be
     * used to create the monitor
     */
    protected String getLabel(JoinPoint jp) {
        return jp.getStaticPart().toString();
    }

    /**
     * @param throwable The exception that was thrown
     * @return A label suitable for a monitoring label representing the thrown exception.  It is a convenience method and need not be
     * used to create the monitor
     */
    protected String getLabel(Throwable throwable) {
        return throwable.getClass().getName();
    }
}
