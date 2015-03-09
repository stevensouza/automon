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
        assertThat(factory.getInstance(OpenMonFactory.METRICS)).isEqualTo(defaultValue);
    }

    @Test
    public void testGetLastToken() throws Exception {
        assertThat(OpenMonFactory.getJustClassName("com.mypackage.Jamon")).isEqualTo("Jamon");
        assertThat(OpenMonFactory.getJustClassName("Jamon")).isEqualTo("Jamon");
    }


}