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

public class MonitoringAspectTest {

    private final Throwable exception = HelloWorld.TEST_RUNTIME_EXCEPTION;
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
    public void testJmxRegistration() throws Throwable {
        AutomonMXBean mxBean = Utils.getMxBean(aspect.getPurpose(), aspect, AutomonMXBean.class);
        mxBean.setOpenMon(OpenMonFactory.JAMON);

        assertThat(aspect.getOpenMon()).describedAs("Should be equal to openMon that was set").isInstanceOf(Jamon.class);

        assertThat(mxBean.getOpenMonString()).
                describedAs("Jmx version and aspect version should be the same").
                isEqualTo(aspect.getOpenMon().toString());

        assertThat(mxBean.isEnabled()).describedAs("Should be enabled").isTrue();
        assertThat(mxBean.isEnabled()).describedAs("Both should be the same").isEqualTo(aspect.isEnabled());

        mxBean.enable(false);
        assertThat(mxBean.isEnabled()).describedAs("Should be disabled").isFalse();
        assertThat(mxBean.isEnabled()).describedAs("Both should be the same").isEqualTo(aspect.isEnabled());
        assertThat(mxBean.getOpenMonString()).
                describedAs("Jmx version and aspect version should be the same").
                isEqualTo(aspect.getOpenMon().toString());

    }

    @Test
    public void testSys_monitorMethods() {
        HelloWorld obj = new HelloWorld();
        obj.hello();
        obj.world();
        obj.iAmHiding(); // not public so won't be seen based on the pointcut

        // start/stop pair should be called for each public method due to public method pointcut, and once for the constructor pointcut.
        verify(openMon, times(3)).start(any(JoinPoint.StaticPart.class));
        verify(openMon, times(3)).stop(any());
    }

    @Test
    public void testSys_monitorFields() {
        HelloWorld obj = new HelloWorld();
        obj.setPlanet("earth"); // sets - instance var which matches pointcut. method itself doesn't count as it isn't public and so missed the pointcut
        System.out.println(obj.getPlanet()); // get access for instance variable.  method doesn't match pointcut as it isn't public but get var does

        // start/stop pair should be called once for the constructor, and twice for instance variable get and set access
        verify(openMon, times(3)).start(any(JoinPoint.StaticPart.class));
        verify(openMon, times(3)).stop(any());
    }

    @Test
    public void testReturnValue() {
        HelloWorld obj = new HelloWorld();
        String testString = obj.getString();
        assertThat(testString).isEqualTo(HelloWorld.RETURN_VALUE);
        // called once per the constructor and once for the method
        verify(openMon, times(2)).start(any(JoinPoint.StaticPart.class));
        verify(openMon, times(2)).stop(any());
    }

    @Test
    public void testSys_exceptionsPublic() {
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
    public void testSys_exceptionsProtected() {
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

    @Aspect
    static class MyInheritedMonitoringAspect extends MonitoringAspect {

        // Note this(HelloWorld) only gets instance accesses (not static).  within(HelloWorld) would also get static
        // accesses to fields and methods.
        @Pointcut("this(org.automon.aspects.HelloWorld) && (org.automon.pointcuts.Select.constructor() || " +
                "org.automon.pointcuts.Select.publicMethod() || " +
                "org.automon.pointcuts.Select.fieldGet() || " +
                "org.automon.pointcuts.Select.fieldSet()  " +
                " ) ")
        public void select() {
        }
    }

}