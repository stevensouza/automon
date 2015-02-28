package org.automon.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by stevesouza on 2/28/15.
 */
@Aspect
public abstract class SpringBase extends AutomonAspect {

    @Pointcut("org.automon.pointcuts.Basic.publicMethod()")
    public void sys_monitor1() {

    }


}
