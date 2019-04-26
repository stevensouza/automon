/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.automon.implementations;

import org.automon.utils.AutomonPropertiesLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author stevesouza
 */
public class StatsDTest {

    private StatsD statsd;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        if (statsd != null) {
            statsd.close();
        }
    }


    @Test
    public void testDefaultProperties() {
        StatsD.StatsDPropsLoader loader = new StatsD.StatsDPropsLoader(new AutomonPropertiesLoader());
        assertThat(loader.getPrefix()).describedAs("StatsD Prefix default is not correct").isEqualTo(StatsD.StatsDPropsLoader.PREFIX_VALUE);
        assertThat(loader.getHostName()).describedAs("StatsD hostname default is not correct").isEqualTo(StatsD.StatsDPropsLoader.HOSTNAME_VALUE);
        assertThat(loader.getPort()).describedAs("StatsD port default is not correct").isEqualTo(Integer.parseInt(StatsD.StatsDPropsLoader.PORT_VALUE));
    }

    @Test
    public void testPopulatePropertiesFromConfigFile() {
        StatsD.StatsDPropsLoader loader = new StatsD.StatsDPropsLoader(new AutomonPropertiesLoader("automon.xml"));
        assertThat(loader.getPrefix()).describedAs("StatsD Prefix from config file is not correct").isEqualTo("mytestprefix");
        assertThat(loader.getHostName()).describedAs("StatsD hostname from config file is not correct").isEqualTo("mytesthostname");
        assertThat(loader.getPort()).describedAs("StatsD port from config file is not correct").isEqualTo(9999);
    }
}
