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


    @Pointcut("within(@org.perf4j.aop.Profiled *) || @annotation(org.perf4j.aop.Profiled)")
    public void perf4j() {
    }

    @Pointcut("within(@com.newrelic.api.agent.Trace *) || @annotation(com.newrelic.api.agent.Trace)")
    public void newrelic() {
    }

    @Pointcut(
            "within(@javax.ejb.Stateless *)     || @annotation(javax.ejb.Stateless) || " +
                    "within(@javax.ejb.Stateful *)      || @annotation(javax.ejb.Stateful) || " +
                    "within(@javax.ejb.MessageDriven *) || @annotation(javax.ejb.MessageDriven) || " +
                    "within(@javax.ejb.Singleton *)     || @annotation(javax.ejb.Singleton)"
    )
    public void ejb() {
    }

    /**
     * Spring annotations are all type level ones: @Component, @Controller, @Repository, @Service
     */
    @Pointcut("within(@org.springframework.stereotype..* *)")
    public void spring() {
    }

    /**
     * JAX-RS provides some annotations to aid in mapping a resource class (a POJO) as a web resource. The annotations include
     * GET, PUT, POST, DELETE and HEAD specify the HTTP request type of a resource.  They are applied to methods.
     */
    @Pointcut(
            "@annotation(javax.ws.rs.GET) || " +
                    "@annotation(javax.ws.rs.PUT) || " +
                    "@annotation(javax.ws.rs.POST) || " +
                    "@annotation(javax.ws.rs.DELETE) || " +
                    "@annotation(javax.ws.rs.HEAD)"
    )
    public void jaxrs() {
    }

    /**
     * Pointcut for jpa entities
     */
    @Pointcut(
            "within(@javax.persistence.Entity *) || " +
                    "within(@javax.persistence.MappedSuperclass *) || " +
                    "within(@javax.persistence.Embeddable *)"
    )
    public void jpa() {
    }
}
