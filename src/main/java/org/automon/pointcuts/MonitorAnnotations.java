package org.automon.pointcuts;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by stevesouza on 2/28/15.
 */
@Aspect
public abstract class MonitorAnnotations {

    // or could be explicit for jamon, simon, spring, and metrics.  not sure if this applies to both type and method
   // @Pointcut("@annotation(com.jamonapi.aop.spring.MonitorAnnotation)")
    @Pointcut("@annotation(com.jamonapi.aop.spring.MonitorAnnotation) || @annotation(org.javasimon.aop.Monitored)")
    public void monitorAnnotation() {

    }
}
