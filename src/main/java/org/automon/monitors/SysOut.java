package org.automon.monitors;

import org.aspectj.lang.JoinPoint;

/**
 * Created by stevesouza on 2/26/15.
 */
public final class SysOut extends OpenMonBase<Object> {
    private static final Object NOOP = new Object();

    @Override
    public Object start(JoinPoint jp) {
        System.out.println("SysOut.start(..): "+getLabel(jp));
        return NOOP;
    }

    @Override
    public void stop(Object context) {
        System.out.println("SysOut.stop(..)");
    }

    @Override
    public void stop(Object context, Throwable throwable) {
        System.out.println("SysOut.stop(..) - Exception: "+throwable);
    }

    @Override
    public void exception(JoinPoint jp, Throwable throwable) {
        System.out.println("SysOut.exception(..): JoinPoint="+ jp+", Exception="+throwable);
    }
}
