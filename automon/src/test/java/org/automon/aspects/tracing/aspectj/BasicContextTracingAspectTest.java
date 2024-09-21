package org.automon.aspects.tracing.aspectj;

import org.apache.logging.log4j.core.LogEvent;
import org.aspectj.lang.Aspects;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.aspects.tracing.TestTracingAspectBase;
import org.automon.jmx.TracingMXBean;
import org.automon.utils.AutomonPropertiesLoader;
import org.automon.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class BasicContextTracingAspectTest extends TestTracingAspectBase {

    private static final AutomonPropertiesLoader originalPropertiesLoader = Utils.AUTOMON_PROPERTIES;
    private final BasicContextTracingAspectTest.BasicContext aspect = Aspects.aspectOf(BasicContextTracingAspectTest.BasicContext.class);

    @BeforeEach
    void setUp() {
        reset();
    }

    @AfterEach
    void tearDown() {
        reset();
    }


    private void reset() {
        Utils.AUTOMON_PROPERTIES = originalPropertiesLoader;
        getListAppender().clear();
        aspect.enable(true);
        aspect.enableLogging(true);
    }


    @Test
    void testNoArgConstructor_defaultsToEnabled() {
        BasicContext bc = new BasicContext();

        assertThat(bc.isEnabled()).isTrue();
        assertThat(bc.isEnabled()).isTrue();
        assertThat(bc.isLoggingEnabled()).isTrue();
    }

    @Test
    void testNoArgConstructor_withEnableFromConfigFile() {
        Utils.AUTOMON_PROPERTIES = new AutomonPropertiesLoader("automon.xml");

        BasicContext bc = new BasicContext();

        assertThat(bc.isEnabled()).isFalse();
        assertThat(bc.isEnabled()).isFalse();
        assertThat(bc.isLoggingEnabled()).isFalse();
    }


    @Test
    void testOneParameterizedConstructor_setsEnabledState() {
        BasicContext bc = new BasicContext(true);

        assertThat(bc.isEnabled()).isTrue();
        assertThat(bc.isEnabled()).isTrue();
        assertThat(bc.isLoggingEnabled()).isTrue();

        bc = new BasicContext(false);

        assertThat(bc.isEnabled()).isFalse();
        assertThat(bc.isEnabled()).isFalse();
        assertThat(bc.isLoggingEnabled()).isTrue();
    }

    @Test
    void testTwoParameterizedConstructor_setsEnabledState() {
        BasicContext bc = new BasicContext(true, true);

        assertThat(bc.isEnabled()).isTrue();
        assertThat(bc.isEnabled()).isTrue();
        assertThat(bc.isLoggingEnabled()).isTrue();

        bc = new BasicContext(true, false);

        assertThat(bc.isEnabled()).isTrue();
        assertThat(bc.isEnabled()).isTrue();
        assertThat(bc.isLoggingEnabled()).isFalse();
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

    // Tests for Enable methods (inherited)
    @Test
    void testDefaultEnabled() {
        assertThat(aspect.isEnabled()).isTrue();
    }

    @Test
    void testEnable() {
        aspect.enable(false);
        assertThat(aspect.isEnabled()).isFalse();

        aspect.enable(true);
        assertThat(aspect.isEnabled()).isTrue();
    }


    @Test
    public void testJmxRegistration() throws Throwable {
        BasicContext aspect = new BasicContext();
        TracingMXBean mxBean = Utils.getMxBean(aspect.getPurpose(), aspect, TracingMXBean.class);
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
                "INFO  o.a.a.t.a.BasicContextTracingAspectTest$BasicContext - BEFORE: MDC={NDC0=BasicContextTracingAspectTest.MyTestClass.name(), kind=method-execution}",
                "INFO  o.a.a.t.a.BasicContextTracingAspectTest$BasicContext - BEFORE: MDC={NDC0=BasicContextTracingAspectTest.MyTestClass.name(), NDC1=BasicContextTracingAspectTest.MyTestClass.first(..), kind=method-execution}",
                "INFO  o.a.a.t.a.BasicContextTracingAspectTest$BasicContext - AFTER: MDC={NDC0=BasicContextTracingAspectTest.MyTestClass.name(), NDC1=BasicContextTracingAspectTest.MyTestClass.first(..), executionTimeMs=#, kind=method-execution}",
                "INFO  o.a.a.t.a.BasicContextTracingAspectTest$BasicContext - BEFORE: MDC={NDC0=BasicContextTracingAspectTest.MyTestClass.name(), NDC1=BasicContextTracingAspectTest.MyTestClass.last(..), kind=method-execution}",
                "INFO  o.a.a.t.a.BasicContextTracingAspectTest$BasicContext - AFTER: MDC={NDC0=BasicContextTracingAspectTest.MyTestClass.name(), NDC1=BasicContextTracingAspectTest.MyTestClass.last(..), executionTimeMs=#, kind=method-execution}",
                "INFO  o.a.a.t.a.BasicContextTracingAspectTest$BasicContext - AFTER: MDC={NDC0=BasicContextTracingAspectTest.MyTestClass.name(), executionTimeMs=#}"
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
                "INFO  o.a.a.t.a.BasicContextTracingAspectTest$BasicContext - BEFORE: MDC={NDC0=BasicContextTracingAspectTest.MyTestClass.exceptions(), kind=method-execution}",
                "INFO  o.a.a.t.a.BasicContextTracingAspectTest$BasicContext - BEFORE: MDC={NDC0=BasicContextTracingAspectTest.MyTestClass.exceptions(), NDC1=BasicContextTracingAspectTest.MyTestClass.checkedException(), kind=method-execution}",
                "ERROR o.a.a.t.a.BasicContextTracingAspectTest$BasicContext - AFTER: MDC={NDC0=BasicContextTracingAspectTest.MyTestClass.exceptions(), NDC1=BasicContextTracingAspectTest.MyTestClass.checkedException(), exception=org.automon.aspects.tracing.aspectj.BasicContextTracingAspectTest.MyTestClass.MyException, kind=method-execution}",
                "INFO  o.a.a.t.a.BasicContextTracingAspectTest$BasicContext - BEFORE: MDC={NDC0=BasicContextTracingAspectTest.MyTestClass.exceptions(), NDC1=BasicContextTracingAspectTest.MyTestClass.runTimeException(), kind=method-execution}",
                "ERROR o.a.a.t.a.BasicContextTracingAspectTest$BasicContext - AFTER: MDC={NDC0=BasicContextTracingAspectTest.MyTestClass.exceptions(), NDC1=BasicContextTracingAspectTest.MyTestClass.runTimeException(), exception=java.lang.RuntimeException, kind=method-execution}",
                "INFO  o.a.a.t.a.BasicContextTracingAspectTest$BasicContext - AFTER: MDC={NDC0=BasicContextTracingAspectTest.MyTestClass.exceptions(), executionTimeMs=#}"
        };

        assertLogEvents(logEvents, expectedMessages);
    }

    @Test
    public void testNoLogging() {
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
    public void testDisable() {
        aspect.enable(false);
        MyTestClass myTestClass = new MyTestClass();
        myTestClass.name();
        myTestClass.exceptions();

        List<LogEvent> logEvents = getListAppender().getEvents();
        assertThat(logEvents).
                describedAs("The aspect is disabled and there is an enabled() pointcut so there should be no logging events").
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

        aspect = new BasicContext(true, true);
        assertThat(aspect.isLoggingEnabled()).isTrue();

        aspect = new BasicContext(true, false);
        assertThat(aspect.isLoggingEnabled()).isFalse();
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

        @Pointcut("execution(* org.automon.aspects.tracing.aspectj.BasicContextTracingAspectTest.MyTestClass.*(..))")
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

        private String first(String name) {
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