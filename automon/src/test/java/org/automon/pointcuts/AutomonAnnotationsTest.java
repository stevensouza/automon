package org.automon.pointcuts;

import org.aspectj.lang.Aspects;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.aspects.AutomonAspectJAspect;
import org.automon.implementations.OpenMon;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AutomonAnnotationsTest {
    private static final RuntimeException EXCEPTION = new RuntimeException("my exception");
    private final OpenMon openMon = mock(OpenMon.class);

    @BeforeEach
    public void setUp() throws Exception {
        MyMonitoringAspect aspect = Aspects.aspectOf(MyMonitoringAspect.class);
        aspect.setOpenMon(openMon);
    }

    @AfterEach
    public void tearDown() throws Exception {

    }


    @Test
    public void testAnnotated_Class() {
        AutomonAnnotatedClass obj = new AutomonAnnotatedClass(); // monitored
        obj.annotatedClass_method1(); // monitored
        obj.annotatedClass_method2(); // monitored
        obj.nonPublicMethod(); //  not monitored
        verify(openMon, times(3)).start(any(JoinPoint.StaticPart.class));
        verify(openMon, times(3)).stop(any());
    }

    @Test
    public void testAnnotated_Class_Exceptions() {
        AutomonAnnotatedClass obj = new AutomonAnnotatedClass(); // monitored
        try {
            obj.myException(EXCEPTION); // monitored
        } catch (Throwable t) {
            assertThat(t).isEqualTo(EXCEPTION);
        }

        verify(openMon, times(2)).start(any(JoinPoint.StaticPart.class));
        verify(openMon).stop(any());
        verify(openMon).stop(any(), eq(EXCEPTION));
        verify(openMon).exception(any(JoinPoint.class), eq(EXCEPTION));
    }

    @Test
    public void testAnnotated_Methods() {
        AutomonAnnotatedMethod obj = new AutomonAnnotatedMethod();
        obj.annotatedMethod(); // monitored
        obj.nonAnnotatedMethod(); // not monitored
        verify(openMon).start(any(JoinPoint.StaticPart.class));
        verify(openMon).stop(any());
    }


    @Test
    public void testAnnotated_Constructor() {
        AutomonAnnotatedMethod obj = new AutomonAnnotatedMethod(); // not monitored
        obj = new AutomonAnnotatedMethod("monitor me!"); // monitored
        verify(openMon).start(any(JoinPoint.StaticPart.class));
        verify(openMon).stop(any());
    }


    @Test
    public void testAnnotated_Methods_Exceptions() {
        AutomonAnnotatedMethod obj = new AutomonAnnotatedMethod();
        try {
            obj.myException(EXCEPTION); // monitored
        } catch (Throwable t) {
            assertThat(t).isEqualTo(EXCEPTION);
        }

        try {
            obj.myNonAnnotatedException(EXCEPTION); // not monitored
        } catch (Throwable t) {
            assertThat(t).isEqualTo(EXCEPTION);
        }


        verify(openMon).start(any(JoinPoint.StaticPart.class));
        verify(openMon).stop(any(), eq(EXCEPTION));
        verify(openMon).exception(any(JoinPoint.class), eq(EXCEPTION));
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
    static class MyMonitoringAspect extends AutomonAspectJAspect {
        // Note this(HelloWorld) only gets instance accesses (not static).  within(HelloWorld) would also get static
        // accesses to fields and methods.

        @Pointcut("within(org.automon.pointcuts.AutomonAnnotated*) && " +
                "(org.automon.pointcuts.Select.publicConstructor() || org.automon.pointcuts.SpringSelect.publicMethod()) &&  " +
                "org.automon.pointcuts.Annotations.automon()"
        )
        public void select() {
        }
    }
}