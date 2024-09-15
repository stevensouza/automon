package com.mypackage.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.tracing.aspectj.FullContextTracingAspect;

/**
 * Sample outputs from this aspect...
 * c.m.a.FullContextTrace INFO 09:12:29.877 - AFTER MDC: {NDC0=HelloWorld.main(..), NDC1=HelloWorld.run(..), executionTimeMs=5122, returnValue=null}
 * c.m.a.FullContextTrace INFO 09:12:24.790 - BEFORE MDC: {NDC0=HelloWorld.main(..), NDC1=HelloWorld.run(..), NDC2=HelloWorld.run(..), NDC3=HelloWorld.iMessedUp_CheckedException(..), NDC4=HelloWorld.iMessedUp_CheckedException(..), NDC5=HelloWorld.MyCheckedException(..), NDC6=HelloWorld.MyCheckedException(..), enclosingSignature=HelloWorld.MyCheckedException(..), kind=constructor-execution, parameters={message=This is my CheckedException message - Steve}, target=com.stevesouza.helloworld.HelloWorld$MyCheckedException: This is my CheckedException message - Steve, this=com.stevesouza.helloworld.HelloWorld$MyCheckedException: This is my CheckedException message - Steve}
 */
@Aspect
public class FullContextTrace extends FullContextTracingAspect {
// Note the pointcut will work with public/private/protected methods (privileged)
// The commented out within(...) pointcut will work with any of the aspectj types/kind
// that are supported such as execution/call/get/set/constructors/static initialization ...
//     @Pointcut("within(com.stevesouza.helloworld.HelloWorld+)")

// Note the following apporach is not required as running the code or not based on the enabled/disabled
// status of the aspect works for all of automon's aspects with no other special handling.  However, I am
// including the following approach which offers an alternative approach for quickly not matching the pointcut
// itself if the aspect is disabled.  In general this approach is not needed though.  See BasicContextTrace for the
// more typical and simple way that an aspect should be created.

    private static FullContextTrace INSTANCE;

    public FullContextTrace() {
        INSTANCE = this;
    }

    @Pointcut("enabled() && execution(* com.stevesouza.helloworld.HelloWorld.*(..))")
    public void select() {
    }

    @Pointcut("if()")
    public static boolean enabled() {
        return INSTANCE.isEnabled();
    }
}
