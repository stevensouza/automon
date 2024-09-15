package com.mypackage.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.tracing.aspectj.RequestIdAspect;

/** Sample outputs from this aspect...
 */
@Aspect
public class RequestIdTrace extends RequestIdAspect {
// Note the pointcut will work with public/private/protected methods (privileged)
// The commented out within(...) pointcut will work with any of the aspectj types/kind
// that are supported such as execution/call/get/set/constructors/static initialization ...
//     @Pointcut("within(com.stevesouza.helloworld.HelloWorld+)")

    @Pointcut("execution(* com.stevesouza.helloworld.HelloWorld.*(..))")
    public void select() {

    }

}
