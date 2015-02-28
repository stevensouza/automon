package org.automon.implementations;

import org.javasimon.Counter;
import org.javasimon.SimonManager;
import org.javasimon.Split;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class JavaSimonTest {
    private JavaSimon openMon = new JavaSimon();
    private static final String LABEL =  "helloWorld.timer";
    private static final String EXCEPTION = "org.automon.MyException";

    @Before
    public void setUp() throws Exception {
        SimonManager.clear();
    }

    @After
    public void tearDown() throws Exception {
        SimonManager.clear();
    }

    @Test
    public void testStart() throws Exception {
        Split mon = openMon.start(LABEL);
        assertThat(mon.getStopwatch().getActive()).describedAs("The monitor should have been started").isEqualTo(1);
        assertThat(mon.getStopwatch().getCounter()).describedAs("The monitor should not have finished").isEqualTo(0);
    }

    @Test
    public void testStop() throws Exception {
        Split mon = openMon.start(LABEL);
        openMon.stop(mon);
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

    @Test
    public void testShouldBeEnabledByDefault() throws Exception {
        assertThat(openMon.isEnabled()).describedAs("Should be enabled by default").isTrue();
    }

    @Test
    public void testEnableDisable() throws Exception {
        openMon.enable(false);
        assertThat(openMon.isEnabled()).describedAs("Enabled status should equal Javasimon's status").isEqualTo(SimonManager.isEnabled());
        assertThat(openMon.isEnabled()).describedAs("Status should be disabled").isFalse();

        openMon.enable(true);
        assertThat(openMon.isEnabled()).describedAs("Enabled status should equal Javasimon's status").isEqualTo(SimonManager.isEnabled());
        assertThat(openMon.isEnabled()).describedAs("Status should be enabled").isTrue();
    }

}