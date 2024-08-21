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

class RequestIdAspectTest {

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
        getListAppender().clear();
    }

    @Test
    public void testRequestID() {
        MyRequestTestClass myTestClass = new MyRequestTestClass();
        myTestClass.firstName("steve");
        final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());
        LOGGER.info("MDC={requestId=#UUID should now be removed");

        List<LogEvent> logEvents = getListAppender().getEvents();
        assertThat(logEvents).hasSize(3);

        // Define expected log messages up to requestId removed as it is always a unique UUID
        String[] expectedMessages = {
                "INFO  o.a.t.MyRequestTestClass - In MyRequestTestClass.firstName(..) method: MDC={requestId=",
                "INFO  o.a.t.MyRequestTestClass - In MyRequestTestClass.hi() method: MDC={requestId=",
                "INFO  o.a.t.RequestIdAspectTest - MDC={requestId=#UUID should now be removed: MDC={}"
        };

        assertLogEvents(logEvents, expectedMessages);
    }


    private static void assertLogEvents(List<LogEvent> logEvents, String[] expectedMessages) {
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
    static class RequestId extends RequestIdAspect {

        @Pointcut("execution(* org.automon.tracing.MyRequestTestClass.firstName(..))")
        public void requestStart() {
        }

    }

}