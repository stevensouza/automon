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

public class AspectJBaseTest {

//    private static final RuntimeException  TEST_RUNTIME_EXCEPTION = HelloWorld.TEST_RUNTIME_EXCEPTION;
//    private static final String RETURN_VALUE = HelloWorld.RETURN_VALUE;
    private OpenMon openMon = mock(OpenMon.class);

    @Before
    public void setUp() throws Exception {
        MyAspectJTestAspect aspect = Aspects.aspectOf(MyAspectJTestAspect.class);
        aspect.setOpenMon(openMon);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testSys_monitorMethods() throws Exception {
        HelloWorld obj = new HelloWorld();
        obj.hello();
        obj.world();
        obj.iAmHiding();

        // start/stop pair should be called once per public method due to public method pointcut, and once for the constructor pointcut.
        verify(openMon, times(3)).start(any(JoinPoint.class));
        verify(openMon, times(3)).stop(any());
    }

    @Test
    public void testSys_monitorFields() throws Exception {
        HelloWorld obj = new HelloWorld();
        obj.setPlanet("earth"); // sets - instance var. method doesn't count as it isn't public and so missed the pointcut
        System.out.println(obj.getPlanet()); // get access for instance variable.  method doesn't match pointcut

        // start/stop pair should be called once for the constructor, and twice instance variable get and set access
        verify(openMon, times(3)).start(any(JoinPoint.class));
        verify(openMon, times(3)).stop(any());
    }

    @Test
    public void testReturnValue() throws Exception {
        HelloWorld obj = new HelloWorld();
        String testString = obj.getString();
        assertThat(testString).isEqualTo(HelloWorld.RETURN_VALUE);
        // called once per the constructor and once for the method
        verify(openMon, times(2)).start(any(JoinPoint.class));
        verify(openMon, times(2)).stop(any());
    }

    @Test
    public void testSys_exceptionsPublic() throws Exception {
        HelloWorld obj = new HelloWorld();
        try {
            obj.throwException();
        } catch (Throwable t) {
            assertThat(t).isEqualTo(HelloWorld.TEST_RUNTIME_EXCEPTION);
        }

        verify(openMon).stop(any(), eq(HelloWorld.TEST_RUNTIME_EXCEPTION));// method call with exception
        verify(openMon, times(2)).stop(any()); // constructor, and 1 field gets
        verify(openMon).exception(any(JoinPoint.class), eq(HelloWorld.TEST_RUNTIME_EXCEPTION));
    }

    @Test
    public void testSys_exceptionsProtected() throws Exception {
        HelloWorld obj = new HelloWorld();
        try {
            obj.throwOtherException(); // won't be monitored as it is not public
        } catch (Throwable t) {
            assertThat(t).isEqualTo(HelloWorld.TEST_RUNTIME_EXCEPTION);
        }

        verify(openMon, never()).stop(any(), eq(HelloWorld.TEST_RUNTIME_EXCEPTION));// method call with exception
        verify(openMon, times(2)).stop(any()); // constructor, and 1 field get
        verify(openMon, never()).exception(any(JoinPoint.class), eq(HelloWorld.TEST_RUNTIME_EXCEPTION));
    }


    // Note the @Override annotation was not used below as it will not compile with ajc.
    @Aspect
    static class MyAspectJTestAspect extends AspectJBase {

        // Note this(HelloWorld) only gets instance accesses (not static).  within(HelloWorld) would also get static
        // accesses to fields and methods.
        @Pointcut("this(HelloWorld) && (org.automon.pointcuts.Select.constructor() || " +
                "org.automon.pointcuts.Select.publicMethod() || " +
                "org.automon.pointcuts.Select.fieldGet() || " +
                "org.automon.pointcuts.Select.fieldSet()  " +
                " ) " )
        public void user_monitor() {

        }

        @Pointcut("this(HelloWorld) && org.automon.pointcuts.Select.publicMethod()")
        public void user_exceptions() {
        }

    }

}