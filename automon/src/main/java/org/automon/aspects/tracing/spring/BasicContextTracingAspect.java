package org.automon.aspects.tracing.spring;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.aspects.tracing.BaseTracingAspect;
import org.automon.jmx.TracingMXBean;
import org.automon.utils.Utils;

/**
 * <p>Spring AOP aspect for managing basic context tracing.</p>
 *
 * <p>This aspect provides `around` and `afterThrowing` advice to log method entry, exit, and exceptions,
 * along with basic context information like execution time.</p>
 *
 * <p>If the {@link TracingMXBean#enableLogging(boolean)}
 * is set to `false`, only the MDC/NDC values will be set, but the logging BEFORE/AFTER methods
 * will not be called (no tracing).  If any logging statements are run within the entered method, the MDC/NDC values
 * will be available. The aspect can be completely disabled by calling {@link TracingMXBean#enable(boolean)}
 * with a `false` value.</p>
 *
 * <p>Subclasses need to implement the `select()` pointcut to define the pointcuts to be traced.</p>
 */
@Aspect
public abstract class BasicContextTracingAspect extends BaseTracingAspect {

    /**
     * The purpose of this aspect for JMX registration.
     */
    static final String PURPOSE = "trace_log_basic_context_spring";

    /**
     * Constructs a new `BasicContextTracingAspect` with both tracing and logging enabled by default.
     * The enabled state is determined from configuration properties using `Utils.shouldEnable(getClass().getName())`
     * and logging is enabled using `Utils.shouldEnableLogging(getClass().getName())`.
     */
    public BasicContextTracingAspect() {
        initialize(PURPOSE,
                Utils.shouldEnable(getClass().getName()),
                Utils.shouldEnableLogging(getClass().getName())
        );
    }

    /**
     * Constructs a new `BasicContextTracingAspect` with the specified tracing and logging enabled states.
     *
     * @param enable        `true` to enable tracing, `false` to disable tracing.
     * @param enableLogging `true` to enable logging, `false` to disable logging.
     */
    public BasicContextTracingAspect(boolean enable, boolean enableLogging) {
        initialize(PURPOSE, enable, enableLogging);
    }

    /**
     * Pointcut that defines which methods or classes will be traced.
     * This should be implemented by subclasses to target specific join points in your application.
     * <p>
     * **Examples:**
     * </p>
     *
     * <pre>
     *      &#64;Pointcut("execution(* com.stevesouza.MyLoggerClassBasic.main(..))")
     *  </pre>
     *
     * <pre>
     * &#64;Pointcut("execution(* com.example..*.*(..))")
     * </pre>
     */
    @Pointcut
    public abstract void select();

    /**
     * Around advice for tracing method execution.
     * <p>
     * This advice wraps the execution of the selected methods, adding context information to the MDC/NDC
     * and logging 'BEFORE' and 'AFTER' messages if logging is enabled. It also measures and logs the execution time.
     * </p>
     * <p>
     * Example output (using SLF4J's MDC and NDC):
     * </p>
     * <pre>
     * 2024-08-18 10:28:35,809 INFO  c.s.a.l.a.b.BasicContextTracingAspect - BEFORE: MDC={NDC0=MyLoggerClassBasic.main(..), NDC1=MyLoggerClassBasic.method1(), NDC2=MyLoggerClassBasic.nameMethod2(..), NDC3=MyLoggerClassBasic.occupationMethod3(..), kind=method-execution}
     * 2024-08-18 10:28:35,809 INFO  c.s.a.l.a.b.BasicContextTracingAspect - AFTER: MDC={NDC0=MyLoggerClassBasic.main(..), NDC1=MyLoggerClassBasic.method1(), NDC2=MyLoggerClassBasic.nameMethod2(..), NDC3=MyLoggerClassBasic.occupationMethod3(..), executionTimeMs=122, kind=method-execution}
     * </pre>
     *
     * <p>
     * Note: This class only cleans up NDC/MDC that it added. Also, if an exception is thrown, the MDC/NDC
     * will be cleaned up and an AFTER log line will be written in the 'after throwing' advice.
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

            // Note: helper.withBasicContext(..) is not called directly as the annotation style aspects don't have
            // thisEnclosingJoinPointStaticPart, so the call that needs this as an argument is not called below.
            // Other than that, the following is the same as helper.withBasicContext(..).
            helper.withKind(thisJoinPointStaticPart).
                    withSignature(thisJoinPointStaticPart);
            logBefore();

            long startTime = System.currentTimeMillis();
            Object returnValue = joinPoint.proceed();
            helper.withExecutionTime(System.currentTimeMillis() - startTime);

            logAfter();
            helper.removeBasicContext();

            return returnValue;
        } else {
            return joinPoint.proceed();
        }
    }

    /**
     * AfterThrowing advice for handling exceptions.
     * <p>
     * This advice is executed when an exception is thrown within the traced methods. It adds exception information to the
     * MDC/NDC context and logs an error message if logging is enabled.
     * </p>
     *
     * @param throwable The thrown exception.
     */
    @AfterThrowing(pointcut = "select()", throwing = "throwable")
    public void afterThrowingAdvice(Throwable throwable) {
        if (isEnabled()) {
            afterThrowing(throwable);
        }
    }
}