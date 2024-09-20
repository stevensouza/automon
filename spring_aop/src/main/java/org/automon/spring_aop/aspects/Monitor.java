package org.automon.spring_aop.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.aspects.AutomonSpringAspect;
import org.automon.tracing.spring.RequestIdAspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Sample output from using sysout and the following class:
 *  SysOut.start(..): execution(void org.automon.spring_aop.MonitorMe.myName(String,String))
 *    exiting MonitorMe.myName(...) issued directly from MonitorMe class
 * SysOut.stop(..) ms.: 0
 */
@Aspect
@Component
public class Monitor extends AutomonSpringAspect {
//    @Pointcut("bean(execution(* org.automon.spring_aop.MonitorMe.*(..)))")
//  Spring allows the special bean construct. bean(*) also works for all beans. Other examples
    // of the bean pointcut follow:
    //  bean(*Service)
    //  bean(user*)
    //  bean(*)
    //  bean(serviceA) || bean(serviceB)
    @Pointcut("bean(monitorMe)")
    public void select() {
    }
}
