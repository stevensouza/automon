package org.automon.pointcuts;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Pointcuts for easily monitoring annotations defined in other monitoring libraries such as JAMon, Metrics, JavaSimon, NewRelic and Perf4j.
 * This model can also be followed to have automon recognize any other annotation.
 *
 * <p>
 * Note @annotation(..) applies to method annotations, and @within(..) or within(@..) apply to class annotations.
 * </p>
 */
@Aspect
public abstract class Annotations {

    @Pointcut("within(@org.automon.annotations.Monitor *) || @annotation(org.automon.annotations.Monitor)")
    public void automon() {
    }

    @Pointcut("within(@com.jamonapi.aop.spring.MonitorAnnotation *) || @annotation(com.jamonapi.aop.spring.MonitorAnnotation)")
    public void jamon() {
    }

    @Pointcut("within(@org.javasimon.aop.Monitored *) || @annotation(org.javasimon.aop.Monitored)")
    public void javasimon() {
    }

    @Pointcut("within(@com.codahale.metrics.annotation.Timed *) || @annotation(com.codahale.metrics.annotation.Timed)")
    public void metrics() {
    }

    @Pointcut("within(@com.newrelic.api.agent.Trace *) || @annotation(com.newrelic.api.agent.Trace)")
    public void newrelic() {
    }

    /**
     * Spring annotations are all type level ones: @Component, @Controller, @Repository, @Service
     */
    @Pointcut("within(@org.springframework.stereotype..* *)")
    public void spring() {
    }

}
