package org.automon.tracing.aspectj;

import org.aspectj.lang.JoinPoint;
import org.automon.tracing.BaseTracingAspect;
import org.automon.utils.Utils;

/**
 * Aspect for full context tracing using AOP (Aspect-Oriented Programming).
 * It provides `around` and `afterThrowing` advice to log method entry, exit, and exceptions,
 * along with full context information like execution time.
 *
 * <p>If the {@link org.automon.tracing.jmx.TraceJmxControllerMBean#enableLogging(boolean)}
 * is set to false then only the MDC/NDC values will be set but the logging BEFORE/AFTER methods
 * will not be called.  If any logging statements are run within the entered method the MDC/NDC values
 * will be available. The aspect can be completely disabled by calling {@link org.automon.tracing.jmx.TraceJmxControllerMBean#enable(boolean)}
 * with a false value.
 * </p>
 *
 * <p>Subclasses need to implement the `select()` pointcut to define the pointcuts to be traced.</p>
 */
public privileged abstract aspect FullContextTracingAspect extends BaseTracingAspect {
    static final String PURPOSE = "trace_log_full_context_native";
    /**
     * Constructs a new `FullContextTracingAspect` with both tracing and logging enabled by default.
     */
    public FullContextTracingAspect() {
        this(Utils.shouldEnable(FullContextTracingAspect.class.getName()),
             Utils.shouldEnableLogging(FullContextTracingAspect.class.getName())
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
     *  <pre>
     *      pointcut select() : execution(* com.stevesouza.MyLoggerClassBasic.main(..));
     *  </pre>
     *
     * <pre>
     * pointcut select() : enabled() && execution(* com.example..*.*(..));
     * </pre>
     *
     * Alternatively the following equivalent approach could be used:
     * <pre>
     *  pointcut select() : if(isEnabled()) && execution(* com.example..*.*(..));
     * </pre>
     *
     */
    public abstract pointcut select();

    /**
     * A pointcut that matches if tracing is enabled.
     * <p>
     * This pointcut can be used in conjunction with other pointcuts to conditionally apply advice
     * only when tracing is enabled.
     *
     * <p>**Examples:**</p>
     *
     * <pre>
     * pointcut select() : enabled() && execution(* com.example..*.*(..));
     * </pre>
     *
     * Alternatively the following equivalent approach could be used:
     * <pre>
     *  pointcut select() : if(isEnabled()) && execution(* com.example..*.*(..));
     * </pre>
     */
    public pointcut enabled() : if(isEnabled());

    /**
     * Around advice for tracing method execution.
     * Adds NDC/MDC context on method entry and exit, along with other context information such as execution time.
     * The information is conditionally logged if {@link #enableLogging(boolean)} is set.
     * See {@link org.automon.utils.LogTracingHelper#withFullContext(JoinPoint, JoinPoint.StaticPart, JoinPoint.StaticPart)}
     * for additional context being traced. Example output which uses SLF4J's MDC and NDC.
     *       <p>
     *         2024-08-18 10:39:03,849 INFO  c.s.a.l.a.a.FullContextTracingAspect - BEFORE: MDC={NDC0=MyLoggerClassAll.main(..), NDC1=MyLoggerClassAll.occupationMethod3(..), enclosingSignature=MyLoggerClassAll.occupationMethod3(..), kind=method-execution, parameters={occupation=developer}, target=com.stevesouza.aspectj.logging.automon.all.MyLoggerClassAll@6ed3f258, this=com.stevesouza.aspectj.logging.automon.all.MyLoggerClassAll@6ed3f258}
     *         2024-08-18 10:39:03,850 INFO  c.s.a.l.a.a.FullContextTracingAspect - AFTER: MDC={NDC0=MyLoggerClassAll.main(..), NDC1=MyLoggerClassAll.occupationMethod3(..), enclosingSignature=MyLoggerClassAll.occupationMethod3(..), executionTimeMs=130, kind=method-execution, parameters={occupation=developer}, returnValue=22, target=com.stevesouza.aspectj.logging.automon.all.MyLoggerClassAll@6ed3f258, this=com.stevesouza.aspectj.logging.automon.all.MyLoggerClassAll@6ed3f258}
     *       </p>
     *
     * <p>Note this class only cleans up NDC/MDC that it added. Also note if an exception is thrown the MDC/NDC
     * will be cleaned up and an AFTER log line will be written in the 'after throwing' advice.
     * </p>
     * 
     * @return The result of the advised method execution.
     * @throws Throwable If the advised method throws an exception, it is re-thrown after logging.
     */
    Object around() : select() {
        helper.withFullContext(thisJoinPoint, thisJoinPointStaticPart, thisEnclosingJoinPointStaticPart);
        logBefore();

        long startTime = System.currentTimeMillis();
        Object returnValue =  proceed();
        helper.withExecutionTime(System.currentTimeMillis() - startTime);
        helper.withReturnValue(objectToString(returnValue));

        logAfter();
        helper.removeFullContext();

        return returnValue;
    }

    /**
     *  AfterThrowing advice for handling exceptions.
     *   Adds the collection context to the MDC/NDC context for the exception event to the existing
     *   context already added from the method entry from the {@link #around} method and also conditionally logs the information if
     *   logging is enableLogging for this class.
     */
    after() throwing(Throwable throwable): select() {
        afterThrowing(throwable);
    }

}
