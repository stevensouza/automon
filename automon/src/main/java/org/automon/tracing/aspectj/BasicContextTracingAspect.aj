package org.automon.tracing.aspectj;

import org.aspectj.lang.JoinPoint;
import org.automon.tracing.BaseTracingAspect;
import org.automon.utils.Utils;

/**
 * <p>Aspect for basic context tracing using AOP (Aspect-Oriented Programming).
 * It provides `around` and `afterThrowing` advice to log method entry, exit, and exceptions,
 * along with basic context information like execution time.
 * </p>
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
public privileged abstract aspect BasicContextTracingAspect extends BaseTracingAspect {
    static final String PURPOSE = "trace_log_basic_context_native";

    /**
     * Constructs a new `BasicContextTracingAspect` with both tracing and logging enabled by default.
     */
    public BasicContextTracingAspect() {
        this(Utils.shouldEnable(BasicContextTracingAspect.class.getName()),
                Utils.shouldEnableLogging(BasicContextTracingAspect.class.getName())
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
     * Adds NDC/MDC context on method entry and exit, along with other basic context information such as execution time.
     * The information is conditionally logged if {@link #enableLogging(boolean)} is set.
     * See {@link org.automon.utils.LogTracingHelper#withBasicContext(JoinPoint.StaticPart, JoinPoint.StaticPart)}
     * for additional context being traced. Example output which uses SLF4J's MDC and NDC.
     *       <p>
     *         2024-08-18 10:28:35,809 INFO  c.s.a.l.a.b.BasicContextTracingAspect - BEFORE: MDC={NDC0=MyLoggerClassBasic.main(..), NDC1=MyLoggerClassBasic.method1(), NDC2=MyLoggerClassBasic.nameMethod2(..), NDC3=MyLoggerClassBasic.occupationMethod3(..), kind=method-execution}
     *         2024-08-18 10:28:35,809 INFO  c.s.a.l.a.b.BasicContextTracingAspect - AFTER: MDC={NDC0=MyLoggerClassBasic.main(..), NDC1=MyLoggerClassBasic.method1(), NDC2=MyLoggerClassBasic.nameMethod2(..), NDC3=MyLoggerClassBasic.occupationMethod3(..), executionTimeMs=122, kind=method-execution}
     *       </p>
     *
     * <p>Note this class only cleans up NDC/MDC that it added.  Also note if an exception is thrown the MDC/NDC
     *  will be cleaned up and an AFTER log line will be written in the 'after throwing' advice.
     *  </p>
     *
     * @return The result of the advised method execution.
     * @throws Throwable If the advised method throws an exception, it is re-thrown after logging.
     */
    Object around(): select() {
        helper.withBasicContext(thisJoinPointStaticPart, thisEnclosingJoinPointStaticPart);
        logBefore();

        long startTime = System.currentTimeMillis();
        Object returnValue = proceed();
        helper.withExecutionTime(System.currentTimeMillis() - startTime);

        logAfter();
        helper.removeBasicContext();

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
