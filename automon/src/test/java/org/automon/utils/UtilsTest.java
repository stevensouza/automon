package org.automon.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UtilsTest {

    @BeforeEach
    public void setUp() throws Exception {

    }

    @AfterEach
    public void tearDown() throws Exception {

    }

    @Test
    public void testBasicThrowableLabel() {
        RuntimeException runtimeException = new RuntimeException("My RTE exception");
        assertThat(Utils.getLabel(runtimeException)).isEqualTo("java.lang.RuntimeException");
    }

    @Test
    public void testSqlThrowableLabel() {
        SQLException sqlException = new SQLException("My SQL exception", "Login failure", 400);
        assertThat(Utils.getLabel(sqlException)).isEqualTo("java.sql.SQLException,ErrorCode=400,SQLState=Login failure");
    }

    @Test
    public void testArgNameValuePairs_Empty() {
        JoinPoint jp = mock(JoinPoint.class);
        assertThat(Utils.getArgNameValuePairs(jp)).isEmpty();
    }

    @Test
    public void testArgNameValuePairs_ArgValueNoArgName() {
        JoinPoint jp = mock(JoinPoint.class);
        when(jp.getArgs()).thenReturn(new Object[]{"Steve"});
        assertThat(Utils.getArgNameValuePairs(jp)).containsExactly("0: Steve");
    }

    @Test
    public void testArgNameValuePairs_ArgValueNoArgName_multiple() {
        JoinPoint jp = mock(JoinPoint.class);
        when(jp.getArgs()).thenReturn(new Object[]{"Steve", 20});
        assertThat(Utils.getArgNameValuePairs(jp)).containsExactly("0: Steve", "1: 20");
    }

    @Test
    public void testArgNameValuePairs_ArgValueAndArgName() {
        JoinPoint jp = mock(JoinPoint.class);
        CodeSignature signature = mock(CodeSignature.class);
        when(jp.getSignature()).thenReturn(signature);
        when(signature.getParameterNames()).thenReturn(new String[]{"firstName"});
        when(jp.getArgs()).thenReturn(new Object[]{"Steve"});
        assertThat(Utils.getArgNameValuePairs(jp)).containsExactly("firstName: Steve");
    }

    @Test
    public void testArgNameValuePairs_ArgValueAndArgName_multiple() {
        JoinPoint jp = mock(JoinPoint.class);
        CodeSignature signature = mock(CodeSignature.class);
        when(jp.getSignature()).thenReturn(signature);
        when(signature.getParameterNames()).thenReturn(new String[]{"firstName", "number"});
        when(jp.getArgs()).thenReturn(new Object[]{"Steve", 20});
        assertThat(Utils.getArgNameValuePairs(jp)).containsExactly("firstName: Steve", "number: 20");
    }

    @Test
    public void testArgNameValuePairsToString() {
        List<String> args = new ArrayList<>();
        assertThat(Utils.argNameValuePairsToString(null)).isEqualTo(Utils.UNKNOWN);
        assertThat(Utils.argNameValuePairsToString(args)).contains("Parameter");
        args.add("fname: Steve");
        args.add("lname: Souza");
        assertThat(Utils.argNameValuePairsToString(args)).contains("Parameter", "fname: Steve", "lname: Souza");
    }

    @Test
    public void testToStringWithLimit() {
        assertThat(Utils.toStringWithLimit(null)).isEqualTo(Utils.NULL_STR);
        assertThat(Utils.toStringWithLimit("hi")).describedAs("Normal length string").isEqualTo("hi");
        StringBuilder sb = new StringBuilder();
        int SIZE = 1000;
        sb.append("A".repeat(SIZE));
        assertThat(Utils.toStringWithLimit(sb.toString()).length()).describedAs("Long strings should be truncated").isLessThan(SIZE);
        assertThat(Utils.toStringWithLimit(sb.toString())).describedAs("String too long should be truncated").endsWith(Utils.DEFAULT_MAX_STRING_ENDING);
    }

    @Test
    public void testGetExceptionTrace() {
        assertThat(Utils.getExceptionTrace(null)).isEqualTo(Utils.UNKNOWN);
        RuntimeException e = new RuntimeException("my exception");
        assertThat(Utils.getExceptionTrace(e)).contains("java.lang.RuntimeException: my exception");
        assertThat(Utils.getExceptionTrace(e)).contains(getClass().getName());
    }

    @Test
    public void testTokenize() {
        String[] array = Utils.tokenize("com.jamonapi.MonitorFactory", "[.]");
        assertThat(array).containsExactly("com", "jamonapi", "MonitorFactory");
        array = Utils.tokenize("jamon , metrics", ",");
        assertThat(array).containsExactly("jamon", "metrics");
    }

    @Test
    public void testStripFileScheme() {
        assertThat(Utils.stripFileScheme("myfile.dat")).isEqualTo("myfile.dat");
        assertThat(Utils.stripFileScheme("file:myfile.dat")).isEqualTo("myfile.dat");
        assertThat(Utils.stripFileScheme("file://myfile.dat")).isEqualTo("myfile.dat");
        assertThat(Utils.stripFileScheme("file:/myfile.dat")).isEqualTo("/myfile.dat");
        assertThat(Utils.stripFileScheme("file:///myfile.dat")).isEqualTo("/myfile.dat");

        // with dir
        assertThat(Utils.stripFileScheme("file:/dir/myfile.dat")).isEqualTo("/dir/myfile.dat");
        assertThat(Utils.stripFileScheme("file://dir/myfile.dat")).isEqualTo("dir/myfile.dat");
        assertThat(Utils.stripFileScheme("file:dir/myfile.dat")).isEqualTo("dir/myfile.dat");
        assertThat(Utils.stripFileScheme("file:///dir/myfile.dat")).isEqualTo("/dir/myfile.dat");
    }

    @Test
    public void removeClassNamesFromList() {
        List<String> list = new ArrayList<>();
        list.add("MyClass1");
        list.add("MyClass2");
        list.add("com.mypackage1.MyClass1");
        list.add("com.mypackage1.MyClass2");
        Utils.removeClassNames(list);
        assertThat(list).containsExactly("MyClass1", "MyClass2");
    }

    @Test
    public void shouldHavePackageName() {
        assertThat(Utils.hasPackageName(null)).isFalse();
        assertThat(Utils.hasPackageName("NoPackage")).isFalse();
        assertThat(Utils.hasPackageName("com.package.MyClass")).isTrue();
    }

    @Test
    public void shouldCreateFirstInstance() {
        String str = Utils.createFirst("java.lang.String");
        assertThat(str).isInstanceOf(String.class);

        str = Utils.createFirst("i.do.not.Exist", "java.lang.String");
        assertThat(str).isInstanceOf(String.class);

        str = Utils.createFirst("i.do.not.Exist1", "i.do.not.Exist2");
        assertThat(str).isNull();
    }

    /**
     * Test of start method, of class StatsD.
     */
    @Test
    public void testFormatExceptionForToolsWithLimitedCharacterSet() {
        assertThat(Utils.formatExceptionForToolsWithLimitedCharacterSet(null)).describedAs("Null values should return unchanged").isNull();

        String before = "java.sql.SQLException,ErrorCode=400,SQLState=Login failure";
        String after = "java.sql.SQLException.ErrorCode 400-SQLState Login failure";
        assertThat(Utils.formatExceptionForToolsWithLimitedCharacterSet(before)).describedAs("StatsD string is not as expected (remove ,=)").isEqualTo(after);

        String plainException = "java.lang.Exception";
        assertThat(Utils.formatExceptionForToolsWithLimitedCharacterSet(plainException)).describedAs("Nonsql exceptions should have no change").isEqualTo(plainException);
    }


}