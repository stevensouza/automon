package org.automon.aspects;

import org.aspectj.lang.Aspects;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.monitors.SysOut;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AspectJBaseTest {

    @Before
    public void setUp() throws Exception {
        MyAspectJTestAspect aspect = Aspects.aspectOf(MyAspectJTestAspect.class);
        aspect.setOpenMon(new SysOut());
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testSys_monitor() throws Exception {
        MyTestClass obj = new MyTestClass();
        obj.hello();
        obj.world();
        obj.iAmHiding();
    }

    @Test
    public void testSys_exceptions() throws Exception {

    }

    @Test
    public void testIfEnabled() throws Exception {

    }

    // Note the @Override annotation was not used below as it will not compile with ajc.
    @Aspect
    static class MyAspectJTestAspect extends AspectJBase {

        @Pointcut("within(MyTestClass) && org.automon.pointcuts.Basic.publicMethod()")
        public void user_monitor() {

        }

        @Pointcut("within(MyTestClass) && org.automon.pointcuts.Basic.publicMethod()")
        public void user_exceptions() {
        }

    }


    private class MyTestClass {
        public void hello() {

        }

        public void world() {

        }

        private void iAmHiding() {

        }
    }
}