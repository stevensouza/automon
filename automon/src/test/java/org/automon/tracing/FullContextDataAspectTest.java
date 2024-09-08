package org.automon.tracing;

import org.apache.logging.log4j.core.LogEvent;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.tracing.jmx.AspectJmxController;
import org.automon.tracing.jmx.AspectJmxControllerMBean;
import org.automon.utils.Utils;
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


    private AspectJmxController getJmx() {
        return FullContextData.getJmxController();
    }

    private void reset() {
        getListAppender().clear();
        getJmx().enable(true);
    }


    @Test
    void testNoArgConstructor_defaultsToEnabled() {
        FullContextData fcd = new FullContextData();

        assertThat(FullContextData.isEnabled()).isTrue();
        assertThat(FullContextData.getJmxController().isEnabled()).isTrue();
    }

    @Test
    void testParameterizedConstructor_setsEnabledState() {
        FullContextData fcd = new FullContextData(true);

        assertThat(FullContextData.isEnabled()).isTrue();
        assertThat(FullContextData.getJmxController().isEnabled()).isTrue();

        fcd = new FullContextData(false);

        assertThat(FullContextData.isEnabled()).isFalse();
        assertThat(FullContextData.getJmxController().isEnabled()).isFalse();
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
        FullContextData aspect = new FullContextData();
        AspectJmxControllerMBean mxBean = Utils.getMxBean(aspect.getPurpose(), aspect, AspectJmxControllerMBean.class);
        assertThat(mxBean.isEnabled()).describedAs("Should be enabled").isTrue();
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

        public FullContextData() {
        }

        public FullContextData(boolean enable) {
            super(enable);
        }

        @Pointcut("enabled() && execution(* org.automon.tracing.FullContextDataAspectTest.MyFullContextDataTestClass.*(..)) &&" +
                "!execution(* org.automon.tracing.FullContextDataAspectTest.MyFullContextDataTestClass.toString())")
        public void select() {
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