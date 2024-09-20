package org.automon.tracing.spring;

import org.apache.logging.log4j.core.LogEvent;
import org.aspectj.lang.Aspects;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.tracing.TestTracingAspectBase;
import org.automon.jmx.TraceJmxController;
import org.automon.jmx.TraceJmxControllerMBean;
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
        BasicContext aspect = Aspects.aspectOf(BasicContext.class);
        return aspect.getJmxController();
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

        assertThat(bc.isEnabled()).isTrue();
        assertThat(bc.getJmxController().isEnabled()).isTrue();
        assertThat(bc.getJmxController().isLoggingEnabled()).isTrue();
    }

    @Test
    void testNoArgConstructor_withEnableFromConfigFile() {
        Utils.AUTOMON_PROPERTIES = new AutomonPropertiesLoader("automon.xml");

        BasicContext bc = new BasicContext();

        assertThat(bc.isEnabled()).isFalse();
        assertThat(bc.getJmxController().isEnabled()).isFalse();
        assertThat(bc.getJmxController().isLoggingEnabled()).isFalse();
    }


    @Test
    void testOneParameterizedConstructor_setsEnabledState() {
        BasicContext bc = new BasicContext(true);

        assertThat(bc.isEnabled()).isTrue();
        assertThat(bc.getJmxController().isEnabled()).isTrue();
        assertThat(bc.getJmxController().isLoggingEnabled()).isTrue();

        bc = new BasicContext(false);

        assertThat(bc.isEnabled()).isFalse();
        assertThat(bc.getJmxController().isEnabled()).isFalse();
        assertThat(bc.getJmxController().isLoggingEnabled()).isTrue();
    }

    @Test
    void testTwoParameterizedConstructor_setsEnabledState() {
        BasicContext bc = new BasicContext(true, true);

        assertThat(bc.isEnabled()).isTrue();
        assertThat(bc.getJmxController().isEnabled()).isTrue();
        assertThat(bc.getJmxController().isLoggingEnabled()).isTrue();

        bc = new BasicContext(true, false);

        assertThat(bc.isEnabled()).isTrue();
        assertThat(bc.getJmxController().isEnabled()).isTrue();
        assertThat(bc.getJmxController().isLoggingEnabled()).isFalse();
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
        assertThat(logEvents).hasSize(8);

        // Define expected log messages (with executionTimeMs replaced)
        String[] expectedMessages = {
                "INFO  o.a.t.s.BasicContextTracingAspectTest$BasicContext - BEFORE: MDC={NDC0=MyTestClass.name(), kind=method-execution}",
                "INFO  o.a.t.s.BasicContextTracingAspectTest$BasicContext - BEFORE: MDC={NDC0=MyTestClass.name(), NDC1=MyTestClass.first(..), kind=method-execution}",
                "INFO  o.a.t.s.BasicContextTracingAspectTest$BasicContext - BEFORE: MDC={NDC0=MyTestClass.name(), NDC1=MyTestClass.first(..), NDC2=MyTestClass.name, kind=field-set}",
                "INFO  o.a.t.s.BasicContextTracingAspectTest$BasicContext - AFTER: MDC={NDC0=MyTestClass.name(), NDC1=MyTestClass.first(..), NDC2=MyTestClass.name, executionTimeMs=#, kind=field-set}",
                "INFO  o.a.t.s.BasicContextTracingAspectTest$BasicContext - AFTER: MDC={NDC0=MyTestClass.name(), NDC1=MyTestClass.first(..), executionTimeMs=#}",
                "INFO  o.a.t.s.BasicContextTracingAspectTest$BasicContext - BEFORE: MDC={NDC0=MyTestClass.name(), NDC1=MyTestClass.last(..), kind=method-execution}",
                "INFO  o.a.t.s.BasicContextTracingAspectTest$BasicContext - AFTER: MDC={NDC0=MyTestClass.name(), NDC1=MyTestClass.last(..), executionTimeMs=#, kind=method-execution}",
                "INFO  o.a.t.s.BasicContextTracingAspectTest$BasicContext - AFTER: MDC={NDC0=MyTestClass.name(), executionTimeMs=#}"
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
                "INFO  o.a.t.s.BasicContextTracingAspectTest$BasicContext - BEFORE: MDC={NDC0=MyTestClass.exceptions(), kind=method-execution}",
                "INFO  o.a.t.s.BasicContextTracingAspectTest$BasicContext - BEFORE: MDC={NDC0=MyTestClass.exceptions(), NDC1=MyTestClass.checkedException(), kind=method-execution}",
                "ERROR o.a.t.s.BasicContextTracingAspectTest$BasicContext - AFTER: MDC={NDC0=MyTestClass.exceptions(), NDC1=MyTestClass.checkedException(), exception=org.automon.tracing.spring.MyTestClass.MyException, kind=method-execution}",
                "INFO  o.a.t.s.BasicContextTracingAspectTest$BasicContext - BEFORE: MDC={NDC0=MyTestClass.exceptions(), NDC1=MyTestClass.runTimeException(), kind=method-execution}",
                "ERROR o.a.t.s.BasicContextTracingAspectTest$BasicContext - AFTER: MDC={NDC0=MyTestClass.exceptions(), NDC1=MyTestClass.runTimeException(), exception=java.lang.RuntimeException, kind=method-execution}",
                "INFO  o.a.t.s.BasicContextTracingAspectTest$BasicContext - AFTER: MDC={NDC0=MyTestClass.exceptions(), executionTimeMs=#}"
        };

        assertLogEvents(logEvents, expectedMessages);
    }

    @Test
    public void testNoLogging() {
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
    public void testDisable() {
        getJmx().enable(false);
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
        assertThat(aspect.getJmxController().isLoggingEnabled()).isTrue();

        aspect.getJmxController().enableLogging(false);
        assertThat(aspect.getJmxController().isLoggingEnabled()).isFalse();
        aspect.getJmxController().enableLogging(true);
        assertThat(aspect.getJmxController().isLoggingEnabled()).isTrue();

        aspect = new BasicContext(true, true);
        assertThat(aspect.getJmxController().isLoggingEnabled()).isTrue();

        aspect = new BasicContext(true, false);
        assertThat(aspect.getJmxController().isLoggingEnabled()).isFalse();
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

        @Pointcut("set(* org.automon.tracing.spring.MyTestClass.name) || execution(* org.automon.tracing.spring.MyTestClass.*(..))")
        public void select() {
        }

    }

}