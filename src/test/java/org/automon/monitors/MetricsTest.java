package org.automon.monitors;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.aspectj.lang.JoinPoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class MetricsTest {
    private Metrics openMon = new Metrics();
    private JoinPoint jp = mock(JoinPoint.class);
    private static final String LABEL =  "helloWorld.timer";
    private static final Exception EXCEPTION = new RuntimeException("my exception");
    private static final String EXCEPTION_LABEL = EXCEPTION.getClass().getName();

    @Before
    public void setUp() throws Exception {
        JoinPoint.StaticPart staticPart = mock(JoinPoint.StaticPart .class);
        when(jp.getStaticPart()).thenReturn(staticPart);
        when(staticPart.toString()).thenReturn(LABEL);
    }
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testStart() throws Exception {
        Timer mon = openMon.start(jp);
        assertThat(mon.getCount()).describedAs("The timer shouldn't have completed").isEqualTo(0);
    }

    @Test
    public void testStop() throws Exception {
        Timer mon = openMon.start(jp);
        openMon.stop(mon);
        assertThat(mon.getCount()).describedAs("The timer should have completed/been stopped").isEqualTo(1);
    }

    @Test
    public void testStopWithException() throws Exception {
        Timer mon = openMon.start(jp);
        openMon.stop(mon, new RuntimeException("my exception"));
        assertThat(mon.getCount()).describedAs("The timer should have completed/been stopped").isEqualTo(1);
    }

    @Test
    public void testException() throws Exception {
        MetricRegistry metricRegistry = openMon.getMetricRegistry();
        assertThat(metricRegistry.counter(EXCEPTION_LABEL).getCount()).describedAs("No exception should exist yet").isEqualTo(0);
        openMon.exception(jp, EXCEPTION);
        assertThat(metricRegistry.counter(EXCEPTION_LABEL).getCount()).describedAs("An exception should now exist").isEqualTo(1);
    }


    @Test
    public void setMetricRegistry() throws Exception {
        MetricRegistry newMetricRegistry = new MetricRegistry();
        openMon.setMetricRegistry(newMetricRegistry);
        assertThat(openMon.getMetricRegistry()).isEqualTo(newMetricRegistry);
    }

}