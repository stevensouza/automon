package org.automon.pointcuts;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by stevesouza on 2/28/15.
 */
@Aspect
public abstract class MonitorAnnotations {

//    @Pointcut("@annotation(Monitor? com..)")
//    public void annotatedWithMonitor() {
//    }

    @Pointcut("@annotation(org.javasimon.aop.Monitored)")
    public void javaSimon() {
    }

//    @Pointcut("@annotation(com.jamonapi.aop.spring.MonitorAnnotation)")
//    public void jamon() {
//    }

    @Pointcut("within(@com.jamonapi.aop.spring.MonitorAnnotation *) || @annotation(com.jamonapi.aop.spring.MonitorAnnotation)")
    public void jamon() {
    }

//    @Pointcut("@annotation(org.perf4j.aop.Profiled)")
//    public void perf4j() {
//    }

}
