package org.automon.tracing;

import org.apache.logging.log4j.core.LogEvent;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class BasicContextTracingAspectTest extends TestTracingAspectBase {

    @Test
    public void testLogInfo() {
        MyTestClass myTestClass = new MyTestClass();
        myTestClass.name();

        List<LogEvent> logEvents = getListAppender().getEvents();
        assertThat(logEvents).hasSize(6);

        // Define expected log messages (with executionTimeMs replaced)
        String[] expectedMessages = {
                "INFO  o.a.t.BasicContextTracingAspectTest$BasicContext - BEFORE: MDC={NDC0=MyTestClass.name(), kind=method-execution}",
                "INFO  o.a.t.BasicContextTracingAspectTest$BasicContext - BEFORE: MDC={NDC0=MyTestClass.name(), NDC1=MyTestClass.first(..), kind=method-execution}",
                "INFO  o.a.t.BasicContextTracingAspectTest$BasicContext - AFTER: MDC={NDC0=MyTestClass.name(), NDC1=MyTestClass.first(..), executionTimeMs=#, kind=method-execution}",
                "INFO  o.a.t.BasicContextTracingAspectTest$BasicContext - BEFORE: MDC={NDC0=MyTestClass.name(), NDC1=MyTestClass.last(..), kind=method-execution}",
                "INFO  o.a.t.BasicContextTracingAspectTest$BasicContext - AFTER: MDC={NDC0=MyTestClass.name(), NDC1=MyTestClass.last(..), executionTimeMs=#, kind=method-execution}",
                "INFO  o.a.t.BasicContextTracingAspectTest$BasicContext - AFTER: MDC={NDC0=MyTestClass.name(), executionTimeMs=#}"
        };

        assertLogEvents(logEvents, expectedMessages);
    }

    @Test
    public void testLogError() {
        MyTestClass myTestClass = new MyTestClass();
        myTestClass.exceptions();

        List<LogEvent> logEvents = getListAppender().getEvents();
        assertThat(logEvents).hasSize(6);

        // Define expected log messages (with executionTimeMs replaced)
        // Note ERRORS due to methods that throw both RuntimeExceptions and Exceptions (a checked custom exception)
        String[] expectedMessages = {
                "INFO  o.a.t.BasicContextTracingAspectTest$BasicContext - BEFORE: MDC={NDC0=MyTestClass.exceptions(), kind=method-execution}",
                "INFO  o.a.t.BasicContextTracingAspectTest$BasicContext - BEFORE: MDC={NDC0=MyTestClass.exceptions(), NDC1=MyTestClass.checkedException(), kind=method-execution}",
                "ERROR o.a.t.BasicContextTracingAspectTest$BasicContext - AFTER: MDC={NDC0=MyTestClass.exceptions(), NDC1=MyTestClass.checkedException(), exception=org.automon.tracing.MyTestClass.MyException, kind=method-execution}",
                "INFO  o.a.t.BasicContextTracingAspectTest$BasicContext - BEFORE: MDC={NDC0=MyTestClass.exceptions(), NDC1=MyTestClass.runTimeException(), kind=method-execution}",
                "ERROR o.a.t.BasicContextTracingAspectTest$BasicContext - AFTER: MDC={NDC0=MyTestClass.exceptions(), NDC1=MyTestClass.runTimeException(), exception=java.lang.RuntimeException, kind=method-execution}",
                "INFO  o.a.t.BasicContextTracingAspectTest$BasicContext - AFTER: MDC={NDC0=MyTestClass.exceptions(), executionTimeMs=#}"
        };

        assertLogEvents(logEvents, expectedMessages);
    }

    @Aspect
    static class BasicContext extends BasicContextTracingAspect {

        @Pointcut("execution(* org.automon.tracing.MyTestClass.*(..))")
        public void trace() {
        }

    }
}