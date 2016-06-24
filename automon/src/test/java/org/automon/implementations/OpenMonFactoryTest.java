package org.automon.implementations;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class OpenMonFactoryTest {

    private OpenMonFactory factory;
    private OpenMon defaultValue = mock(OpenMon.class);

    @Before
    public void setUp() throws Exception {
        factory = new OpenMonFactory(defaultValue);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testDefault() throws Exception {
        // test valid values loaded by default with fully qualified class path.
        assertThat(factory.getInstance(OpenMonFactory.JAMON)).isInstanceOf(Jamon.class);
        assertThat(factory.getInstance(OpenMonFactory.JAVA_SIMON)).isInstanceOf(JavaSimon.class);
        assertThat(factory.getInstance(OpenMonFactory.METRICS)).isInstanceOf(Metrics.class);
        assertThat(factory.getInstance(OpenMonFactory.SYSOUT)).isInstanceOf(SysOut.class);
        assertThat(factory.getInstance(OpenMonFactory.NULL_IMP)).isInstanceOf(NullImp.class);
        assertThat(factory.getInstance(OpenMonFactory.NEW_RELIC)).isInstanceOf(NewRelicImp.class);
        assertThat(factory.getInstance(OpenMonFactory.STATSD)).isInstanceOf(StatsD.class);

        // test getting instance with the class name only (case insensitive)
        assertThat(factory.getInstance("jamon")).isInstanceOf(Jamon.class);
        assertThat(factory.getInstance("JAMon")).isInstanceOf(Jamon.class);

        assertThat(factory.getInstance("com.i.do.not.Exist")).
                describedAs("The default value should be returned if the constructor fails").
                isEqualTo(defaultValue);
    }

    @Test
    public void testAddMultiple() throws Exception {
        factory.reset();
        factory.add(OpenMonFactory.JAMON, OpenMonFactory.JAVA_SIMON);
        assertThat(factory.getInstance(OpenMonFactory.JAMON)).isInstanceOf(Jamon.class);
        assertThat(factory.getInstance(OpenMonFactory.JAVA_SIMON)).isInstanceOf(JavaSimon.class);
        assertThat(factory.getInstance("I_DO_NOT_EXIST")).isEqualTo(defaultValue);
    }

    @Test
    public void testGetLastToken() throws Exception {
        assertThat(OpenMonFactory.getJustClassName("com.mypackage.Jamon")).isEqualTo("Jamon");
        assertThat(OpenMonFactory.getJustClassName("Jamon")).isEqualTo("Jamon");
    }

    @Test
    public void testToString() throws Exception {
        assertThat(factory.toString()).contains("jamon, javasimon, metrics, newrelicimp, nullimp, statsd, sysout");
    }

    @Test
    public void testGetFirstInstance() throws Exception {
        assertThat(factory.getFirstInstance()).isNotNull();
        assertThat(factory.getFirstInstance()).isNotEqualTo(defaultValue);
    }

}