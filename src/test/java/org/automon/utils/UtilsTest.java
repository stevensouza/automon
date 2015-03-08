package org.automon.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UtilsTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testBasicThrowableLabel() throws Exception {
        RuntimeException runtimeException = new RuntimeException("My RTE exception");
        assertThat(Utils.getLabel(runtimeException)).isEqualTo("java.lang.RuntimeException");
    }

    @Test
    public void testSqlThrowableLabel() throws Exception {
        SQLException sqlException = new SQLException("My SQL exception", "Login failure", 400);
        assertThat(Utils.getLabel(sqlException)).isEqualTo("java.sql.SQLException,ErrorCode=400,SQLState=Login failure");
    }

    @Test
    public void testArgNameValuePairs_Empty() throws Exception {
        JoinPoint jp = mock(JoinPoint.class);
        assertThat(Utils.getArgNameValuePairs(jp)).isEmpty();
    }

    @Test
    public void testArgNameValuePairs_ArgValueNoArgName() throws Exception {
        JoinPoint jp = mock(JoinPoint.class);
        when(jp.getArgs()).thenReturn(new Object[]{"Steve"});
        assertThat(Utils.getArgNameValuePairs(jp)).containsExactly("0: Steve");
    }

    @Test
    public void testArgNameValuePairs_ArgValueNoArgName_multiple() throws Exception {
        JoinPoint jp = mock(JoinPoint.class);
        when(jp.getArgs()).thenReturn(new Object[]{"Steve", 20});
        assertThat(Utils.getArgNameValuePairs(jp)).containsExactly("0: Steve",  "1: 20");
    }

    @Test
    public void testArgNameValuePairs_ArgValueAndArgName() throws Exception {
        JoinPoint jp = mock(JoinPoint.class);
        CodeSignature signature = mock(CodeSignature.class);
        when(jp.getSignature()).thenReturn(signature);
        when(signature.getParameterNames()).thenReturn(new String[]{"firstName"});
        when(jp.getArgs()).thenReturn(new Object[]{"Steve"});
        assertThat(Utils.getArgNameValuePairs(jp)).containsExactly("firstName: Steve");
    }

    @Test
    public void testArgNameValuePairs_ArgValueAndArgName_multiple() throws Exception {
        JoinPoint jp = mock(JoinPoint.class);
        CodeSignature signature = mock(CodeSignature.class);
        when(jp.getSignature()).thenReturn(signature);
        when(signature.getParameterNames()).thenReturn(new String[]{"firstName", "number"});
        when(jp.getArgs()).thenReturn(new Object[]{"Steve", 20});
        assertThat(Utils.getArgNameValuePairs(jp)).containsExactly("firstName: Steve", "number: 20");
    }

    @Test
    public void testArgNameValuePairsToString() throws Exception {
        List<String> args = new ArrayList<String>();
        assertThat(Utils.argNameValuePairsToString(null)).isEqualTo(Utils.UNKNOWN);
        assertThat(Utils.argNameValuePairsToString(args)).contains("Parameter");
        args.add("fname: Steve");
        args.add("lname: Souza");
        assertThat(Utils.argNameValuePairsToString(args)).contains("Parameter", "fname: Steve", "lname: Souza");
    }

    @Test
    public void testToStringWithLimit() throws Exception {
        assertThat(Utils.toStringWithLimit(null)).isEqualTo(Utils.NULL_STR);
        assertThat(Utils.toStringWithLimit("hi")).describedAs("Normal length string").isEqualTo("hi");
        StringBuilder sb = new StringBuilder();
        int SIZE=1000;
        for (int i=0;i<SIZE;i++) {
            sb.append("A");
        }
        assertThat(Utils.toStringWithLimit(sb.toString()).length()).describedAs("Long strings should be truncated").isLessThan(SIZE);
        assertThat(Utils.toStringWithLimit(sb.toString())).describedAs("String too long should be truncated").endsWith(Utils.DEFAULT_MAX_STRING_ENDING);
    }

    @Test
    public void testGetExceptionTrace() throws Exception {
        assertThat(Utils.getExceptionTrace(null)).isEqualTo(Utils.UNKNOWN);
        RuntimeException e = new RuntimeException("my exception");
        assertThat(Utils.getExceptionTrace(e)).contains("java.lang.RuntimeException: my exception");
        assertThat(Utils.getExceptionTrace(e)).contains(getClass().getName());
    }

    @Test
    public void testTokenize() throws Exception {
        String[] array = Utils.tokenize("com.jamonapi.MonitorFactory", "[.]");
        assertThat(array).containsExactly("com","jamonapi","MonitorFactory");
        array = Utils.tokenize("jamon , javasimon", ",");
        assertThat(array).containsExactly("jamon","javasimon");
    }

}