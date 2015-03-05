package org.automon.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AutomonExpirableTest {

    @Test
    public void testDefaultState() throws Exception {
        AutomonExpirable expirable = new AutomonExpirable();
        assertThat(expirable.getArgNamesAndValues()).isNull();
        assertThat(expirable.getThrowable()).isNull();
        assertThat(expirable.toString()).isNotNull();
    }

    @Test
    public void testSetArgNamesAndValues() throws Exception {
        AutomonExpirable expirable = new AutomonExpirable();
        List<String> args = new ArrayList<String>();
        args.add("fname: Steve");
        expirable.setArgNamesAndValues(args);
        assertThat(expirable.getArgNamesAndValues()).isEqualTo(args);
        assertThat(expirable.toString()).contains("fname: Steve");
    }


    @Test
    public void testSetThrowable() throws Exception {
        AutomonExpirable expirable = new AutomonExpirable();
        Throwable t = new RuntimeException("my exception");
        expirable.setThrowable(t);
        assertThat(expirable.getThrowable()).isEqualTo(t);
        assertThat(expirable.toString()).contains("java.lang.RuntimeException: my exception", getClass().getName());
    }

    @Test
    public void testThrowableAndArgs() throws Exception {
        AutomonExpirable expirable = new AutomonExpirable();

        List<String> args = new ArrayList<String>();
        args.add("fname: Steve");
        expirable.setArgNamesAndValues(args);

        Throwable t = new RuntimeException("my exception");
        expirable.setThrowable(t);

        assertThat(expirable.toString()).contains("fname: Steve");
        assertThat(expirable.toString()).contains("java.lang.RuntimeException: my exception", getClass().getName());
    }
}