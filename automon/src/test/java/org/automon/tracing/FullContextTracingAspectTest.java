package org.automon.tracing;

import org.apache.logging.log4j.core.LogEvent;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class FullContextTracingAspectTest extends TestTracingAspectBase {

    @Test
    public void testLogInfo() {
        MyTestClass2 myTestClass = new MyTestClass2();
        myTestClass.name();

        List<LogEvent> logEvents = getListAppender().getEvents();
        assertThat(logEvents).hasSize(6);

        // Define expected log messages (with executionTimeMs replaced)
        String[] expectedMessages = {
                "INFO  o.a.t.FullContextTracingAspectTest$FullContext - BEFORE: MDC={NDC0=MyTestClass2.name(), enclosingSignature=MyTestClass2.name(), kind=method-execution, parameters={}, target=MyTestClass2.toString(), this=MyTestClass2.toString()}",
                "INFO  o.a.t.FullContextTracingAspectTest$FullContext - BEFORE: MDC={NDC0=MyTestClass2.name(), NDC1=MyTestClass2.first(..), enclosingSignature=MyTestClass2.first(..), kind=method-execution, parameters={name=steve}, target=MyTestClass2.toString(), this=MyTestClass2.toString()}",
                "INFO  o.a.t.FullContextTracingAspectTest$FullContext - AFTER: MDC={NDC0=MyTestClass2.name(), NDC1=MyTestClass2.first(..), enclosingSignature=MyTestClass2.first(..), executionTimeMs=#, kind=method-execution, parameters={name=steve}, returnValue=steve, target=MyTestClass2.toString(), this=MyTestClass2.toString()}",
                "INFO  o.a.t.FullContextTracingAspectTest$FullContext - BEFORE: MDC={NDC0=MyTestClass2.name(), NDC1=MyTestClass2.last(..), enclosingSignature=MyTestClass2.last(..), kind=method-execution, parameters={name=souza}, target=MyTestClass2.toString(), this=MyTestClass2.toString()}",
                "INFO  o.a.t.FullContextTracingAspectTest$FullContext - AFTER: MDC={NDC0=MyTestClass2.name(), NDC1=MyTestClass2.last(..), enclosingSignature=MyTestClass2.last(..), executionTimeMs=#, kind=method-execution, parameters={name=souza}, returnValue=souza, target=MyTestClass2.toString(), this=MyTestClass2.toString()}",
                "INFO  o.a.t.FullContextTracingAspectTest$FullContext - AFTER: MDC={NDC0=MyTestClass2.name(), executionTimeMs=#, returnValue=name: steve souza}"
        };

        assertLogEvents(logEvents, expectedMessages);
    }

    @Test
    public void testLogError() {
        MyTestClass2 myTestClass = new MyTestClass2();
        myTestClass.exceptions();

        List<LogEvent> logEvents = getListAppender().getEvents();
        assertThat(logEvents).hasSize(6);

        // Define expected log messages (with executionTimeMs replaced)
        // Note ERRORS due to methods that throw both RuntimeExceptions and Exceptions (a checked custom exception)
        String[] expectedMessages = {
                "INFO  o.a.t.FullContextTracingAspectTest$FullContext - BEFORE: MDC={NDC0=MyTestClass2.exceptions(), enclosingSignature=MyTestClass2.exceptions(), kind=method-execution, parameters={}, target=MyTestClass2.toString(), this=MyTestClass2.toString()}",
                "INFO  o.a.t.FullContextTracingAspectTest$FullContext - BEFORE: MDC={NDC0=MyTestClass2.exceptions(), NDC1=MyTestClass2.checkedException(..), enclosingSignature=MyTestClass2.checkedException(..), kind=method-execution, parameters={fname=jeff}, target=MyTestClass2.toString(), this=MyTestClass2.toString()}",
                "ERROR o.a.t.FullContextTracingAspectTest$FullContext - AFTER: MDC={NDC0=MyTestClass2.exceptions(), NDC1=MyTestClass2.checkedException(..), enclosingSignature=MyTestClass2.checkedException(..), exception=org.automon.tracing.MyTestClass2.MyException, kind=method-execution, parameters={fname=jeff}, target=MyTestClass2.toString(), this=MyTestClass2.toString()}",
                "INFO  o.a.t.FullContextTracingAspectTest$FullContext - BEFORE: MDC={NDC0=MyTestClass2.exceptions(), NDC1=MyTestClass2.runTimeException(..), enclosingSignature=MyTestClass2.runTimeException(..), kind=method-execution, parameters={lname=beck}, target=MyTestClass2.toString(), this=MyTestClass2.toString()}",
                "ERROR o.a.t.FullContextTracingAspectTest$FullContext - AFTER: MDC={NDC0=MyTestClass2.exceptions(), NDC1=MyTestClass2.runTimeException(..), enclosingSignature=MyTestClass2.runTimeException(..), exception=java.lang.RuntimeException, kind=method-execution, parameters={lname=beck}, target=MyTestClass2.toString(), this=MyTestClass2.toString()}",
                "INFO  o.a.t.FullContextTracingAspectTest$FullContext - AFTER: MDC={NDC0=MyTestClass2.exceptions(), executionTimeMs=#, returnValue=2}"
        };

        assertLogEvents(logEvents, expectedMessages);
    }

    @Aspect
    static class FullContext extends FullContextTracingAspect {

        @Pointcut("execution(* org.automon.tracing.MyTestClass2.*(..)) && !execution(* org.automon.tracing.MyTestClass2.toString())")
        public void trace() {
        }

    }
}