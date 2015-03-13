package org.automon.implementations;

import org.aspectj.lang.JoinPoint;
import org.automon.utils.Utils;

/**
 * {@link org.automon.implementations.OpenMon} implementation that uses {@link java.lang.System#out} to log method
 * start, stop and thrown exceptions.  This tracer class is mainly used just to get a better understanding
 * of how Automon works, however similar implementations using log4j, sl4j, java logging etc would be useful.
 */
public final class SysOut implements OpenMon<Object> {
    private static final Object NOOP = new Object();

    @Override
    public Object start(JoinPoint.StaticPart jp) {
        System.out.println("SysOut.start(..): "+ Utils.getLabel(jp));
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
