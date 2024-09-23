package org.automon.aspects.tracing.aspectj;

import org.aspectj.lang.JoinPoint;
import org.automon.jmx.TracingMXBean;
import org.automon.aspects.tracing.BaseTracingAspect;
import org.automon.utils.Utils;

/**
 * Aspect for full context tracing using AspectJ in non-Spring applications.
 * It provides `around` and `afterThrowing` advice to log method entry, exit, and exceptions,
 * along with full context information like execution time, parameters, return value, etc.
 *
 * <p>If {@link TracingMXBean#enableLogging(boolean)} is set to `false`, only the MDC/NDC values will be set,
 * but the logging BEFORE/AFTER methods will not be called. If any logging statements are run within the
 * entered method, the MDC/NDC values will be available. The aspect can be completely disabled by calling
 * {@link TracingMXBean#enable(boolean)} with a `false` value.</p>
 *
 * <p>Subclasses need to implement the `select()` pointcut to define the pointcuts to be traced.</p>
 */
public privileged abstract aspect FullContextTracingAspect extends BaseTracingAspect {

    /**
     * The purpose of this aspect for JMX registration.
     */
    static final String PURPOSE = "trace_log_full_context_native";

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
     * Pointcut that defines the methods or classes to be traced.
     * This should be implemented by subclasses to target specific join points in your application.
     * <p>
     * **Examples:**
     * </p>
     *
     * <pre>
     * pointcut select() : execution(* com.stevesouza.MyLoggerClassBasic.main(..));
     * </pre>
     *
     * <pre>
     * pointcut select() : execution(* com.example..*.*(..));
     * </pre>
     */
    public abstract pointcut select();

    /**
     * Around advice for tracing method execution.
     * <p>
     * This advice wraps the execution of the selected pointcut, adding detailed context information
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
     * Note: This aspect only cleans up the NDC/MDC entries it adds. If an exception is thrown, the MDC/NDC
     * will be cleaned up, and an AFTER log line will be written in the 'after throwing' advice.
     * </p>
     *
     * @return The result of the advised pointcut execution.
     * @throws Throwable If the advised pointcut throws an exception, it is re-thrown after logging.
     */
    Object around() : select() {
        if (isEnabled()) {
            helper.withFullContext(thisJoinPoint, thisJoinPointStaticPart, thisEnclosingJoinPointStaticPart);
            logBefore();

            long startTime = System.currentTimeMillis();
            Object returnValue = proceed();
            helper.withExecutionTime(System.currentTimeMillis() - startTime);
            helper.withReturnValue(objectToString(returnValue));

            logAfter();
            helper.removeFullContext();

            return returnValue;
        } else {
            return proceed();
        }
    }

    /**
     * AfterThrowing advice for handling exceptions.
     * <p>
     * This advice is executed when an exception is thrown within the traced methods. It delegates the exception handling
     * to the `afterThrowing` method in the base class, ensuring proper cleanup and logging.
     * </p>
     *
     * @param throwable The thrown exception
     */
    after() throwing(Throwable throwable): select() {
        if (isEnabled()) {
            afterThrowing(throwable);
        }
    }
}