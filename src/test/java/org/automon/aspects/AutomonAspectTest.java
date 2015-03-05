package org.automon.aspects;

import org.aspectj.lang.Aspects;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.implementations.OpenMon;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AutomonAspectTest {

    private MyInheritedAutomonAspect aspect = Aspects.aspectOf(MyInheritedAutomonAspect.class);
    private Throwable exception = new RuntimeException("my exception");
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
        assertThat(aspect.isEnabled()).describedAs("Should be enabled by default").isTrue();
    }

    @Test
    public void testEnableDisable() throws Exception {
        aspect.enable(false);
        assertThat(aspect.isEnabled()).describedAs("Should be disabled").isFalse();

        aspect.enable(true);
        assertThat(aspect.isEnabled()).describedAs("Should be enabled").isTrue();
    }

    @Test
    public void testMonitorPerformance() throws Throwable {
        HiWorld hi = new HiWorld();
        Object START_CONTEXT = new Object();
        when(openMon.start(any(JoinPoint.StaticPart.class))).thenReturn(START_CONTEXT);

        hi.hello();

        verify(openMon).start(any(JoinPoint.StaticPart.class));
        verify(openMon).stop(START_CONTEXT);
    }

    @Test
    public void testMonitorExceptions() throws Throwable {
        HiWorld hi = new HiWorld();
        try {
            hi.myException(exception);
        } catch (Exception e) {
            assertThat(e).isEqualTo(exception);
        }

        verify(openMon).exception(any(JoinPoint.class), eq(exception));
    }


    /** Note1: I had to use an @Aspect annotated aspect vs the native aspect here.  The problem was that in test it appeared that the java compiler
     * would run before ajc, and so any 'aspect' classes wouldn't be available when the tests were run.  Using @Aspect let javac compile them.
     * This is also good as it shows java developers how to inherit from the aspects.
     *
     * Note2: The @Override annotation was not used below as it will not compile with ajc.
     *
     * Note3: I also couldn't think of a way to disable all monitoring without the following pointcuts
     *  Tried if(false) and within(com.idontexist.IDont) - The second might work thought it gave a
     *  warning that there was no match.
     */
    @Aspect
    static class MyInheritedAutomonAspect extends AutomonAspect {
        @Pointcut("hiWorld()")
        public void sys_monitor() {
        }

        @Pointcut("hiWorld()")
        public void user_monitor() {
        }

        @Pointcut("hiWorld()")
        public void sys_exceptions() {
        }

        @Pointcut("hiWorld()")
        public void user_exceptions() {
        }

        @Pointcut("execution(* HiWorld.*(..))")
        public void hiWorld() {
        }
    }

}