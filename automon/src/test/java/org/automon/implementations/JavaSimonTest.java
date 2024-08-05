package org.automon.implementations;

import org.aspectj.lang.JoinPoint;
import org.javasimon.Counter;
import org.javasimon.SimonManager;
import org.javasimon.Split;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class JavaSimonTest {
    private JavaSimon openMon = new JavaSimon();
    private JoinPoint jp = mock(JoinPoint.class);
    private JoinPoint.StaticPart staticPart = mock(JoinPoint.StaticPart.class);

    @BeforeEach
    public void setUp() throws Exception {
        SimonManager.clear();
        when(staticPart.toString()).thenReturn(SharedConstants.LABEL);
    }

    @AfterEach
    public void tearDown() throws Exception {
        SimonManager.clear();
    }

    @Test
    public void testStart() throws Exception {
        Split mon = openMon.start(staticPart);
        assertThat(mon.getStopwatch().getActive()).describedAs("The monitor should have been started").isEqualTo(1);
        assertThat(mon.getStopwatch().getCounter()).describedAs("The monitor should not have finished").isEqualTo(0);
    }

    @Test
    public void testStop() throws Exception {
        Split mon = openMon.start(staticPart);
        openMon.stop(mon);
        assertThat(mon.getStopwatch().getActive()).describedAs("The monitor should have been started").isEqualTo(0);
        assertThat(mon.getStopwatch().getCounter()).describedAs("The monitor should not have finished").isEqualTo(1);
    }

    @Test
    public void testStopWithException() throws Exception {
        Split mon = openMon.start(staticPart);
        openMon.stop(mon, new RuntimeException("my exception"));
        assertThat(mon.getStopwatch().getActive()).describedAs("The monitor should have been started").isEqualTo(0);
        assertThat(mon.getStopwatch().getCounter()).describedAs("The monitor should not have finished").isEqualTo(1);
    }

    @Test
    public void testException() throws Exception {
        Counter mon = SimonManager.getCounter(openMon.cleanExceptionForSimon(SharedConstants.EXCEPTION_LABEL));
        Counter monGeneral = SimonManager.getCounter(OpenMon.EXCEPTION_LABEL);
        assertThat(mon.getCounter()).describedAs("The exception monitor should not have been created yet").isEqualTo(0);
        assertThat(monGeneral.getCounter()).describedAs("The general exception monitor should not have been created yet").isEqualTo(0);
        openMon.exception(jp, SharedConstants.EXCEPTION);
        assertThat(mon.getCounter()).describedAs("The exception monitor should have been created yet").isEqualTo(1);
        assertThat(monGeneral.getCounter()).describedAs("The general  exception monitor should have been created yet").isEqualTo(1);
    }

}