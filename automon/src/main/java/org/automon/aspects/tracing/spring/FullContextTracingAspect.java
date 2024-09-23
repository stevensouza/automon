package org.automon.aspects.tracing.spring;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.jmx.TracingMXBean;
import org.automon.aspects.tracing.BaseTracingAspect;
import org.automon.utils.Utils;

/**
 * <p>AOP aspect for full context tracing that can be used in both Spring and non-Spring applications</p>
 *
 * <p>This aspect provides `around` and `afterThrowing` advice to log method entry, exit, and exceptions,
 * along with full context information like execution time, parameters, return value, etc.</p>
 *
 * <p>If the {@link TracingMXBean#enableLogging(boolean)} is set to `false`, only the MDC/NDC values will be set,
 * but the logging 'BEFORE' and 'AFTER' messages will not be called. If any logging statements are run within the
 * entered method, the MDC/NDC values will be available. The aspect can be completely disabled by calling
 * {@link TracingMXBean#enable(boolean)} with a `false` value.</p>
 *
 * <p>Subclasses need to implement the `select()` pointcut to define the pointcuts to be traced.</p>
 */
@Aspect // Indicates that this class is a Spring AOP aspect
public abstract class FullContextTracingAspect extends BaseTracingAspect {

    /**
     * The purpose of this aspect for JMX registration (default: "trace_log_full_context_spring").
     */
    static final String PURPOSE = "trace_log_full_context_spring";

    /**
     * Constructs a new `FullContextTracingAspect` with both tracing and logging enabled by default.
     * The enabled state is determined from configuration properties using `Utils.shouldEnable(getClass().getName())`
     * and logging is enabled using `Utils.shouldEnableLogging(getClass().getName())`.
     */
    public FullContextTracingAspect() {
        initialize(PURPOSE,
                Utils.shouldEnable(getClass().getName()),
                Utils.shouldEnableLogging(getClass().getName())
        );
    }

    /**
     * Constructs a new `FullContextTracingAspect` with the specified tracing and logging enabled states.
     *
     * @param enable        `true` to enable tracing, `false` to disable tracing.
     * @param enableLogging `true` to enable logging, `false` to disable logging.
     */
    public FullContextTracingAspect(boolean enable, boolean enableLogging) {
        initialize(PURPOSE, enable, enableLogging);
    }

    /**
     * Pointcut that defines what join points to trace.
     * <p>
     * **Examples:**
     * </p>
     *
     * <pre>
     * &#64;Pointcut("execution(* com.stevesouza.MyLoggerClassBasic.main(..))")
     * </pre>
     *
     * <pre>
     * &#64;Pointcut("execution(* com.example..*.*(..))")
     * </pre>
     */
    @Pointcut
    public abstract void select();

    /**
     * Around advice for tracing join point execution.
     * <p>
     * This advice wraps the execution of the selected join points, adding detailed context information
     * (including execution time, parameters, return value, etc.) to the MDC/NDC and logging 'BEFORE' and 'AFTER'
     * messages if logging is enabled.
     * </p>
     * <p>
     * Example output (using SLF4J's MDC and NDC):
     * </p>
     *
     * <pre>
     * 2024-08-18 10:39:03,849 INFO  c.s.a.l.a.a.FullContextTracingAspect - BEFORE: MDC={NDC0=MyLoggerClassAll.main(..), NDC1=MyLoggerClassAll.occupationMethod3(..), enclosingSignature=MyLoggerClassAll.occupationMethod3(..), kind=method-execution, parameters={occupation=developer}, target=com.stevesouza.aspectj.logging.automon.all.MyLoggerClassAll@6ed3f258, this=com.stevesouza.aspectj.logging.automon.all.MyLoggerClassAll@6ed3f258}
     * 2024-08-18 10:39:03,850 INFO  c.s.a.l.a.a.FullContextTracingAspect - AFTER: MDC={NDC0=MyLoggerClassAll.main(..), NDC1=MyLoggerClassAll.occupationMethod3(..), enclosingSignature=MyLoggerClassAll.occupationMethod3(..), executionTimeMs=130, kind=method-execution, parameters={occupation=developer}, returnValue=22, target=com.stevesouza.aspectj.logging.automon.all.MyLoggerClassAll@6ed3f258, this=com.stevesouza.aspectj.logging.automon.all.MyLoggerClassAll@6ed3f258}
     * </pre>
     *
     * <p>
     * Note: This class only cleans up NDC/MDC entries that it adds. If an exception is thrown, the MDC/NDC
     * will be cleaned up, and an AFTER log line will be written in the 'after throwing' advice.
     * </p>
     *
     * @param joinPoint The ProceedingJoinPoint representing the intercepted method call.
     * @return The result of the advised method execution.
     * @throws Throwable If the advised method throws an exception, it is re-thrown after logging.
     */
    @Around("select()")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        if (isEnabled()) {
            JoinPoint.StaticPart thisJoinPointStaticPart = joinPoint.getStaticPart();

            // Note: helper.withFullContext(..) is not called directly as the annotation style aspects don't have
            // thisEnclosingJoinPointStaticPart so the call that needs this as an argument is not called below.
            // Other than that the following is the same as helper.withFullContext(..).
            helper.withKind(thisJoinPointStaticPart).
                    withParameters(joinPoint).
                    withSignature(thisJoinPointStaticPart).
                    withTarget(joinPoint).
                    withThis(joinPoint);
            logBefore();

            long startTime = System.currentTimeMillis();
            Object returnValue = joinPoint.proceed();
            helper.withExecutionTime(System.currentTimeMillis() - startTime);
            helper.withReturnValue(objectToString(returnValue));

            logAfter();
            helper.removeFullContext();

            return returnValue;
        } else {
            return joinPoint.proceed();
        }
    }

    /**
     *  AfterThrowing advice for handling exceptions.
     *   Adds the collection context to the MDC/NDC context for the exception event to the existing
     *   context already added from the method entry from the {@link #aroundAdvice(ProceedingJoinPoint)} method and also conditionally logs the information if
     *   logging is enableLogging for this class.
     */
    @AfterThrowing(pointcut = "select()", throwing = "throwable")
    public void afterThrowingAdvice(Throwable throwable) {
        if (isEnabled()) {
            afterThrowing(throwable);
        }
    }

}