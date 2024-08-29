package org.automon.tracing;

import org.apache.logging.log4j.core.LogEvent;
import org.aspectj.lang.Aspects;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class BasicContextTracingAspectTest extends TestTracingAspectBase {

    @BeforeEach
    void setUp() {
        reset();
    }

    @AfterEach
    void tearDown() {
        reset();
    }

    private BasicContext aspect = null;

    private void reset() {
        getListAppender().clear();
        aspect = Aspects.aspectOf(BasicContext.class);
        aspect.enable(true);
        aspect.enableLogging(true);
    }

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

    // Tests for AspectJmxController methods (inherited)
    @Test
    void testDefaultEnabled() {
        assertThat(aspect.isEnabled()).isTrue(); // Inherited from AspectJmxController
    }

    @Test
    void testEnable() {
        aspect.enable(false);
        assertThat(aspect.isEnabled()).isFalse();

        aspect.enable(true);
        assertThat(aspect.isEnabled()).isTrue();
    }

    @Test
    public void testLogInfo() {
        MyTestClass myTestClass = new MyTestClass();
        myTestClass.name();

        List<LogEvent> logEvents = getListAppender().getEvents();
        assertThat(logEvents).hasSize(6);

        // Define expected log messages (with executionTimeMs replaced)
        String[] expectedMessages = {
                "INFO  o.a.t.BasicContextTracingAspectTest$BasicContext - BEFORE: MDC={NDC0=BasicContextTracingAspectTest.MyTestClass.name(), kind=method-execution}",
                "INFO  o.a.t.BasicContextTracingAspectTest$BasicContext - BEFORE: MDC={NDC0=BasicContextTracingAspectTest.MyTestClass.name(), NDC1=BasicContextTracingAspectTest.MyTestClass.first(..), kind=method-execution}",
                "INFO  o.a.t.BasicContextTracingAspectTest$BasicContext - AFTER: MDC={NDC0=BasicContextTracingAspectTest.MyTestClass.name(), NDC1=BasicContextTracingAspectTest.MyTestClass.first(..), executionTimeMs=#, kind=method-execution}",
                "INFO  o.a.t.BasicContextTracingAspectTest$BasicContext - BEFORE: MDC={NDC0=BasicContextTracingAspectTest.MyTestClass.name(), NDC1=BasicContextTracingAspectTest.MyTestClass.last(..), kind=method-execution}",
                "INFO  o.a.t.BasicContextTracingAspectTest$BasicContext - AFTER: MDC={NDC0=BasicContextTracingAspectTest.MyTestClass.name(), NDC1=BasicContextTracingAspectTest.MyTestClass.last(..), executionTimeMs=#, kind=method-execution}",
                "INFO  o.a.t.BasicContextTracingAspectTest$BasicContext - AFTER: MDC={NDC0=BasicContextTracingAspectTest.MyTestClass.name(), executionTimeMs=#}"
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
                "INFO  o.a.t.BasicContextTracingAspectTest$BasicContext - BEFORE: MDC={NDC0=BasicContextTracingAspectTest.MyTestClass.exceptions(), kind=method-execution}",
                "INFO  o.a.t.BasicContextTracingAspectTest$BasicContext - BEFORE: MDC={NDC0=BasicContextTracingAspectTest.MyTestClass.exceptions(), NDC1=BasicContextTracingAspectTest.MyTestClass.checkedException(), kind=method-execution}",
                "ERROR o.a.t.BasicContextTracingAspectTest$BasicContext - AFTER: MDC={NDC0=BasicContextTracingAspectTest.MyTestClass.exceptions(), NDC1=BasicContextTracingAspectTest.MyTestClass.checkedException(), exception=org.automon.tracing.BasicContextTracingAspectTest.MyTestClass.MyException, kind=method-execution}",
                "INFO  o.a.t.BasicContextTracingAspectTest$BasicContext - BEFORE: MDC={NDC0=BasicContextTracingAspectTest.MyTestClass.exceptions(), NDC1=BasicContextTracingAspectTest.MyTestClass.runTimeException(), kind=method-execution}",
                "ERROR o.a.t.BasicContextTracingAspectTest$BasicContext - AFTER: MDC={NDC0=BasicContextTracingAspectTest.MyTestClass.exceptions(), NDC1=BasicContextTracingAspectTest.MyTestClass.runTimeException(), exception=java.lang.RuntimeException, kind=method-execution}",
                "INFO  o.a.t.BasicContextTracingAspectTest$BasicContext - AFTER: MDC={NDC0=BasicContextTracingAspectTest.MyTestClass.exceptions(), executionTimeMs=#}"
        };

        assertLogEvents(logEvents, expectedMessages);
    }

    @Test
    public void testNoLogging() {
        BasicContextTracingAspectTest.BasicContext aspect = Aspects.aspectOf(BasicContextTracingAspectTest.BasicContext.class);
        aspect.enableLogging(false);

        MyTestClass myTestClass = new MyTestClass();
        myTestClass.name();
        myTestClass.exceptions();

        List<LogEvent> logEvents = getListAppender().getEvents();
        assertThat(logEvents).
                describedAs("Logging is disabled so there should be no logging events").
                hasSize(0);

    }

    @Test
    public void testEnableDisable() {
        BasicContext aspect = new BasicContext();
        assertThat(aspect.isLoggingEnabled()).isTrue();

        aspect.enableLogging(false);
        assertThat(aspect.isLoggingEnabled()).isFalse();
        aspect.enableLogging(true);
        assertThat(aspect.isLoggingEnabled()).isTrue();

        aspect = new BasicContext(true);
        assertThat(aspect.isLoggingEnabled()).isTrue();

        aspect = new BasicContext(false);
        assertThat(aspect.isLoggingEnabled()).isFalse();
    }


    @Aspect
    static class BasicContext extends BasicContextTracingAspect {

        public BasicContext() {
            super();
            getJmxController().enable(true);
        }

        public BasicContext(boolean enable) {
            super(enable);
        }

        @Pointcut("enabled() && execution(* org.automon.tracing.BasicContextTracingAspectTest.MyTestClass.*(..))")
        public void trace() {
        }
    }

    public static class MyTestClass {

        public String name() {
            return "name: " + first("steve") + " " + last("souza");
        }

        public void exceptions() {
            try {
                checkedException();
            } catch (Exception e) {
                // gobble
            }

            try {
                runTimeException();
            } catch (RuntimeException e) {
                // gobble
            }
        }

        String first(String name) {
            return name;
        }

        String last(String name) {
            return name;
        }

        void runTimeException() {
            throw new RuntimeException("runTimeException");
        }

        void checkedException() throws Exception {
            throw new MyException("checkedException");
        }

        public static class MyException extends Exception {
            public MyException(String message) {
                super(message);
            }
        }


    }
}