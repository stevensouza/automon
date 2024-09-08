package org.automon.spring_aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.utils.Utils;

/**
 * Abstract base aspect for basic context tracing using Spring AOP and AspectJ annotations.
 * It provides `around` advice to log method entry, exit, and exceptions,
 * along with basic context information like execution time.
 *
 * <p>If the {@link org.automon.tracing.jmx.TraceJmxControllerMBean#enableLogging(boolean)}
 * is set to false then only the MDC/NDC values will be set but the logging BEFORE/AFTER methods
 * will not be called.  If any logging statements are run within the entered method the MDC/NDC values
 * will be available. The aspect can be completely disabled by calling {@link org.automon.tracing.jmx.TraceJmxControllerMBean#enable(boolean)}
 * with a false value.
 * </p>
 *
 * <p>Subclasses need to implement the `trace()` pointcut to define the pointcuts to be traced.</p>
 */
@Aspect
public abstract class BasicContextTracingAspect extends TracingAspect {

    /**
     * Constructs a new `BasicContextTracingAspect` with tracing and logging enabled states
     * determined by the `Utils.shouldEnable` and `Utils.shouldEnableLogging` methods, respectively.
     * The purpose is set to "trace_log_basic_context" and the JMX controller is registered.
     */
    public BasicContextTracingAspect() {
        this(Utils.shouldEnable(BasicContextTracingAspect.class.getName()),
                Utils.shouldEnableLogging(BasicContextTracingAspect.class.getName())
        );
    }

    /**
     * Constructs a new `BasicContextTracingAspect` with the specified tracing enabled state
     * and logging enabled by default. The purpose is set to "trace_log_basic_context" and
     * the JMX controller is registered.
     *
     * @param enable `true` to enable tracing, `false` to disable tracing.
     */
    public BasicContextTracingAspect(boolean enable) {
        this(enable, true);
    }

    /**
     * Constructs a new `BasicContextTracingAspect` with the specified tracing and logging enabled states.
     * The purpose is set to "trace_log_basic_context" and the JMX controller is registered.
     *
     * @param enable        `true` to enable tracing, `false` to disable tracing.
     * @param enableLogging `true` to enable logging, `false` to disable logging.
     */
    public BasicContextTracingAspect(boolean enable, boolean enableLogging) {
        super(enable, enableLogging);
        setPurpose("trace_log_basic_context");
        registerJmxController();
    }

    /**
     * Abstract pointcut to be defined in concrete subaspects.
     * This pointcut determines which methods will be traced.
     */
    @Pointcut
    public abstract void trace();


    /**
     * Around advice for tracing method execution.
     * Adds NDC/MDC context on method entry and exit, along with other basic context information such as execution time.
     * The information is conditionally logged if {@link #enableLogging(boolean)} is set.
     * See {@link org.automon.utils.LogTracingHelper#withBasicContext(JoinPoint.StaticPart, JoinPoint.StaticPart)}
     * for additional context being traced. Example output which uses SLF4J's MDC and NDC.
     * <p>
     * 2024-08-18 10:28:35,809 INFO  c.s.a.l.a.b.BasicContextTracingAspect - BEFORE: MDC={NDC0=MyLoggerClassBasic.main(..), NDC1=MyLoggerClassBasic.method1(), NDC2=MyLoggerClassBasic.nameMethod2(..), NDC3=MyLoggerClassBasic.occupationMethod3(..), kind=method-execution}
     * 2024-08-18 10:28:35,809 INFO  c.s.a.l.a.b.BasicContextTracingAspect - AFTER: MDC={NDC0=MyLoggerClassBasic.main(..), NDC1=MyLoggerClassBasic.method1(), NDC2=MyLoggerClassBasic.nameMethod2(..), NDC3=MyLoggerClassBasic.occupationMethod3(..), executionTimeMs=122, kind=method-execution}
     * </p>
     *
     * <p>Note this class only cleans up NDC/MDC that it added.  Also note if an exception is thrown the MDC/NDC
     * will be cleaned up and an AFTER log line will be written in the 'after throwing' advice.
     * </p>
     *
     * @return The result of the advised method execution.
     * @throws Throwable If the advised method throws an exception, it is re-thrown after logging.
     */
    @Around(value = "trace()")
    public Object around(ProceedingJoinPoint jp) throws Throwable {
        helper.withSignature(jp.getStaticPart()).
                withKind(jp.getStaticPart());
//        helper.withBasicContext(jp.getStaticPart(), jp.getStaticPart());
        logBefore();

        long startTime = System.currentTimeMillis();
        Object returnValue = jp.proceed();
        helper.withExecutionTime(System.currentTimeMillis() - startTime);

        logAfter();
        helper.removeBasicContext();

        return returnValue;
    }

}