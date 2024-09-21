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
 * Spring AOP aspect for full context tracing using AOP (Aspect-Oriented Programming).
 * It provides `around` and `afterThrowing` advice to log method entry, exit, and exceptions,
 * along with full context information like execution time.
 *
 * <p>If the {@link TracingMXBean#enableLogging(boolean)}
 * is set to false then only the MDC/NDC values will be set but the logging BEFORE/AFTER methods
 * will not be called.  If any logging statements are run within the entered method the MDC/NDC values
 * will be available. The aspect can be completely disabled by calling {@link TracingMXBean#enable(boolean)}
 * with a false value.
 * </p>
 *
 * <p>Subclasses need to implement the `select()` pointcut to define the pointcuts to be traced.</p>
 */
@Aspect
public abstract class FullContextTracingAspect extends BaseTracingAspect {
    static final String PURPOSE = "trace_log_full_context_spring";

    /**
     * Constructs a new `FullContextTracingAspect` with both tracing and logging enabled by default.
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
     * Pointcut that defines where the request ID should be added and removed.
     * This should be implemented to target the entry and exit points of requests in your application.
     * <p>
     * <p>**Examples:**</p>
     *
     * <pre>
     *      pointcut select() : execution(* com.stevesouza.MyLoggerClassBasic.main(..));
     *  </pre>
     *
     * <pre>
     * pointcut select() : enabled() && execution(* com.example..*.*(..));
     * </pre>
     * <p>
     * Alternatively the following equivalent approach could be used:
     * <pre>
     *  pointcut select() : if(isEnabled()) && execution(* com.example..*.*(..));
     * </pre>
     */
    @Pointcut
    public abstract void select();

    /**
     * Around advice for tracing method execution.
     * Adds NDC/MDC context on method entry and exit, along with other context information such as execution time.
     * The information is conditionally logged if {@link TracingMXBean#enableLogging(boolean)} is set.
     * <p>
     * 2024-08-18 10:39:03,849 INFO  c.s.a.l.a.a.FullContextTracingAspect - BEFORE: MDC={NDC0=MyLoggerClassAll.main(..), NDC1=MyLoggerClassAll.occupationMethod3(..), enclosingSignature=MyLoggerClassAll.occupationMethod3(..), kind=method-execution, parameters={occupation=developer}, target=com.stevesouza.aspectj.logging.automon.all.MyLoggerClassAll@6ed3f258, this=com.stevesouza.aspectj.logging.automon.all.MyLoggerClassAll@6ed3f258}
     * 2024-08-18 10:39:03,850 INFO  c.s.a.l.a.a.FullContextTracingAspect - AFTER: MDC={NDC0=MyLoggerClassAll.main(..), NDC1=MyLoggerClassAll.occupationMethod3(..), enclosingSignature=MyLoggerClassAll.occupationMethod3(..), executionTimeMs=130, kind=method-execution, parameters={occupation=developer}, returnValue=22, target=com.stevesouza.aspectj.logging.automon.all.MyLoggerClassAll@6ed3f258, this=com.stevesouza.aspectj.logging.automon.all.MyLoggerClassAll@6ed3f258}
     * </p>
     *
     * <p>Note this class only cleans up NDC/MDC that it added. Also note if an exception is thrown the MDC/NDC
     * will be cleaned up and an AFTER log line will be written in the 'after throwing' advice.
     * </p>
     *
     * @return The result of the advised method execution.
     * @throws Throwable If the advised method throws an exception, it is re-thrown after logging.
     */
    @Around("select()")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        if (isEnabled()) {
            JoinPoint.StaticPart thisJoinPointStaticPart = joinPoint.getStaticPart();

            // note helper.withFullContext(..) is not called directly as the annotation style aspects don't have
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
     * AfterThrowing advice for handling exceptions.
     * Adds the collection context to the MDC/NDC context for the exception event to the existing
     * context already added from the method entry from the {@link #aroundAdvice(ProceedingJoinPoint)} method and also conditionally logs the information if
     * logging is enableLogging for this class.
     */
    @AfterThrowing(pointcut = "select()", throwing = "throwable")
    public void afterThrowingAdvice(Throwable throwable) {
        if (isEnabled()) {
            afterThrowing(throwable);
        }
    }

}
