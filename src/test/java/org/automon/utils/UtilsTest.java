package org.automon.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

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
}