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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class BasicContextTracingAspectTest {

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void testLogMessages() {
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

        for (int i = 0; i < logEvents.size(); i++) {
            // executionTime=105 can vary so normalizing it for the assertions to executionTime=#
            String actualMessage = logEvents.get(i).getMessage().getFormattedMessage();
            actualMessage = actualMessage.replaceAll("executionTimeMs=\\d+", "executionTimeMs=#");

            assertThat(actualMessage).
                    describedAs("Each log event should start with this text (excludes ending newlines in check)").
                    startsWith(expectedMessages[i]);
        }
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
    static class BasicContext extends BasicContextTracingAspect {

        @Pointcut("execution(* org.automon.tracing.MyTestClass.*(..))")
        public void trace() {
        }

    }
}