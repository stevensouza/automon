package org.automon.pointcuts;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * <p>This AspectJ aspect defines pointcuts for conveniently targeting methods or classes annotated with specific annotations
 * from popular monitoring libraries (JAMon, Metrics, NewRelic) and Spring.</p>
 *
 * <p>It allows Automon to easily recognize and monitor or trace code elements based on these annotations.</p>
 *
 * <p>**Note:**</p>
 * <ul>
 *     <li>`@annotation(..)` is used for matching method-level annotations.</li>
 *     <li>`@within(..)` or `within(@..)` is used for matching class-level annotations.</li>
 * </ul>
 */
@Aspect // Indicates that this class is an AspectJ aspect
public abstract class Annotations {

    /**
     * Pointcut that matches methods or classes annotated with `@Monitor`.
     */
    @Pointcut("within(@org.automon.annotations.Monitor *) || @annotation(org.automon.annotations.Monitor)")
    public void automon_monitor() {
    }

    /**
     * Pointcut that matches methods or classes annotated with `@Trace`.
     */
    @Pointcut("within(@org.automon.annotations.Trace *) || @annotation(org.automon.annotations.Trace)")
    public void automon_trace() {
    }

    /**
     * Pointcut that matches methods or classes annotated with JAMon's `@MonitorAnnotation`.
     */
    @Pointcut("within(@com.jamonapi.aop.spring.MonitorAnnotation *) || @annotation(com.jamonapi.aop.spring.MonitorAnnotation)")
    public void jamon() {
    }

    /**
     * Pointcut that matches methods or classes annotated with Metrics' `@Timed`.
     */
    @Pointcut("within(@com.codahale.metrics.annotation.Timed *) || @annotation(com.codahale.metrics.annotation.Timed)")
    public void metrics() {
    }

    /**
     * Pointcut that matches methods or classes annotated with New Relic's `@Trace`.
     */
    @Pointcut("within(@com.newrelic.api.agent.Trace *) || @annotation(com.newrelic.api.agent.Trace)")
    public void newrelic() {
    }

    /**
     * Pointcut that matches classes annotated with any Spring stereotype annotations (e.g., `@Component`, `@Controller`, `@Repository`, `@Service`).
     */
    @Pointcut("within(@org.springframework.stereotype..* *)")
    public void spring() {
    }
}
