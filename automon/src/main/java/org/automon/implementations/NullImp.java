package org.automon.implementations;

import org.aspectj.lang.JoinPoint;

/**
 * {@link org.automon.implementations.OpenMon} implementation that performs a noop for all methods.  This impementation
 * is used when Automon is disabled.
 */
public final class NullImp implements OpenMon<Object> {
    private static final Object NOOP = new Object();

    @Override
    public Object start(JoinPoint.StaticPart label) {
        return NOOP;
    }

    @Override
    public void stop(Object context) {

    }

    @Override
    public void stop(Object context, Throwable throwable) {

    }

    @Override
    public void exception(JoinPoint jp, Throwable throwable) {

    }
}
