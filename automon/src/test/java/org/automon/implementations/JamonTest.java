package org.automon.implementations;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.aspectj.lang.JoinPoint;
import org.automon.utils.AutomonExpirable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JamonTest {
    private Jamon openMon = new Jamon();
    private JoinPoint jp = mock(JoinPoint.class);
    private JoinPoint.StaticPart staticPart = mock(JoinPoint.StaticPart .class);

    @Before
    public void setUp() throws Exception {
        MonitorFactory.reset();
        when(staticPart.toString()).thenReturn(SharedConstants.LABEL);
    }

    @After
    public void tearDown() throws Exception {
        MonitorFactory.reset();
    }

    @Test
    public void testStart() throws Exception {
        Monitor mon = openMon.start(staticPart);
        assertThat(mon.getLabel()).describedAs("The label should match passed in label").isEqualTo(SharedConstants.LABEL);
        assertThat(mon.getUnits()).describedAs("The units should be for time").isEqualTo("ms.");
        assertThat(mon.getActive()).describedAs("The monitor should have been started").isEqualTo(1);
    }

    @Test
    public void testStop() throws Exception {
        Monitor mon = openMon.start(staticPart);
        openMon.stop(mon);
        assertThat(mon.getLabel()).describedAs("The label should match passed in label").isEqualTo(SharedConstants.LABEL);
        assertThat(mon.getActive()).describedAs("The monitor should not be running").isEqualTo(0);
        assertThat(mon.getHits()).describedAs("The monitor should have finished and recorded hits").isEqualTo(1);
    }

    @Test
    public void testStopWithException() throws Exception {
        Monitor mon = openMon.start(staticPart);
        openMon.stop(mon, new RuntimeException("my exception"));
        assertThat(mon.getLabel()).describedAs("The label should match passed in label").isEqualTo(SharedConstants.LABEL);
        assertThat(mon.getActive()).describedAs("The monitor should not be running").isEqualTo(0);
        assertThat(mon.getHits()).describedAs("The monitor should have finished and recorded hits").isEqualTo(1);
    }

    @Test
    public void testException() throws Exception {
        assertThat(MonitorFactory.exists(SharedConstants.EXCEPTION_LABEL, "Exception")).describedAs("The exception monitor should not exist yet").isFalse();
        openMon.exception(jp, SharedConstants.EXCEPTION);
        assertThat(MonitorFactory.getMonitor(SharedConstants.EXCEPTION_LABEL, "Exception").getHits()).describedAs("One exception should have been thrown").isEqualTo(1);
        assertThat(MonitorFactory.getMonitor(OpenMon.EXCEPTION_LABEL, "Exception").getHits()). describedAs("One exception should have been thrown").isEqualTo(1);

        Map<Throwable, AutomonExpirable> map = openMon.getExceptionsMap();
        assertThat(map.get(SharedConstants.EXCEPTION).getThrowable()).describedAs("Throwable should have been set").isEqualTo(SharedConstants.EXCEPTION);
        assertThat(map.get(SharedConstants.EXCEPTION).getArgNamesAndValues()).describedAs("Arg names and values should have been set").isNotNull();
    }

}