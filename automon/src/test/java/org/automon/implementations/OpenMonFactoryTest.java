package org.automon.implementations;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class OpenMonFactoryTest {

    private OpenMonFactory factory;
    private final OpenMon defaultValue = mock(OpenMon.class);

    @BeforeEach
    public void setUp() throws Exception {
        factory = new OpenMonFactory(defaultValue);
    }

    @AfterEach
    public void tearDown() throws Exception {

    }

    @Test
    public void testDefault() {
        // test valid values loaded by default with fully qualified class path.
        assertThat(factory.getInstance(OpenMonFactory.JAMON)).isInstanceOf(Jamon.class);
        assertThat(factory.getInstance(OpenMonFactory.JAVA_SIMON)).isInstanceOf(JavaSimon.class);
        assertThat(factory.getInstance(OpenMonFactory.METRICS)).isInstanceOf(Metrics.class);
        assertThat(factory.getInstance(OpenMonFactory.SYSOUT)).isInstanceOf(SysOut.class);
        assertThat(factory.getInstance(OpenMonFactory.NULL_IMP)).isInstanceOf(NullImp.class);
        assertThat(factory.getInstance(OpenMonFactory.NEW_RELIC)).isInstanceOf(NewRelicImp.class);

        // test getting instance with the class name only (case insensitive)
        assertThat(factory.getInstance("jamon")).isInstanceOf(Jamon.class);
        assertThat(factory.getInstance("JAMon")).isInstanceOf(Jamon.class);

        assertThat(factory.getInstance("com.i.do.not.Exist")).
                describedAs("The default value should be returned if the constructor fails").
                isEqualTo(defaultValue);
    }

    @Test
    public void testAddMultiple() {
        factory.reset();
        factory.add(OpenMonFactory.JAMON, OpenMonFactory.JAVA_SIMON);
        assertThat(factory.getInstance(OpenMonFactory.JAMON)).isInstanceOf(Jamon.class);
        assertThat(factory.getInstance(OpenMonFactory.JAVA_SIMON)).isInstanceOf(JavaSimon.class);
        assertThat(factory.getInstance("I_DO_NOT_EXIST")).isEqualTo(defaultValue);
    }

    @Test
    public void testGetLastToken() {
        assertThat(OpenMonFactory.getJustClassName("com.mypackage.Jamon")).isEqualTo("Jamon");
        assertThat(OpenMonFactory.getJustClassName("Jamon")).isEqualTo("Jamon");
    }

    @Test
    public void testToString()  {
        assertThat(factory.toString()).contains("jamon, javasimon, metrics, micrometer, newrelicimp, nullimp, sysout");
    }

    @Test
    public void testGetFirstInstance()  {
        assertThat(factory.getFirstInstance()).isNotNull();
        assertThat(factory.getFirstInstance()).isNotEqualTo(defaultValue);
    }

}