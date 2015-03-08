package org.automon.implementations;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
        assertThat(factory.getInstance("I don't exist", defaultValue)).isEqualTo(defaultValue);
    }

//    @Test
//    public void testPut1String() throws Exception {
//        String key = Jamon.class.getName();
//        factory.put(key);
//        assertThat(factory.get(key)).isInstanceOf(Jamon.class);
//    }
//
//    @Test
//    public void testPutNStrings() throws Exception {
//        String jamon = Jamon.class.getName();
//        String javaSimon = JavaSimon.class.getName();
//        String metrics = Metrics.class.getName();
//        String clazzNames = jamon+" , "+javaSimon+", "+metrics;
//        factory.put(clazzNames);
//        assertThat(factory.get(jamon)).isInstanceOf(Jamon.class);
//        assertThat(factory.get(javaSimon)).isInstanceOf(JavaSimon.class);
//        assertThat(factory.get(metrics)).isInstanceOf(Metrics.class);
//    }


}