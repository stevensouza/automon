package org.automon.implementations;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class MetricsTest {
    private final Metrics openMon = new Metrics();
    private final JoinPoint jp = mock(JoinPoint.class);
    private final JoinPoint.StaticPart staticPart = mock(JoinPoint.StaticPart.class);

    @BeforeEach
    public void setUp() throws Exception {
        Metrics.setMetricRegistry(new MetricRegistry());
        JoinPoint.StaticPart staticPart = mock(JoinPoint.StaticPart.class);
        when(staticPart.toString()).thenReturn(SharedConstants.LABEL);
    }

    @AfterEach
    public void tearDown() throws Exception {
        Metrics.setMetricRegistry(new MetricRegistry());
    }

    @Test
    public void testStart() {
        Timer mon = openMon.start(staticPart);
        assertThat(mon.getCount()).describedAs("The timer shouldn't have completed").isEqualTo(0);
    }

    @Test
    public void testStop() {
        Timer mon = openMon.start(staticPart);
        openMon.stop(mon);
        assertThat(mon.getCount()).describedAs("The timer should have completed/been stopped").isEqualTo(1);
    }

    @Test
    public void testStopWithException() {
        Timer mon = openMon.start(staticPart);
        openMon.stop(mon, new RuntimeException("my exception"));
        assertThat(mon.getCount()).describedAs("The timer should have completed/been stopped").isEqualTo(1);
    }

    @Test
    public void testException() {
        MetricRegistry metricRegistry = Metrics.getMetricRegistry();
        assertThat(metricRegistry.counter(SharedConstants.EXCEPTION_LABEL).getCount()).describedAs("No exception should exist yet").isEqualTo(0);
        assertThat(metricRegistry.counter(OpenMon.EXCEPTION_LABEL).getCount()).describedAs("No general exception should exist yet").isEqualTo(0);
        openMon.exception(jp, SharedConstants.EXCEPTION);
        assertThat(metricRegistry.counter(SharedConstants.EXCEPTION_LABEL).getCount()).describedAs("An exception should now exist").isEqualTo(1);
        assertThat(metricRegistry.counter(OpenMon.EXCEPTION_LABEL).getCount()).describedAs("An general exception should now exist").isEqualTo(1);
    }


    @Test
    public void setMetricRegistry() {
        MetricRegistry newMetricRegistry = new MetricRegistry();
        Metrics.setMetricRegistry(newMetricRegistry);
        assertThat(Metrics.getMetricRegistry()).isEqualTo(newMetricRegistry);
    }

}