package org.automon.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by stevesouza on 2/28/15.
 */
@Aspect
public abstract class SpringBase extends AutomonAspect {

    @Pointcut("sys_pointcut()")
    public void sys_monitor() {

    }

    @Pointcut("sys_pointcut()")
    public void sys_exceptions() {

    }

    @Pointcut("org.automon.pointcuts.Select.publicMethod() && !within(AutomonAspect+)")
    public void sys_pointcut() {

    }
}
