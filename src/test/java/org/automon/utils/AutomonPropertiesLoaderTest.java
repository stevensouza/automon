package org.automon.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AutomonPropertiesLoaderTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testDefaults() throws Exception {
        AutomonPropertiesLoader loader = new AutomonPropertiesLoader();
        Properties properties = loader.getProperties();
        assertThat(properties.getProperty(AutomonPropertiesLoader.CONFIGURED_OPEN_MON)).isEqualTo(AutomonPropertiesLoader.DEFAULT_OPEN_MON);
    }

    @Test
    public void testFromConfigFile() throws Exception {
        AutomonPropertiesLoader loader = new AutomonPropertiesLoader("automon.xml");
        Properties properties = loader.getProperties();
        assertThat(properties.getProperty(AutomonPropertiesLoader.CONFIGURED_OPEN_MON)).isEqualTo("org.mypackage.MyOpenMon");
    }


    @Test
    public void testFromConfigFileWithScheme1() throws Exception {
        AutomonPropertiesLoader loader = new AutomonPropertiesLoader("file:automon.xml");
        Properties properties = loader.getProperties();
        assertThat(properties.getProperty(AutomonPropertiesLoader.CONFIGURED_OPEN_MON)).isEqualTo("org.mypackage.MyOpenMon");
    }

    @Test
    public void testFromConfigFileWithScheme2() throws Exception {
        AutomonPropertiesLoader loader = new AutomonPropertiesLoader("file://automon.xml");
        Properties properties = loader.getProperties();
        assertThat(properties.getProperty(AutomonPropertiesLoader.CONFIGURED_OPEN_MON)).isEqualTo("org.mypackage.MyOpenMon");
    }

    @Test
    public void testFromConfigFileMimicNullFile() throws Exception {
        // mimics when property System.getProperty("org.aspectj.weaver.loadtime.configuration") returns null.
        AutomonPropertiesLoader loader = new AutomonPropertiesLoader((String) null, "automon.xml");
        Properties properties = loader.getProperties();
        assertThat(properties.getProperty(AutomonPropertiesLoader.CONFIGURED_OPEN_MON)).isEqualTo("org.mypackage.MyOpenMon");
    }

    @Test
    public void testFromConfigFilesThatDoNotExist() throws Exception {
        AutomonPropertiesLoader loader = new AutomonPropertiesLoader("automon1.properties", "automon2.properties");
        Properties properties = loader.getProperties();
        assertThat(properties.getProperty(AutomonPropertiesLoader.CONFIGURED_OPEN_MON)).isEqualTo(AutomonPropertiesLoader.DEFAULT_OPEN_MON);
    }


    @Test
    public void testFromSystemProperties() throws Exception {
        AutomonPropertiesLoader.SysProperty sysProperty = mock(AutomonPropertiesLoader.SysProperty.class);
        when(sysProperty.getProperty(AutomonPropertiesLoader.CONFIGURED_OPEN_MON)).thenReturn("com.mypackage.SystemPropertyPrecedence");
        AutomonPropertiesLoader loader = new AutomonPropertiesLoader(sysProperty, "automon.xml");
        Properties properties = loader.getProperties();
        assertThat(properties.getProperty(AutomonPropertiesLoader.CONFIGURED_OPEN_MON)).isEqualTo("com.mypackage.SystemPropertyPrecedence");
    }
}