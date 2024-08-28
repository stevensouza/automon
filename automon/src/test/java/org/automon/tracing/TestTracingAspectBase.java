package org.automon.tracing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.aspectj.lang.Aspects;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestTracingAspectBase {
    protected static void assertLogEvents(List<LogEvent> logEvents, String[] expectedMessages) {
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
    protected static ListAppender getListAppender() {
        // Get the LoggerContext (assuming Log4j 2 as the underlying logging implementation)
        LoggerContext context = (LoggerContext) LogManager.getContext(false);

        // Get the Configuration from the LoggerContext
        Configuration configuration = context.getConfiguration();

        // Get the appender by name from the Configuration
        Appender appender = configuration.getAppender("ListAppender");

        // Cast the Appender to ListAppender (assuming it's the correct type)
        return (ListAppender) appender;
    }

    @BeforeEach
    void setUp() {
        reset();
    }

    @AfterEach
    void tearDown() {
        reset();
    }

    private static void reset() {
        TestTracingAspectBase.getListAppender().clear();
        BasicContextTracingAspectTest.BasicContext aspect = Aspects.aspectOf(BasicContextTracingAspectTest.BasicContext.class);
        aspect.enableLogging(true);
    }
/*

    @Test
    void testDefaultLoggingEnabled() {
        assertThat(aspect.isLoggingEnabled()).isTrue();
    }

    @Test
    void testEnableLogging() {
        aspect.enableLogging(false);
        assertThat(aspect.isLoggingEnabled()).isFalse();

        aspect.enableLogging(true);
        assertThat(aspect.isLoggingEnabled()).isTrue();
    }

    // Tests for AspectControl methods (inherited)
    @Test
    public void testDefaultEnabled() {
        Assertions.assertThat(aspect.isEnabled()).isTrue(); // Inherited from AspectControl
    }

    @Test
    public void testEnable() {
        aspect.enable(false);
        Assertions.assertThat(aspect.isEnabled()).isFalse();

        aspect.enable(true);
        Assertions.assertThat(aspect.isEnabled()).isTrue();
    }
*/


}
