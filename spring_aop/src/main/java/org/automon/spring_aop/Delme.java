package org.automon.spring_aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class Delme extends BasicContextTracingAspect{
    @Pointcut("execution(* org.automon.spring_aop.MonitorMe.*(..))")
    public void trace() {
    }
}
