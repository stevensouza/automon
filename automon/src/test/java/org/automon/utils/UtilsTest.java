package org.automon.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.automon.utils.Utils.LINE_SEPARATOR;
import static org.automon.utils.Utils.UNKNOWN;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UtilsTest {

    private static final AutomonPropertiesLoader originalPropertiesLoader = Utils.AUTOMON_PROPERTIES;

    @BeforeEach
    public void setUp() throws Exception {
        Utils.AUTOMON_PROPERTIES = originalPropertiesLoader;
    }

    @AfterEach
    public void tearDown() throws Exception {
        Utils.AUTOMON_PROPERTIES = originalPropertiesLoader;
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
        assertThat(Utils.paramsToMap(jp)).isEmpty();
    }

    @Test
    public void testArgNameValuePairs_ArgValueNoArgName() {
        JoinPoint jp = mock(JoinPoint.class);
        when(jp.getArgs()).thenReturn(new Object[]{"Steve"});

        assertThat(Utils.paramsToMap(jp)).containsExactly(entry("param0", "Steve"));
    }

    @Test
    public void testArgNameValuePairs_ArgValueNoArgName_multiple() {
        JoinPoint jp = mock(JoinPoint.class);
        when(jp.getArgs()).thenReturn(new Object[]{"Steve", 20});
        assertThat(Utils.paramsToMap(jp)).containsExactly(entry("param0", "Steve"), entry("param1", 20));
    }

    @Test
    public void testArgNameValuePairs_ArgValueAndArgName() {
        JoinPoint jp = mock(JoinPoint.class);
        CodeSignature signature = mock(CodeSignature.class);
        when(jp.getSignature()).thenReturn(signature);
        when(signature.getParameterNames()).thenReturn(new String[]{"firstName"});
        when(jp.getArgs()).thenReturn(new Object[]{"Steve"});
        assertThat(Utils.paramsToMap(jp)).containsExactly(entry("firstName", "Steve"));
    }

    @Test
    public void testArgNameValuePairs_ArgValueAndArgName_multiple() {
        JoinPoint jp = mock(JoinPoint.class);
        CodeSignature signature = mock(CodeSignature.class);
        when(jp.getSignature()).thenReturn(signature);
        when(signature.getParameterNames()).thenReturn(new String[]{"firstName", "number"});
        when(jp.getArgs()).thenReturn(new Object[]{"Steve", 20});
        assertThat(Utils.paramsToMap(jp)).containsExactly(entry("firstName", "Steve"), entry("number", 20));
    }

    @Test
    void testParamsToMap_NullParameterNames() {
        // Note this can happen if code is not compiled to have method argument/parameter names..
        // i.e. class.method(String name) - name wouldn't be available in code. in this case param0 would
        // would be used instead. The example below simulates a 2 arg method.
        // Arrange
        JoinPoint joinPoint = mock(JoinPoint.class);
        CodeSignature codeSignature = mock(CodeSignature.class); // Mock as CodeSignature
        when(joinPoint.getSignature()).thenReturn(codeSignature);
        when(codeSignature.getParameterNames()).thenReturn(null); // Explicitly set parameterNames to null
        Object[] argValues = {"value1", 2};
        when(joinPoint.getArgs()).thenReturn(argValues);

        // Act
        Map<String, Object> result = Utils.paramsToMap(joinPoint);

        // Assert
        assertThat(result).containsExactly(
                entry("param0", "value1"),
                entry("param1", 2)
        );
    }

    @Test
    void testArgNameValuePairsNull() {
        String result = Utils.argNameValuePairsToString(null);
        assertThat(result).isEqualTo(UNKNOWN);
    }

    @Test
    void testArgNameValuePairsEmptyArgs() {
        Map<String, Object> args = new HashMap<>();
        String result = Utils.argNameValuePairsToString(args);
        assertThat(result).isEqualTo("=== Parameters ===" + LINE_SEPARATOR + LINE_SEPARATOR);
    }

    @Test
    void testArgNameValuePairsMultipleArgs() {
        // note any map can be used.  LinkedHashMap was used in this test to guarantee
        // the resulting string is in the correct order.
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("filename", "report.txt");
        args.put("max_records", 1000);
        args.put("user_id", "johndoe123");

        String expected = "=== Parameters ===" + LINE_SEPARATOR +
                "filename=report.txt" + LINE_SEPARATOR +
                "max_records=1000" + LINE_SEPARATOR +
                "user_id=johndoe123" + LINE_SEPARATOR +
                LINE_SEPARATOR;

        String result = Utils.argNameValuePairsToString(args);
        assertThat(result).isEqualTo(expected);
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
        assertThat(Utils.getExceptionTrace(null)).isEqualTo(UNKNOWN);
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

    @Test
    public void testEnableDisableNoValues() {
        assertThat(Utils.shouldEnable(null)).isTrue();
        assertThat(Utils.shouldEnable("")).isTrue();
        assertThat(Utils.shouldEnable("MyClass")).isTrue();

        assertThat(Utils.shouldEnableLogging(null)).isTrue();
        assertThat(Utils.shouldEnableLogging("")).isTrue();
        assertThat(Utils.shouldEnableLogging("MyClass")).isTrue();
    }

    @Test
    public void testEnableDisableFromConfigFile() {
        Utils.AUTOMON_PROPERTIES = new AutomonPropertiesLoader("automon.xml");

        // BasicContextTracingAspect assertions
        assertThat(Utils.shouldEnable("org.automon.aspects.tracing.aspectj.BasicContextTracingAspect")).isFalse();
        assertThat(Utils.shouldEnableLogging("org.automon.aspects.tracing.aspectj.BasicContextTracingAspect")).isFalse();

        // RequestIdAspect assertions
        assertThat(Utils.shouldEnable("org.automon.aspects.tracing.aspectj.RequestIdAspect")).isTrue();
        assertThat(Utils.shouldEnableLogging("org.automon.aspects.tracing.aspectj.RequestIdAspect")).isFalse();

        // FullContextTracingAspect assertions
        assertThat(Utils.shouldEnable("org.automon.aspects.tracing.aspectj.FullContextTracingAspect")).isFalse();
        assertThat(Utils.shouldEnableLogging("org.automon.aspects.tracing.aspectj.FullContextTracingAspect")).isFalse();

        // MyAspect assertions
        assertThat(Utils.shouldEnable("MyAspect")).isTrue();
        assertThat(Utils.shouldEnableLogging("MyAspect")).
                describedAs("This should default to true as it is not in the file").
                isTrue();

    }

}