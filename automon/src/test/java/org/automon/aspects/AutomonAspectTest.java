package org.automon.aspects;

import org.aspectj.lang.Aspects;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.aspects.jmx.AutomonMXBean;
import org.automon.implementations.Jamon;
import org.automon.implementations.NullImp;
import org.automon.implementations.OpenMon;
import org.automon.implementations.OpenMonFactory;
import org.automon.utils.Utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AutomonAspectTest {

    private final MyInheritedAutomonAspect aspect = Aspects.aspectOf(MyInheritedAutomonAspect.class);
    private final Throwable exception = new RuntimeException("my exception");
    private final OpenMon openMon = mock(OpenMon.class);

    @BeforeEach
    public void setUp() throws Exception {
        aspect.setOpenMon(openMon);
    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    @Test
    public void testIsEnabled() {
        assertThat(aspect.isEnabled()).describedAs("Should be enabled").isTrue();
        aspect.setOpenMon(OpenMonFactory.NULL_IMP);
        assertThat(aspect.isEnabled()).describedAs("Should be disabled").isFalse();
    }

    @Test
    public void testSetOpenMon() {
        aspect.setOpenMon(openMon);
        assertThat(aspect.getOpenMon()).describedAs("Should be equal to openMon that was set").isEqualTo(openMon);
    }

    @Test
    public void testSetOpenMonWithString() {
        aspect.setOpenMon(OpenMonFactory.JAMON);
        assertThat(aspect.getOpenMon()).describedAs("Should be equal to openMon that was set").isInstanceOf(Jamon.class);
    }

    @Test
    public void testSetOpenMonWithNull() {
        aspect.setOpenMon((String) null);
        assertThat(aspect.getOpenMon()).describedAs("Should not be null").isNotNull();
        assertThat(aspect.getOpenMon()).describedAs("Should have found one of the default implementations in the classpath").isNotInstanceOf(NullImp.class);
    }

    @Test
    public void testGetOpenMonFactory() {
        assertThat(aspect.getOpenMonFactory()).isNotNull();
    }

    @Test
    public void testMonitorPerformance() {
        HiWorld hi = new HiWorld();
        Object START_CONTEXT = new Object();
        when(openMon.start(any(JoinPoint.StaticPart.class))).thenReturn(START_CONTEXT);

        hi.hello();

        verify(openMon).start(any(JoinPoint.StaticPart.class));
        verify(openMon).stop(START_CONTEXT);
    }

    @Test
    public void testMonitorExceptions() throws Throwable {
        Object START_CONTEXT = new Object();
        when(openMon.start(any(JoinPoint.StaticPart.class))).thenReturn(START_CONTEXT);

        HiWorld hi = new HiWorld();
        try {
            hi.myException(exception);
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

        mxBean.setOpenMon(OpenMonFactory.NULL_IMP);
        assertThat(mxBean.isEnabled()).describedAs("Should be disabled").isFalse();
        assertThat(mxBean.isEnabled()).describedAs("Both should be the same").isEqualTo(aspect.isEnabled());
        assertThat(mxBean.getOpenMon()).
                describedAs("Jmx version and aspect version should be the same").
                isEqualTo(aspect.getOpenMon().toString());
    }


    /**
     * Note1: I had to use an @Aspect annotated aspect vs the native aspect here.  The problem was that in test it appeared that the java compiler
     * would run before ajc, and so any 'aspect' classes wouldn't be available when the tests were run.  Using @Aspect let javac compile them.
     * This is also good as it shows java developers how to inherit from the aspects.
     * <p>
     * Note2: The @Override annotation was not used below as it will not compile with ajc.
     * <p>
     * Note3: I also couldn't think of a way to disable all monitoring without the following pointcuts
     * Tried if(false) and within(com.idontexist.IDont) - The second might work thought it gave a
     * warning that there was no match.
     */
    @Aspect
    static class MyInheritedAutomonAspect extends AutomonAspect {
        @Pointcut("hiWorld()")
        public void _sys_monitor() {
        }

        @Pointcut("hiWorld()")
        public void user_monitor() {
        }

        @Pointcut("hiWorld()")
        public void _sys_exceptions() {
        }

        @Pointcut("hiWorld()")
        public void user_exceptions() {
        }

        @Pointcut("execution(* org.automon.aspects.HiWorld.*(..))")
        public void hiWorld() {
        }
    }

}