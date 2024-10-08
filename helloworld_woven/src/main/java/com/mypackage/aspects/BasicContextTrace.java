package com.mypackage.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.aspects.tracing.spring.BasicContextTracingAspect;

/** Sample outputs from this aspect...
 * c.m.a.FullContextTrace INFO 09:12:29.877 - AFTER MDC: {NDC0=HelloWorld.main(..), NDC1=HelloWorld.run(..), executionTimeMs=5122, returnValue=null}
 * c.m.a.FullContextTrace INFO 09:12:24.790 - BEFORE MDC: {NDC0=HelloWorld.main(..), NDC1=HelloWorld.run(..), NDC2=HelloWorld.run(..), NDC3=HelloWorld.iMessedUp_CheckedException(..), NDC4=HelloWorld.iMessedUp_CheckedException(..), NDC5=HelloWorld.MyCheckedException(..), NDC6=HelloWorld.MyCheckedException(..), enclosingSignature=HelloWorld.MyCheckedException(..), kind=constructor-execution, parameters={message=This is my CheckedException message - Steve}, target=com.stevesouza.helloworld.HelloWorld$MyCheckedException: This is my CheckedException message - Steve, this=com.stevesouza.helloworld.HelloWorld$MyCheckedException: This is my CheckedException message - Steve}
 */
@Aspect
public class BasicContextTrace extends BasicContextTracingAspect {
// Note the pointcut will work with public/private/protected methods (privileged)
// The commented out within(...) pointcut will work with any of the aspectj types/kind
// that are supported such as execution/call/get/set/constructors/static initialization ...
//     @Pointcut("within(com.stevesouza.helloworld.HelloWorld+)")


    // note the following is a private method so the annotation style aspects work at compile time (at least)
    @Pointcut("execution(* com.stevesouza.helloworld.HelloWorld.iMessedUp_RuntimeException(..))")
    public void select() {

    }

}
