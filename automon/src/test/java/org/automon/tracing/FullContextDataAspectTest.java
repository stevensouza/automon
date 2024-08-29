package org.automon.tracing;

import org.apache.logging.log4j.core.LogEvent;
import org.aspectj.lang.Aspects;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class FullContextDataAspectTest extends TestTracingAspectBase {

    @BeforeEach
    void setUp() {
        reset();
    }

    @AfterEach
    void tearDown() {
        reset();
    }

    private FullContextData aspect = null;

    private void reset() {
        getListAppender().clear();
        aspect = Aspects.aspectOf(FullContextData.class);
        aspect.enable(true);
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
    public void testFullContextData() {
        MyFullContextDataTestClass myTestClass = new MyFullContextDataTestClass();
        myTestClass.firstName("steve");

        final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());
        LOGGER.info("MDC/NDC should now be removed");

        List<LogEvent> logEvents = getListAppender().getEvents();
        assertThat(logEvents).hasSize(3);

        // Define expected log messages up to requestId removed as it is always a unique UUID
        String[] expectedMessages = {
                "INFO  o.a.t.FullContextDataAspectTest$MyFullContextDataTestClass - In MyFullContextDataTestClass.firstName(..) method: MDC={NDC0=FullContextDataAspectTest.MyFullContextDataTestClass.firstName(..), enclosingSignature=FullContextDataAspectTest.MyFullContextDataTestClass.firstName(..), kind=method-execution, parameters={name=steve}, target=MyFullContextDataTestClass.toString(), this=MyFullContextDataTestClass.toString()}",
                "INFO  o.a.t.FullContextDataAspectTest$MyFullContextDataTestClass - In MyFullContextDataTestClass.hi() method: MDC={NDC0=FullContextDataAspectTest.MyFullContextDataTestClass.firstName(..), NDC1=FullContextDataAspectTest.MyFullContextDataTestClass.hi(), enclosingSignature=FullContextDataAspectTest.MyFullContextDataTestClass.hi(), kind=method-execution, parameters={}, target=MyFullContextDataTestClass.toString(), this=MyFullContextDataTestClass.toString()}",
                "INFO  o.a.t.FullContextDataAspectTest - MDC/NDC should now be removed: MDC={}"
        };

        assertLogEvents(logEvents, expectedMessages);
    }


    @Aspect
    static class FullContextData extends FullContextDataAspect {

        @Pointcut("execution(* org.automon.tracing.FullContextDataAspectTest.MyFullContextDataTestClass.*(..)) &&" +
                "!execution(* org.automon.tracing.FullContextDataAspectTest.MyFullContextDataTestClass.toString())")
        public void fullContextData() {
        }

    }

    public static class MyFullContextDataTestClass {
        private final Logger logger = LoggerFactory.getLogger(MyFullContextDataTestClass.class);

        public String firstName(String name) {
            logger.info("In MyFullContextDataTestClass.firstName(..) method");
            hi();
            return name;
        }

        public void hi() {
            logger.info("In MyFullContextDataTestClass.hi() method");
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + ".toString()";
        }
    }

}