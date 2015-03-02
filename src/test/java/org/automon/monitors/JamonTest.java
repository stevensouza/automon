package org.automon.monitors;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.aspectj.lang.JoinPoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JamonTest {
    private Jamon openMon = new Jamon();
    private JoinPoint jp = mock(JoinPoint.class);
    private static final String LABEL =  "helloWorld.timer";
    private static final Exception EXCEPTION = new RuntimeException("my exception");
    private static final String EXCEPTION_LABEL = EXCEPTION.getClass().getName();

    @Before
    public void setUp() throws Exception {
        MonitorFactory.reset();
        JoinPoint.StaticPart staticPart = mock(JoinPoint.StaticPart .class);
        when(jp.getStaticPart()).thenReturn(staticPart);
        when(staticPart.toString()).thenReturn(LABEL);
    }

    @After
    public void tearDown() throws Exception {
        MonitorFactory.reset();
    }

    @Test
    public void testStart() throws Exception {
        Monitor mon = openMon.start(jp);
        assertThat(mon.getLabel()).describedAs("The label should match passed in label").isEqualTo(LABEL);
        assertThat(mon.getUnits()).describedAs("The units should be for time").isEqualTo("ms.");
        assertThat(mon.getActive()).describedAs("The monitor should have been started").isEqualTo(1);
    }

    @Test
    public void testStop() throws Exception {
        Monitor mon = openMon.start(jp);
        openMon.stop(mon);
        assertThat(mon.getLabel()).describedAs("The label should match passed in label").isEqualTo(LABEL);
        assertThat(mon.getActive()).describedAs("The monitor should not be running").isEqualTo(0);
        assertThat(mon.getHits()).describedAs("The monitor should have finished and recorded hits").isEqualTo(1);
    }

    @Test
    public void testStopWithException() throws Exception {
        Monitor mon = openMon.start(jp);
        openMon.stop(mon, new RuntimeException("my exception"));
        assertThat(mon.getLabel()).describedAs("The label should match passed in label").isEqualTo(LABEL);
        assertThat(mon.getActive()).describedAs("The monitor should not be running").isEqualTo(0);
        assertThat(mon.getHits()).describedAs("The monitor should have finished and recorded hits").isEqualTo(1);
    }

    @Test
    public void testException() throws Exception {
        assertThat(MonitorFactory.exists(EXCEPTION_LABEL, "Exception")).describedAs("The exception monitor should not exist yet").isFalse();
        openMon.exception(jp, EXCEPTION);
        assertThat(MonitorFactory.exists(EXCEPTION_LABEL, "Exception")).describedAs("The exception monitor should exist").isTrue();
        assertThat(MonitorFactory.getMonitor(EXCEPTION_LABEL, "Exception").getLabel()).
                describedAs("The label should match passed in label").isEqualTo(EXCEPTION_LABEL);
    }



}