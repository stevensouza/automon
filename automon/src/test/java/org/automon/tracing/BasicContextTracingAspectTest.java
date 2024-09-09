package org.automon.tracing;

import org.apache.logging.log4j.core.LogEvent;
import org.aspectj.lang.Aspects;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.tracing.jmx.TraceJmxController;
import org.automon.tracing.jmx.TraceJmxControllerMBean;
import org.automon.utils.AutomonPropertiesLoader;
import org.automon.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class BasicContextTracingAspectTest extends TestTracingAspectBase {

    private static final AutomonPropertiesLoader originalPropertiesLoader = Utils.AUTOMON_PROPERTIES;

    @BeforeEach
    void setUp() {
        reset();
    }

    @AfterEach
    void tearDown() {
        reset();
    }

    private TraceJmxController getJmx() {
        return BasicContext.getJmxController();
    }

    private void reset() {
        Utils.AUTOMON_PROPERTIES = originalPropertiesLoader;
        getListAppender().clear();
        getJmx().enable(true);
        getJmx().enableLogging(true);
    }


    @Test
    void testNoArgConstructor_defaultsToEnabled() {
        BasicContext bc = new BasicContext();

        assertThat(BasicContext.isEnabled()).isTrue();
        assertThat(BasicContext.getJmxController().isEnabled()).isTrue();
        assertThat(BasicContext.getJmxController().isLoggingEnabled()).isTrue();
    }

    @Test
    void testNoArgConstructor_withEnableFromConfigFile() {
        Utils.AUTOMON_PROPERTIES = new AutomonPropertiesLoader("automon.xml");

        BasicContext bc = new BasicContext();

        assertThat(BasicContext.isEnabled()).isFalse();
        assertThat(BasicContext.getJmxController().isEnabled()).isFalse();
        assertThat(BasicContext.getJmxController().isLoggingEnabled()).isFalse();
    }


    @Test
    void testOneParameterizedConstructor_setsEnabledState() {
        BasicContext fcd = new BasicContext(true);

        assertThat(BasicContext.isEnabled()).isTrue();
        assertThat(BasicContext.getJmxController().isEnabled()).isTrue();
        assertThat(BasicContext.getJmxController().isLoggingEnabled()).isTrue();

        fcd = new BasicContext(false);

        assertThat(BasicContext.isEnabled()).isFalse();
        assertThat(BasicContext.getJmxController().isEnabled()).isFalse();
        assertThat(BasicContext.getJmxController().isLoggingEnabled()).isTrue();
    }

    @Test
    void testTwoParameterizedConstructor_setsEnabledState() {
        BasicContext fcd = new BasicContext(true, true);

        assertThat(BasicContext.isEnabled()).isTrue();
        assertThat(BasicContext.getJmxController().isEnabled()).isTrue();
        assertThat(BasicContext.getJmxController().isLoggingEnabled()).isTrue();

        fcd = new BasicContext(true, false);

        assertThat(BasicContext.isEnabled()).isTrue();
        assertThat(BasicContext.getJmxController().isEnabled()).isTrue();
        assertThat(BasicContext.getJmxController().isLoggingEnabled()).isFalse();
    }

    @Test
    void testDefaultLoggingEnabled() {
        assertThat(getJmx().isLoggingEnabled()).isTrue();
    }

    @Test
    void testEnableLogging() {
        getJmx().enableLogging(false);
        assertThat(getJmx().isLoggingEnabled()).isFalse();

        getJmx().enableLogging(true);
        assertThat(getJmx().isLoggingEnabled()).isTrue();
    }

    // Tests for AspectJmxController methods (inherited)
    @Test
    void testDefaultEnabled() {
        assertThat(getJmx().isEnabled()).isTrue(); // Inherited from AspectJmxController
    }

    @Test
    void testEnable() {
        getJmx().enable(false);
        assertThat(getJmx().isEnabled()).isFalse();

        getJmx().enable(true);
        assertThat(getJmx().isEnabled()).isTrue();
    }


    @Test
    public void testJmxRegistration() throws Throwable {
        BasicContext aspect = new BasicContext();
        TraceJmxControllerMBean mxBean = Utils.getMxBean(aspect.getPurpose(), aspect, TraceJmxControllerMBean.class);
        assertThat(mxBean.isEnabled()).describedAs("Should be enabled").isTrue();
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
        getJmx().enableLogging(false);

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
        assertThat(getJmx().isLoggingEnabled()).isTrue();

        getJmx().enableLogging(false);
        assertThat(getJmx().isLoggingEnabled()).isFalse();
        getJmx().enableLogging(true);
        assertThat(getJmx().isLoggingEnabled()).isTrue();

        aspect = new BasicContext(true, true);
        assertThat(getJmx().isLoggingEnabled()).isTrue();

        aspect = new BasicContext(true, false);
        assertThat(getJmx().isLoggingEnabled()).isFalse();
    }


    @Aspect
    static class BasicContext extends BasicContextTracingAspect {

        // No-arg constructor
        public BasicContext() {
        }

        // Single-argument constructor (enable tracing)
        public BasicContext(boolean enable) {
            super(enable, true); // Default to logging enabled
        }

        // Two-argument constructor (enable tracing and logging)
        public BasicContext(boolean enable, boolean enableLogging) {
            super(enable, enableLogging);
        }

        @Pointcut("enabled() && execution(* org.automon.tracing.BasicContextTracingAspectTest.MyTestClass.*(..))")
        public void select() {
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