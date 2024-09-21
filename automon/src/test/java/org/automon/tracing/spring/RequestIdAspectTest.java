package org.automon.tracing.spring;

import org.apache.logging.log4j.core.LogEvent;
import org.aspectj.lang.Aspects;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.tracing.TestTracingAspectBase;
import org.automon.jmx.EnableMXBean;
import org.automon.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class RequestIdAspectTest extends TestTracingAspectBase {
    private final RequestId aspect = Aspects.aspectOf(RequestId.class);

    @BeforeEach
    void setUp() {
        reset();
    }

    @AfterEach
    void tearDown() {
        reset();
    }


    private void reset() {
        getListAppender().clear();
        aspect.enable(true);
    }

    @Test
    void testNoArgConstructor_defaultsToEnabled() {
        RequestId requestId = new RequestId();

        assertThat(requestId.isEnabled()).isTrue();
        assertThat(requestId.isEnabled()).isTrue();
    }

    @Test
    void testParameterizedConstructor_setsEnabledState() {
        RequestId requestId = new RequestId(true);

        assertThat(requestId.isEnabled()).isTrue();
        assertThat(requestId.isEnabled()).isTrue();

        requestId = new RequestId(false);

        assertThat(requestId.isEnabled()).isFalse();
        assertThat(requestId.isEnabled()).isFalse();
    }

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
        RequestId aspect = new RequestId();
        EnableMXBean mxBean = Utils.getMxBean(aspect.getPurpose(), aspect, EnableMXBean.class);
        assertThat(mxBean.isEnabled()).describedAs("Should be enabled").isTrue();
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
                "INFO  o.a.t.s.RequestIdAspectTest$MyRequestTestClass - In MyRequestTestClass.firstName(..) method: MDC={requestId=",
                "INFO  o.a.t.s.RequestIdAspectTest$MyRequestTestClass - In MyRequestTestClass.hi() method: MDC={requestId=",
                "INFO  o.a.t.s.RequestIdAspectTest - MDC={requestId=#UUID should now be removed: MDC={}"
        };

        assertLogEvents(logEvents, expectedMessages);
    }

    @Test
    public void testDisable() {
        aspect.enable(false);
        MyRequestTestClass myTestClass = new MyRequestTestClass();
        myTestClass.firstName("steve");
        final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());
        LOGGER.info("MDC={requestId=#UUID should now be removed");

        List<LogEvent> logEvents = getListAppender().getEvents();
        assertThat(logEvents).hasSize(3);

        // Define expected log messages up to requestId removed as it is always a unique UUID
        String[] expectedMessages = {
                "INFO  o.a.t.s.RequestIdAspectTest$MyRequestTestClass - In MyRequestTestClass.firstName(..) method: MDC={}",
                "INFO  o.a.t.s.RequestIdAspectTest$MyRequestTestClass - In MyRequestTestClass.hi() method: MDC={}",
                "INFO  o.a.t.s.RequestIdAspectTest - MDC={requestId=#UUID should now be removed: MDC={}"
        };

        assertLogEvents(logEvents, expectedMessages);
    }

    @Aspect
    static class RequestId extends RequestIdAspect {

        public RequestId() {
        }

        public RequestId(boolean enable) {
            super(enable);
        }

        @Pointcut("execution(* org.automon.tracing.spring.RequestIdAspectTest.MyRequestTestClass.firstName(..))")
        public void select() {
        }

    }

    public static class MyRequestTestClass {
        private final Logger logger = LoggerFactory.getLogger(MyRequestTestClass.class);

        public String firstName(String name) {
            logger.info("In MyRequestTestClass.firstName(..) method");
            hi();
            return name;
        }

        private void hi() {
            logger.info("In MyRequestTestClass.hi() method");
        }
    }
}