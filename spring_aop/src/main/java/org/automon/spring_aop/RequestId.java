package org.automon.spring_aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.tracing.spring.RequestIdAspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(-1) // makes this aspect the highest precedence.
public class RequestId extends RequestIdAspect {
    @Pointcut("execution(* org.automon.spring_aop.MonitorMe.*(..))")
    public void select() {
    }
}
