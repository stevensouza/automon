package org.automon.tracing;

import org.apache.logging.log4j.core.LogEvent;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class RequestIdAspectTest extends TestTracingAspectBase {

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
                "INFO  o.a.t.RequestIdAspectTest$MyRequestTestClass - In MyRequestTestClass.firstName(..) method: MDC={requestId=",
                "INFO  o.a.t.RequestIdAspectTest$MyRequestTestClass - In MyRequestTestClass.hi() method: MDC={requestId=",
                "INFO  o.a.t.RequestIdAspectTest - MDC={requestId=#UUID should now be removed: MDC={}"
        };

        assertLogEvents(logEvents, expectedMessages);
    }

    @Aspect
    static class RequestId extends RequestIdAspect {

        @Pointcut("execution(* org.automon.tracing.RequestIdAspectTest.MyRequestTestClass.firstName(..))")
        public void requestStart() {
        }

    }

    public static class MyRequestTestClass {
        private final Logger logger = LoggerFactory.getLogger(MyRequestTestClass.class);

        public String firstName(String name) {
            logger.info("In MyRequestTestClass.firstName(..) method");
            hi();
            return name;
        }

        public void hi() {
            logger.info("In MyRequestTestClass.hi() method");
        }
    }
}