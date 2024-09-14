package org.automon.spring_aop.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.tracing.spring.FullContextTracingAspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FullContextTrace extends FullContextTracingAspect {
    // note to have method parameters written with their argument names you have to compile in debug mode
    // (-g or in maven <compilerArgument>-parameters</compilerArgument>
    //  Example with debug enabled: parameters={fname=steve, lname=souza}
    // Without debug enabled you will still see the values but with generic parameter names such as
    //  parameters={param0=steve, param1=souza}
    @Pointcut("execution(* org.automon.spring_aop.MonitorMe.*(..))")
    public void select() {
    }
}
