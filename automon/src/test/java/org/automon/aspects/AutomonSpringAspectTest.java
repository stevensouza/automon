package org.automon.aspects;

import org.aspectj.lang.Aspects;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.aspects.jmx.AutomonMXBean;
import org.automon.implementations.Jamon;
import org.automon.implementations.OpenMon;
import org.automon.implementations.OpenMonFactory;
import org.automon.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AutomonSpringAspectTest {

    private final Throwable exception = MyClass.TEST_RUNTIME_EXCEPTION;
    private final OpenMon openMon = mock(OpenMon.class);
    private final MyInheritedMonitoringAspect aspect = Aspects.aspectOf(MyInheritedMonitoringAspect.class);

    private ApplicationContext context;

    @BeforeEach
    public void setUp() throws Exception {
        aspect.setOpenMon(openMon);
        aspect.enable(true);
    }

    @AfterEach
    public void tearDown() throws Exception {
    }


    @Test
    public void testMonitorPerformance() {
        MyClass monitorMe = new MyClass();
        Object START_CONTEXT = new Object();
        when(openMon.start(any(JoinPoint.StaticPart.class))).thenReturn(START_CONTEXT);

        monitorMe.getString();

        verify(openMon).start(any(JoinPoint.StaticPart.class));
        verify(openMon).stop(START_CONTEXT);
    }

    @Test
    public void testMonitorExceptions() {
        Object START_CONTEXT = new Object();
        when(openMon.start(any(JoinPoint.StaticPart.class))).thenReturn(START_CONTEXT);
        MyClass monitorMe = new MyClass();
        try {
            monitorMe.throwException();
        } catch (Exception e) {
            assertThat(e).isEqualTo(exception);
        }

        verify(openMon).stop(START_CONTEXT, exception);
        verify(openMon, never()).stop(any());
        verify(openMon).exception(any(JoinPoint.class), eq(exception));
    }

    @Test
    public void testJmxRegistration() throws Throwable {
        AutomonMXBean mxBean = Utils.getMxBean(aspect.getPurpose(), aspect, AutomonMXBean.class);
        mxBean.setOpenMon(OpenMonFactory.JAMON);

        assertThat(aspect.getOpenMon()).describedAs("Should be equal to openMon that was set").isInstanceOf(Jamon.class);

        assertThat(mxBean.getOpenMon()).
                describedAs("Jmx version and aspect version should be the same").
                isEqualTo(aspect.getOpenMon().toString());

        assertThat(mxBean.isEnabled()).describedAs("Should be enabled").isTrue();
        assertThat(mxBean.isEnabled()).describedAs("Both should be the same").isEqualTo(aspect.isEnabled());

        mxBean.enable(false);
        assertThat(mxBean.isEnabled()).describedAs("Should be disabled").isFalse();
        assertThat(mxBean.isEnabled()).describedAs("Both should be the same").isEqualTo(aspect.isEnabled());
        assertThat(mxBean.getOpenMon()).
                describedAs("Jmx version and aspect version should be the same").
                isEqualTo(aspect.getOpenMon().toString());

    }

    static class MyClass {
        static final RuntimeException TEST_RUNTIME_EXCEPTION = new RuntimeException("testing throwing exceptions");
        static final String RETURN_VALUE = "any string";

        public String getString() {
            return RETURN_VALUE;
        }

        public void throwException() {
            throw TEST_RUNTIME_EXCEPTION;
        }
    }

    @Aspect
    static class MyInheritedMonitoringAspect extends AutomonSpringAspect {

        @Pointcut("execution(* org.automon.aspects.AutomonSpringAspectTest.MyClass.*(..))")
        public void select() {
        }
    }

}