package org.automon.implementations;

import org.aspectj.lang.JoinPoint;
import org.automon.utils.Utils;

/**
 * {@link org.automon.implementations.OpenMon} implementation that uses {@link java.lang.System#out} to log method
 * start, stop and thrown exceptions.  This tracer class is mainly used just to get a better understanding
 * of how Automon works, however similar implementations using log4j, sl4j, java logging etc would be useful.
 */
public final class SysOut implements OpenMon<TimerContext> {

    @Override
    public TimerContext start(JoinPoint.StaticPart jp) {
        System.out.println("SysOut.start(..): " + Utils.getLabel(jp));
        return new TimerContext(jp);
    }

    @Override
    public void stop(TimerContext context) {
        System.out.println("SysOut.stop(..) ms.: " + context.stop());
    }

    @Override
    public void stop(TimerContext context, Throwable throwable) {
        System.out.println("SysOut.stop(..) ms.: " + context.stop() + " - Exception: " + throwable);
    }

    @Override
    public void exception(JoinPoint jp, Throwable throwable) {
        System.out.println("SysOut.exception(..): JoinPoint=" + jp + ", Exception=" + throwable);
    }
}
