package org.automon.implementations;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.aspectj.lang.JoinPoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class MicrometerTest {
    private Micrometer openMon = new Micrometer();
    private JoinPoint jp = mock(JoinPoint.class);
    private JoinPoint.StaticPart staticPart = mock(JoinPoint.StaticPart.class);

    @Before
    public void setUp() throws Exception {
        Micrometer.setMeterRegistry(new SimpleMeterRegistry());
        when(staticPart.toString()).thenReturn(SharedConstants.LABEL);
    }

    @After
    public void tearDown() throws Exception {
        Micrometer.setMeterRegistry(new SimpleMeterRegistry());
    }

    @Test
    public void testStart() throws Exception {
        TimerContext mon = openMon.start(staticPart);
        assertThat(openMon.getTimer(staticPart.toString()).count()).describedAs("A timer that wasn't started should not have a count").isEqualTo(0);
    }

    @Test
    public void testStop() throws Exception {
        TimerContext mon = openMon.start(staticPart);
        sleep(250);
        openMon.stop(mon);
        assertThat(openMon.getTimer(staticPart.toString()).count())
                .describedAs("The timer should have completed/been stopped").isEqualTo(1);
        assertThat(openMon.getTimer(staticPart.toString()).totalTime(TimeUnit.MILLISECONDS))
                .describedAs("The timer should have expected time").isGreaterThanOrEqualTo(250);
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTimerNotNull() {
        assertThat(openMon.getTimer(staticPart.toString())).isNotNull();
    }

    @Test
    public void testCounterNotNull() {
        assertThat(openMon.getCounter(SharedConstants.EXCEPTION_LABEL)).isNotNull();
    }

    @Test
    public void testStopWithException() throws Exception {
        TimerContext mon = openMon.start(staticPart);
        openMon.stop(mon, new RuntimeException("my exception"));
        assertThat(openMon.getTimer(staticPart.toString()).count())
                .describedAs("The timer should have completed/been stopped").isEqualTo(1);
    }

    @Test
    public void testException() throws Exception {
        assertThat(openMon.getCounter(SharedConstants.EXCEPTION_LABEL).count())
                .describedAs("No exception should exist yet").isEqualTo(0);
        openMon.exception(jp, SharedConstants.EXCEPTION);
        assertThat(openMon.getCounter(SharedConstants.EXCEPTION_LABEL).count())
                .describedAs("One exception should exist").isEqualTo(1);
    }


    @Test
    public void setMetricRegistry() throws Exception {
        MeterRegistry registry = new SimpleMeterRegistry();
        Micrometer.setMeterRegistry(registry);
        assertThat(Micrometer.getMeterRegistry()).isEqualTo(registry);
    }

}