package org.automon.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Contains 'if()' which is not compatible with Spring so this should be used when using AspectJ directly.  Note this allows for disabling of
 * AutoMon via the pointcut language.
 */
@Aspect
public abstract class AspectJBase extends AutomonAspect {

    @Pointcut("sys_pointcut()")
    public void sys_monitor() {

    }

    @Pointcut("sys_pointcut()")
    public void sys_exceptions() {

    }

    @Pointcut("within(java.lang.Object+) && !within(AspectJBase+)")
    public void sys_pointcut() {

    }

}
