package org.automon.monitors;

import org.aspectj.lang.JoinPoint;

/**
 * Created by stevesouza on 3/2/15.
 */
public abstract class OpenMonBase<T> implements OpenMon<T> {

    /**
     * @param jp The pointcut from the @Around advice that was intercepted.
     * @return A label suitable for a monitoring/timer label.  It is a convenience mehtod and need not be
     * used to create the monitor
     */
    protected String getLabel(JoinPoint jp) {
        return jp.getStaticPart().toString();
    }
}
