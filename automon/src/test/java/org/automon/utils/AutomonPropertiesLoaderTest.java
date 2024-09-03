package org.automon.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AutomonPropertiesLoaderTest {

    @BeforeEach
    public void setUp() throws Exception {

    }

    @AfterEach
    public void tearDown() throws Exception {

    }

    @Test
    public void testDefaults() {
        AutomonPropertiesLoader loader = new AutomonPropertiesLoader();
        Properties properties = loader.getProperties();
        assertThat(properties.getProperty(AutomonPropertiesLoader.CONFIGURED_OPEN_MON)).isEmpty();
    }


    @Test
    public void testNoExists() {
        AutomonPropertiesLoader loader = new AutomonPropertiesLoader();
        Properties properties = loader.getProperties();
        assertThat(properties.getProperty("I_DO_NOT_EXIST")).isNull();
    }


    @Test
    public void testFromConfigFile() {
        AutomonPropertiesLoader loader = new AutomonPropertiesLoader("automon.xml");
        Properties properties = loader.getProperties();
        assertThat(properties.getProperty(AutomonPropertiesLoader.CONFIGURED_OPEN_MON)).isEqualTo("org.mypackage.MyOpenMon");
    }


    @Test
    public void testFromConfigFileWithScheme1() {
        AutomonPropertiesLoader loader = new AutomonPropertiesLoader("file:automon.xml");
        Properties properties = loader.getProperties();
        assertThat(properties.getProperty(AutomonPropertiesLoader.CONFIGURED_OPEN_MON)).isEqualTo("org.mypackage.MyOpenMon");
    }

    @Test
    public void testFromConfigFileWithScheme2() {
        AutomonPropertiesLoader loader = new AutomonPropertiesLoader("file://automon.xml");
        Properties properties = loader.getProperties();
        assertThat(properties.getProperty(AutomonPropertiesLoader.CONFIGURED_OPEN_MON)).isEqualTo("org.mypackage.MyOpenMon");
    }

    @Test
    public void testFromConfigFileMimicNullFile() {
        // mimics when property System.getProperty("org.aspectj.weaver.loadtime.configuration") returns null.
        AutomonPropertiesLoader loader = new AutomonPropertiesLoader((String) null, "automon.xml");
        Properties properties = loader.getProperties();
        assertThat(properties.getProperty(AutomonPropertiesLoader.CONFIGURED_OPEN_MON)).isEqualTo("org.mypackage.MyOpenMon");
    }

    @Test
    public void testFromConfigFilesThatDoNotExist() {
        AutomonPropertiesLoader loader = new AutomonPropertiesLoader("automon1.properties", "automon2.properties");
        Properties properties = loader.getProperties();
        assertThat(properties.getProperty(AutomonPropertiesLoader.CONFIGURED_OPEN_MON)).isEmpty();
    }


    @Test
    public void testFromSystemProperties() {
        AutomonPropertiesLoader.SysProperty sysProperty = mock(AutomonPropertiesLoader.SysProperty.class);
        Properties props = new Properties();
        props.put(AutomonPropertiesLoader.CONFIGURED_OPEN_MON, "com.mypackage.SystemPropertyPrecedence");
        when(sysProperty.getProperties()).thenReturn(props);
        when(sysProperty.getProperty(AutomonPropertiesLoader.CONFIGURED_OPEN_MON)).thenReturn("com.mypackage.SystemPropertyPrecedence");

        AutomonPropertiesLoader loader = new AutomonPropertiesLoader(sysProperty, "automon.xml");
        Properties properties = loader.getProperties();
        assertThat(properties.getProperty(AutomonPropertiesLoader.CONFIGURED_OPEN_MON)).isEqualTo("com.mypackage.SystemPropertyPrecedence");
    }

    @Test
    public void testFromSystemProperties_StatsD() {
        AutomonPropertiesLoader.SysProperty sysProperty = mock(AutomonPropertiesLoader.SysProperty.class);
        Properties props = new Properties();
        props.put("org.automon.statsd.noexist", "mynoexist_value");
        when(sysProperty.getProperties()).thenReturn(props);
        when(sysProperty.getProperty("org.automon.statsd.noexist")).thenReturn("mynoexist_value");

        AutomonPropertiesLoader loader = new AutomonPropertiesLoader(sysProperty, "automon.xml");
        Properties properties = loader.getProperties();
        assertThat(properties.getProperty("org.automon.statsd.noexist")).isEqualTo("mynoexist_value");
    }

    @Test
    public void testGetBoolean_True() {
        AutomonPropertiesLoader loader = new AutomonPropertiesLoader("automon.xml");

        assertThat(loader.getBoolean("i do not exist")).
                describedAs("If a property does not exist 'true' should be returned").
                isTrue();
        assertThat(loader.getBoolean("org.automon.tracing.RequestIdAspect.enable")).
                describedAs("This uses uppercase TRUE which should return boolean true").
                isTrue();
    }

    @Test
    public void testGetBoolean_False() {
        AutomonPropertiesLoader loader = new AutomonPropertiesLoader("automon.xml");

        assertThat(loader.getBoolean("org.automon.tracing.BasicContextTracingAspect.enableLogging")).
                isFalse();
        assertThat(loader.getBoolean("org.automon.tracing.BasicContextTracingAspect.test")).
            describedAs("Any value other than 'true' is considered false").
            isFalse();
    }

    @Test
    public void testInvalidFile() {
        AutomonPropertiesLoader loader = new AutomonPropertiesLoader("invalid_file.xml");
        assertThat(loader.getProperties().getProperty(AutomonPropertiesLoader.CONFIGURED_OPEN_MON)).isEqualTo("");
    }

}