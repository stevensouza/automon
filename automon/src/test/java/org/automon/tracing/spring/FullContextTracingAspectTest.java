package org.automon.tracing.spring;

import org.apache.logging.log4j.core.LogEvent;
import org.aspectj.lang.Aspects;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.tracing.TestTracingAspectBase;
import org.automon.jmx.TraceJmxController;
import org.automon.jmx.TraceJmxControllerMBean;
import org.automon.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class FullContextTracingAspectTest extends TestTracingAspectBase {


    @BeforeEach
    void setUp() {
        reset();
    }

    @AfterEach
    void tearDown() {
        reset();
    }


    private TraceJmxController getJmx() {
        FullContext aspect = Aspects.aspectOf(FullContext.class);
        return aspect.getJmxController();
    }

    private void reset() {
        getListAppender().clear();
        getJmx().enable(true);
        getJmx().enableLogging(true);
    }

    @Test
    void testNoArgConstructor_defaultsToEnabled() {
        FullContext fc = new FullContext();

        assertThat(fc.isEnabled()).isTrue();
        assertThat(fc.getJmxController().isEnabled()).isTrue();
        assertThat(fc.getJmxController().isLoggingEnabled()).isTrue();
    }


    @Test
    void testOneParameterizedConstructor_setsEnabledState() {
        FullContext fc = new FullContext(true);

        assertThat(fc.isEnabled()).isTrue();
        assertThat(fc.getJmxController().isEnabled()).isTrue();
        assertThat(fc.getJmxController().isLoggingEnabled()).isTrue();

        fc = new FullContext(false);

        assertThat(fc.isEnabled()).isFalse();
        assertThat(fc.getJmxController().isEnabled()).isFalse();
        assertThat(fc.getJmxController().isLoggingEnabled()).isTrue();
    }

    @Test
    void testTwoParameterizedConstructor_setsEnabledState() {
        FullContext fc = new FullContext(true, true);

        assertThat(fc.isEnabled()).isTrue();
        assertThat(fc.getJmxController().isEnabled()).isTrue();
        assertThat(fc.getJmxController().isLoggingEnabled()).isTrue();

        fc = new FullContext(true, false);

        assertThat(fc.isEnabled()).isTrue();
        assertThat(fc.getJmxController().isEnabled()).isTrue();
        assertThat(fc.getJmxController().isLoggingEnabled()).isFalse();
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
        FullContext aspect = new FullContext();
        TraceJmxControllerMBean mxBean = Utils.getMxBean(aspect.getPurpose(), aspect, TraceJmxControllerMBean.class);
        assertThat(mxBean.isEnabled()).describedAs("Should be enabled").isTrue();
    }


    @Test
    public void testLogInfo() {
        MyTestClass2 myTestClass = new MyTestClass2();
        myTestClass.name();

        List<LogEvent> logEvents = getListAppender().getEvents();
        assertThat(logEvents).hasSize(8);

        // Define expected log messages (with executionTimeMs replaced)
        String[] expectedMessages = {
                "INFO  o.a.t.s.FullContextTracingAspectTest$FullContext - BEFORE: MDC={NDC0=MyTestClass2.name(), kind=method-execution, parameters={}, target=MyTestClass2.toString(), this=MyTestClass2.toString()}",
                "INFO  o.a.t.s.FullContextTracingAspectTest$FullContext - BEFORE: MDC={NDC0=MyTestClass2.name(), NDC1=MyTestClass2.first(..), kind=method-execution, parameters={name=steve}, target=MyTestClass2.toString(), this=MyTestClass2.toString()}",
                "INFO  o.a.t.s.FullContextTracingAspectTest$FullContext - BEFORE: MDC={NDC0=MyTestClass2.name(), NDC1=MyTestClass2.first(..), NDC2=MyTestClass2.name, kind=field-set, parameters={param0=steve}, target=MyTestClass2.toString(), this=MyTestClass2.toString()}",
                "INFO  o.a.t.s.FullContextTracingAspectTest$FullContext - AFTER: MDC={NDC0=MyTestClass2.name(), NDC1=MyTestClass2.first(..), NDC2=MyTestClass2.name, executionTimeMs=#, kind=field-set, parameters={param0=steve}, returnValue=null, target=MyTestClass2.toString(), this=MyTestClass2.toString()}",
                "INFO  o.a.t.s.FullContextTracingAspectTest$FullContext - AFTER: MDC={NDC0=MyTestClass2.name(), NDC1=MyTestClass2.first(..), executionTimeMs=#, returnValue=steve}",
                "INFO  o.a.t.s.FullContextTracingAspectTest$FullContext - BEFORE: MDC={NDC0=MyTestClass2.name(), NDC1=MyTestClass2.last(..), kind=method-execution, parameters={name=souza}, target=MyTestClass2.toString(), this=MyTestClass2.toString()}",
                "INFO  o.a.t.s.FullContextTracingAspectTest$FullContext - AFTER: MDC={NDC0=MyTestClass2.name(), NDC1=MyTestClass2.last(..), executionTimeMs=#, kind=method-execution, parameters={name=souza}, returnValue=souza, target=MyTestClass2.toString(), this=MyTestClass2.toString()}",
                "INFO  o.a.t.s.FullContextTracingAspectTest$FullContext - AFTER: MDC={NDC0=MyTestClass2.name(), executionTimeMs=#, returnValue=name: steve souza}"
        };

        assertLogEvents(logEvents, expectedMessages);
    }

    @Test
    public void testDisable() {
        getJmx().enable(false);
        MyTestClass2 myTestClass = new MyTestClass2();
        myTestClass.name();

        List<LogEvent> logEvents = getListAppender().getEvents();
        assertThat(logEvents).hasSize(0);
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
                "INFO  o.a.t.s.FullContextTracingAspectTest$FullContext - BEFORE: MDC={NDC0=MyTestClass2.exceptions(), kind=method-execution, parameters={}, target=MyTestClass2.toString(), this=MyTestClass2.toString()}",
                "INFO  o.a.t.s.FullContextTracingAspectTest$FullContext - BEFORE: MDC={NDC0=MyTestClass2.exceptions(), NDC1=MyTestClass2.checkedException(), kind=method-execution, parameters={}, target=MyTestClass2.toString(), this=MyTestClass2.toString()}",
                "ERROR o.a.t.s.FullContextTracingAspectTest$FullContext - AFTER: MDC={NDC0=MyTestClass2.exceptions(), NDC1=MyTestClass2.checkedException(), exception=org.automon.tracing.spring.MyTestClass2.MyException, kind=method-execution, parameters={}, target=MyTestClass2.toString(), this=MyTestClass2.toString()}",
                "INFO  o.a.t.s.FullContextTracingAspectTest$FullContext - BEFORE: MDC={NDC0=MyTestClass2.exceptions(), NDC1=MyTestClass2.runTimeException(), kind=method-execution, parameters={}, target=MyTestClass2.toString(), this=MyTestClass2.toString()}",
                "ERROR o.a.t.s.FullContextTracingAspectTest$FullContext - AFTER: MDC={NDC0=MyTestClass2.exceptions(), NDC1=MyTestClass2.runTimeException(), exception=java.lang.RuntimeException, kind=method-execution, parameters={}, target=MyTestClass2.toString(), this=MyTestClass2.toString()}",
                "INFO  o.a.t.s.FullContextTracingAspectTest$FullContext - AFTER: MDC={NDC0=MyTestClass2.exceptions(), executionTimeMs=#, returnValue=null}"
        };

        assertLogEvents(logEvents, expectedMessages);
    }

    @Test
    public void testVarArgs() {
        MyTestClass2 myTestClass = new MyTestClass2();
        myTestClass.calculateSum(1, 2, 3);

        List<LogEvent> logEvents = getListAppender().getEvents();
        assertThat(logEvents).hasSize(2);

        String[] expectedMessages = {
                // note only beginning of log message is used as the var arg value/address can always be different.
                "INFO  o.a.t.s.FullContextTracingAspectTest$FullContext - BEFORE: MDC={NDC0=MyTestClass2.calculateSum(..), kind=method-execution, parameters={numbers=[I@",
                "INFO  o.a.t.s.FullContextTracingAspectTest$FullContext - AFTER: MDC={NDC0=MyTestClass2.calculateSum(..), executionTimeMs=#, kind=method-execution, parameters={numbers=[I@"
        };

        assertLogEvents(logEvents, expectedMessages);
    }

    @Test
    public void testNoLogging() {
        getJmx().enableLogging(false);

        MyTestClass2 myTestClass = new MyTestClass2();
        myTestClass.name();
        myTestClass.exceptions();

        List<LogEvent> logEvents = getListAppender().getEvents();
        assertThat(logEvents).
                describedAs("Logging is disabled so there should be no logging events").
                hasSize(0);

    }

    @Aspect
    static class FullContext extends FullContextTracingAspect {

        // No-arg constructor
        public FullContext() {
        }

        // Single-argument constructor (enable tracing)
        public FullContext(boolean enable) {
            super(enable, true); // Default to logging enabled
        }

        // Two-argument constructor (enable tracing and logging)
        public FullContext(boolean enable, boolean enableLogging) {
            super(enable, enableLogging);
        }

        @Pointcut("(set(* org.automon.tracing.spring.MyTestClass2.name) || execution(* org.automon.tracing.spring.MyTestClass2.*(..))) && !execution(* org.automon.tracing.spring.MyTestClass2.toString())")
        public void select() {
        }

    }
    
}