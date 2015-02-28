package org.automon.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by stevesouza on 2/28/15.
 */
@Aspect
public abstract class AspectJBase extends AutomonAspect {

    @Pointcut("isAutomonEnabled()")
    public void sys_monitor1() {

    }

    @Pointcut("isAutomonEnabled()")
    public void sys_exceptions1() {

    }


    @Pointcut("if()")
    public static boolean isAutomonEnabled() {
        return isEnabled();
    }

}
