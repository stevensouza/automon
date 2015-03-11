package org.automon.pointcuts;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Pointcuts for easily monitoring annotations defined in other monitoring libraries such as JAMon, Metrics, JavaSimon, NewRelic and Perf4j.
 * This model can also be followed to have automon recognize any other annotation.  Typically these pointcuts will be combined
 * with a pointcut that limits the matches to matches.  For examaple: execution(public *.*
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
            "within(@javax.ejb.Stateless *)     || @annotation(javax.ejb.Stateless) || "+
            "within(@javax.ejb.Stateful *)      || @annotation(javax.ejb.Stateful) || "+
            "within(@javax.ejb.MessageDriven *) || @annotation(javax.ejb.MessageDriven) || "+
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

    // restapi
    /**
     * JAX-RS provides some annotations to aid in mapping a resource class (a POJO) as a web resource. The annotations include:

     @Path specifies the relative path for a resource class or method.
     @GET, @PUT, @POST, @DELETE and @HEAD specify the HTTP request type of a resource.
     @Produces specifies the response Internet media types (used for content negotiation).
     @Consumes specifies the accepted request Internet media types.

     */
    // jpa
    // hibernate

}
