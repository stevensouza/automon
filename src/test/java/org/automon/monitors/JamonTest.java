package org.automon.monitors;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class JamonTest {
    private Jamon openMon = new Jamon();
    private static final String LABEL =  "helloWorld.timer";
    private static final String EXCEPTION = "org.automon.MyException";

    @Before
    public void setUp() throws Exception {
        MonitorFactory.reset();
    }

    @After
    public void tearDown() throws Exception {
        MonitorFactory.reset();
    }

    @Test
    public void testStart() throws Exception {
        Monitor mon = openMon.start(LABEL);
        assertThat(mon.getLabel()).describedAs("The label should match passed in label").isEqualTo(LABEL);
        assertThat(mon.getUnits()).describedAs("The units should be for time").isEqualTo("ms.");
        assertThat(mon.getActive()).describedAs("The monitor should have been started").isEqualTo(1);
    }

    @Test
    public void testStop() throws Exception {
        Monitor mon = openMon.start(LABEL);
        openMon.stop(mon);
        assertThat(mon.getLabel()).describedAs("The label should match passed in label").isEqualTo(LABEL);
        assertThat(mon.getActive()).describedAs("The monitor should not be running").isEqualTo(0);
        assertThat(mon.getHits()).describedAs("The monitor should have finished and recorded hits").isEqualTo(1);
    }

    @Test
    public void testException() throws Exception {
        assertThat(MonitorFactory.exists(EXCEPTION, "Exception")).describedAs("The exception monitor should not exist yet").isFalse();
        openMon.exception(EXCEPTION);
        assertThat(MonitorFactory.exists(EXCEPTION, "Exception")).describedAs("The exception monitor should exist").isTrue();
        assertThat(MonitorFactory.getMonitor(EXCEPTION, "Exception").getLabel()).
                describedAs("The label should match passed in label").isEqualTo(EXCEPTION);
    }



}