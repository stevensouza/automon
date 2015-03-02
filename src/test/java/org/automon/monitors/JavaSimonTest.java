package org.automon.monitors;

import com.jamonapi.MonitorFactory;
import org.aspectj.lang.JoinPoint;
import org.javasimon.Counter;
import org.javasimon.SimonManager;
import org.javasimon.Split;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class JavaSimonTest {
    private JavaSimon openMon = new JavaSimon();
    private JoinPoint jp = mock(JoinPoint.class);
    private static final String LABEL =  "helloWorld.timer";
    private static final String EXCEPTION = "org.automon.MyException";

    @Before
    public void setUp() throws Exception {
        SimonManager.clear();
        JoinPoint.StaticPart staticPart = mock(JoinPoint.StaticPart .class);
        when(jp.getStaticPart()).thenReturn(staticPart);
        when(staticPart.toString()).thenReturn(LABEL);
    }

    @After
    public void tearDown() throws Exception {
        SimonManager.clear();
    }

    @Test
    public void testStart() throws Exception {
        Split mon = openMon.start(jp);
        assertThat(mon.getStopwatch().getActive()).describedAs("The monitor should have been started").isEqualTo(1);
        assertThat(mon.getStopwatch().getCounter()).describedAs("The monitor should not have finished").isEqualTo(0);
    }

    @Test
    public void testStop() throws Exception {
        Split mon = openMon.start(jp);
        openMon.stop(mon);
        assertThat(mon.getStopwatch().getActive()).describedAs("The monitor should have been started").isEqualTo(0);
        assertThat(mon.getStopwatch().getCounter()).describedAs("The monitor should not have finished").isEqualTo(1);
    }

    @Test
    public void testStopWithException() throws Exception {
        Split mon = openMon.start(jp);
        openMon.stop(mon, new RuntimeException("my exception"));
        assertThat(mon.getStopwatch().getActive()).describedAs("The monitor should have been started").isEqualTo(0);
        assertThat(mon.getStopwatch().getCounter()).describedAs("The monitor should not have finished").isEqualTo(1);
    }

    @Test
    public void testException() throws Exception {
        Counter mon = SimonManager.getCounter(EXCEPTION);
        assertThat(mon.getCounter()).describedAs("The exception monitor should not have been created yet").isEqualTo(0);
        openMon.exception(EXCEPTION);
        assertThat(mon.getCounter()).describedAs("The exception monitor should not have been created yet").isEqualTo(1);
    }

}