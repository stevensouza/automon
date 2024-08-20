package org.automon.tracing;

import org.aspectj.lang.JoinPoint;

/**
 * Aspect for basic context tracing using AOP (Aspect-Oriented Programming).
 * It provides `around` and `afterThrowing` advice to log method entry, exit, and exceptions,
 * along with basic context information like execution time.
 *
 * <p>Subclasses need to implement the `trace()` pointcut to define the pointcuts to be traced.</p>
 */
public abstract aspect BasicContextTracingAspect extends TracingAspect {

    /**
     * Around advice for tracing method execution.
     * Logs method entry and exit, along with basic context information such as execution time.
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
    Object around() : trace() {
            helper.withBasicContext(thisJoinPointStaticPart, thisEnclosingJoinPointStaticPart);
            LOGGER.info(BEFORE);

            long startTime = System.currentTimeMillis();
            Object returnValue =  proceed();
            helper.withExecutionTime(System.currentTimeMillis() - startTime);

            LOGGER.info(AFTER);
            helper.removeBasicContext();

            return returnValue;
    }


}