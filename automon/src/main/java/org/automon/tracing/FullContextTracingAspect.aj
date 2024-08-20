package org.automon.tracing;

import org.aspectj.lang.JoinPoint;

/**
 * Aspect for full context tracing using AOP (Aspect-Oriented Programming).
 * It provides `around` and `afterThrowing` advice to log method entry, exit, and exceptions,
 * along with full context information like execution time.
 *
 * <p>Subclasses need to implement the `trace()` pointcut to define the pointcuts to be traced.</p>
 */
public abstract aspect FullContextTracingAspect extends TracingAspect {
    
    /**
     * Around advice for tracing method execution.
     * Logs method entry and exit, along with basic context information such as execution time.
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
    Object around() : trace() {
        helper.withFullContext(thisJoinPoint, thisJoinPointStaticPart, thisEnclosingJoinPointStaticPart);
        LOGGER.info(BEFORE);

        long startTime = System.currentTimeMillis();
        Object returnValue =  proceed();
        helper.withExecutionTime(System.currentTimeMillis() - startTime);
        helper.withReturnValue(objectToString(returnValue));

        LOGGER.info(AFTER);
        helper.removeFullContext();

        return returnValue;
    }

}