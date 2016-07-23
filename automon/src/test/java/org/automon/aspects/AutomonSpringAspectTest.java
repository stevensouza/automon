package org.automon.aspects;

import org.aspectj.lang.JoinPoint;
import org.automon.implementations.OpenMon;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import org.automon.implementations.Jamon;
import org.automon.implementations.OpenMonFactory;
import org.automon.utils.Utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AutomonSpringAspectTest {

    private Throwable exception = HelloWorld.TEST_RUNTIME_EXCEPTION;
    private OpenMon openMon = mock(OpenMon.class);

    private ApplicationContext context;
    @Before
    public void setUp() throws Exception {
        context = new ClassPathXmlApplicationContext("applicationContext.xml");
        AutomonSpringAspect aspect = context.getBean("automonSpringAspect", AutomonSpringAspect.class);
        aspect.setOpenMon(openMon);        
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testMonitorPerformance() throws Throwable {
        HelloWorld monitorMe = context.getBean("monitorMe", HelloWorld.class);
        Object START_CONTEXT = new Object();
        when(openMon.start(any(JoinPoint.StaticPart.class))).thenReturn(START_CONTEXT);
        
        monitorMe.getString();
        
        verify(openMon).start(any(JoinPoint.StaticPart.class));
        verify(openMon).stop(START_CONTEXT);
    }

    @Test
    public void testMonitorExceptions() throws Throwable {
        HelloWorld monitorMe = context.getBean("monitorMe", HelloWorld.class);
        try {
            monitorMe.throwException();
        } catch (Exception e) {
            assertThat(e).isEqualTo(exception);
        }

        verify(openMon).exception(any(JoinPoint.class), eq(exception));
    }

    @Test
    public void testJmxRegistration() throws Throwable {
        AutomonSpringAspect aspect = context.getBean("automonSpringAspect", AutomonSpringAspect.class);
        AutomonMXBean mxBean = Utils.getAutomonMxBean(aspect);
        mxBean.setOpenMon(OpenMonFactory.JAMON);

        assertThat(aspect.getOpenMon()).describedAs("Should be equal to openMon that was set").isInstanceOf(Jamon.class);

        assertThat(mxBean.getOpenMon()).
                describedAs("Jmx version and aspect version should be the same").
                isEqualTo(aspect.getOpenMon().toString());

        assertThat(mxBean.isEnabled()).describedAs("Should be enabled").isTrue();
        assertThat(mxBean.isEnabled()).describedAs("Both should be the same").isEqualTo(aspect.isEnabled());

        mxBean.setOpenMon(OpenMonFactory.NULL_IMP);
        assertThat(mxBean.isEnabled()).describedAs("Should be disabled").isFalse();
        assertThat(mxBean.isEnabled()).describedAs("Both should be the same").isEqualTo(aspect.isEnabled());
        assertThat(mxBean.getOpenMon()).
                describedAs("Jmx version and aspect version should be the same").
                isEqualTo(aspect.getOpenMon().toString());

    }

}