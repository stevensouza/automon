/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.automon.implementations;

import org.junit.Test;


import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author stevesouza
 */
public class StatsDTest {

    /**
     * Test of start method, of class StatsD.
     */
    @Test
    public void testFormatExceptionForStatsD() {
        StatsD statsd = new StatsD();

        assertThat(statsd.formatExceptionForStatsD(null)).describedAs("Null values should return unchanged").isNull();

        String before = "java.sql.SQLException,ErrorCode=400,SQLState=Login failure";
        String after = "java.sql.SQLException.ErrorCode 400-SQLState Login failure";
        assertThat(statsd.formatExceptionForStatsD(before)).describedAs("StatsD string is not as expected (remove ,=)").isEqualTo(after);

        String plainException = "java.lang.Exception";
        assertThat(statsd.formatExceptionForStatsD(plainException)).describedAs("Nonsql exceptions should have no change").isEqualTo(plainException);
    }

}
