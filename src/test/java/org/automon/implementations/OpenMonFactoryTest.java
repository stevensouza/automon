package org.automon.implementations;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class OpenMonFactoryTest {

    private OpenMonFactory factory = new OpenMonFactory();

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testDefault() throws Exception {
        OpenMon defaultValue = mock(OpenMon.class);
        assertThat(factory.getInstance(OpenMonFactory.JAMON, defaultValue)).isInstanceOf(Jamon.class);
        assertThat(factory.getInstance(OpenMonFactory.JAVA_SIMON, defaultValue)).isInstanceOf(JavaSimon.class);
        assertThat(factory.getInstance(OpenMonFactory.METRICS, defaultValue)).isInstanceOf(Metrics.class);
        assertThat(factory.getInstance(OpenMonFactory.SYSOUT, defaultValue)).isInstanceOf(SysOut.class);
        assertThat(factory.getInstance(OpenMonFactory.NULL_IMP, defaultValue)).isInstanceOf(NullImp.class);
        assertThat(factory.getInstance("jamon", defaultValue)).isInstanceOf(Jamon.class);
        assertThat(factory.getInstance("JAMon", defaultValue)).isInstanceOf(Jamon.class);
        assertThat(factory.getInstance("I don't exist", defaultValue)).isEqualTo(defaultValue);

    }

    @Test
    public void testGetLastToken() throws Exception {
        assertThat(OpenMonFactory.getJustClassName("com.mypackage.Jamon")).isEqualTo("Jamon");
        assertThat(OpenMonFactory.getJustClassName("Jamon")).isEqualTo("Jamon");
    }


}