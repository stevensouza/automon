package org.automon.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Contains 'if()' which is not compatible with Spring so this should be used when using AspectJ directly.  Note this allows for disabling of
 * AutoMon via the pointcut language.
 */
@Aspect
public abstract class AspectJBase extends AutomonAspect {

    @Pointcut("ifEnabled()")
    public void sys_monitor1() {

    }

    @Pointcut("ifEnabled()")
    public void sys_exceptions1() {

    }


    @Pointcut("if()")
    public static boolean ifEnabled() {
        return isEnabled();
    }

}
