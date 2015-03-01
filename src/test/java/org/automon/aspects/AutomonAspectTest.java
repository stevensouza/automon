package org.automon.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.monitors.OpenMon;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AutomonAspectTest {

    private AutomonAspect aspect = new MyDisabledTestAspect();
    private Throwable exception = new RuntimeException("my exception");
    private ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
    private JoinPoint jp = mock(JoinPoint.class);
    private OpenMon openMon = mock(OpenMon.class);


    @Before
    public void setUp() throws Exception {
        aspect.setOpenMon(openMon);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testIsEnabledByDefault() throws Exception {
        assertThat(AutomonAspect.isEnabled()).describedAs("Should be enabled by default").isTrue();
    }

    @Test
    public void testEnableDisable() throws Exception {
        AutomonAspect.enable(false);
        assertThat(AutomonAspect.isEnabled()).describedAs("Should be disabled").isFalse();

        AutomonAspect.enable(true);
        assertThat(AutomonAspect.isEnabled()).describedAs("Should be enabled").isTrue();
    }

    @Test
    public void testMonitorPerformance() throws Throwable {
        Object START_RETURN_VALUE = new Object();
        when(openMon.start(anyString())).thenReturn(START_RETURN_VALUE);
        when(pjp.getStaticPart()).thenReturn(mock(JoinPoint.StaticPart.class));

        aspect.monitorPerformance(pjp);

        verify(openMon).start(anyString());
        verify(openMon).stop(START_RETURN_VALUE);
    }

    @Test
    public void testMonitorExceptions() throws Throwable {
        aspect.monitorExceptions(jp, exception);

        verify(openMon).exception(anyString());
    }

    @Aspect
    static class MyDisabledTestAspect extends AutomonAspect {
        // Note the @Override annotation was not used below as it will not compile with ajc.
        // Note I also couldn't think of a way to disable all monitoring without the following pointcuts
        //  Tried if(false) and within(com.idontexist.IDont) - The second might work thought it gave a
        //  warning that there was no match.

        @Pointcut("!within(java.lang.Object+)")
        public void sys_monitor() {

        }

        @Pointcut("!within(java.lang.Object+)")
        public void user_monitor() {

        }

        @Pointcut("!within(java.lang.Object+)")
        public void sys_exceptions() {

        }

        @Pointcut("!within(java.lang.Object+)")
        public void user_exceptions() {
        }

    }

}