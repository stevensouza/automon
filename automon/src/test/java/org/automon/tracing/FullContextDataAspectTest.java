package org.automon.tracing;

import org.apache.logging.log4j.core.LogEvent;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class FullContextDataAspectTest extends TestTracingAspectBase {

    @Test
    public void testFullContextData() {
        MyFullContextDataTestClass myTestClass = new MyFullContextDataTestClass();
        myTestClass.firstName("steve");

        final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());
        LOGGER.info("MDC/NDC should now be removed");

        List<LogEvent> logEvents = getListAppender().getEvents();
        assertThat(logEvents).hasSize(3);

        // Define expected log messages up to requestId removed as it is always a unique UUID
        String[] expectedMessages = {
                "INFO  o.a.t.MyFullContextDataTestClass - In MyFullContextDataTestClass.firstName(..) method: MDC={NDC0=MyFullContextDataTestClass.firstName(..), enclosingSignature=MyFullContextDataTestClass.firstName(..), kind=method-execution, parameters={name=steve}, target=MyFullContextDataTestClass.toString(), this=MyFullContextDataTestClass.toString()}",
                "INFO  o.a.t.MyFullContextDataTestClass - In MyFullContextDataTestClass.hi() method: MDC={NDC0=MyFullContextDataTestClass.firstName(..), NDC1=MyFullContextDataTestClass.hi(), enclosingSignature=MyFullContextDataTestClass.hi(), kind=method-execution, parameters={}, target=MyFullContextDataTestClass.toString(), this=MyFullContextDataTestClass.toString()}",
                "INFO  o.a.t.FullContextDataAspectTest - MDC/NDC should now be removed: MDC={}"
        };

        assertLogEvents(logEvents, expectedMessages);
    }


    @Aspect
    static class FullContextData extends FullContextDataAspect {

        @Pointcut("execution(* org.automon.tracing.MyFullContextDataTestClass.*(..)) &&" +
                "!execution(* org.automon.tracing.MyFullContextDataTestClass.toString())")
        public void fullContextData() {
        }

    }

}