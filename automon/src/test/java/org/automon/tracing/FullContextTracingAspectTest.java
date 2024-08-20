package org.automon.tracing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class FullContextTracingAspectTest {

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
        getListAppender().clear();
    }

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

    private static void assertLogEvents(List<LogEvent> logEvents, String[] expectedMessages) {
        System.out.println("1***");
        for (int i = 0; i < logEvents.size(); i++) {
            // executionTime=105 can vary so normalizing it for the assertions to executionTime=#
            String actualMessage = logEvents.get(i).getMessage().getFormattedMessage();
            actualMessage = actualMessage.replaceAll("executionTimeMs=\\d+", "executionTimeMs=#");

            System.out.println(actualMessage);
            assertThat(actualMessage).
                    describedAs("Each log event should start with this text (excludes ending newlines in check)").
                    startsWith(expectedMessages[i]);
        }
        System.out.println("2***");

    }

    /**
     * Retrieves the `ListAppender` with the name "ListAppender" from the Log4j 2 configuration.
     *
     * @return The `ListAppender` instance, or `null` if not found.
     */
    private static ListAppender getListAppender() {
        // Get the LoggerContext (assuming Log4j 2 as the underlying logging implementation)
        LoggerContext context = (LoggerContext) LogManager.getContext(false);

        // Get the Configuration from the LoggerContext
        Configuration configuration = context.getConfiguration();

        // Get the appender by name from the Configuration
        Appender appender = configuration.getAppender("ListAppender");

        // Cast the Appender to ListAppender (assuming it's the correct type)
        return (ListAppender) appender;
    }

    @Aspect
    static class FullContext extends FullContextTracingAspect {

        @Pointcut("execution(* org.automon.tracing.MyTestClass2.*(..)) && !execution(* org.automon.tracing.MyTestClass2.toString())")
        public void trace() {
        }

    }
}