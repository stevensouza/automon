package org.automon.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.slf4j.MDC;
import org.slf4j.NDC;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LogTracingHelperTest {
// look for tests here https://g.co/gemini/share/a9f7b681061f
    private LogTracingHelper logTracingHelper;
    private JoinPoint joinPoint;
    private JoinPoint.StaticPart staticPart;
    private JoinPoint.StaticPart enclosingStaticPart;

    @BeforeEach
    void setUp() {
        logTracingHelper = LogTracingHelper.getInstance();
        joinPoint = mock(JoinPoint.class);
        staticPart = mock(JoinPoint.StaticPart.class);
        enclosingStaticPart = mock(JoinPoint.StaticPart.class);
    }

    @Test
    void testGetInstance() {
        assertThat(LogTracingHelper.getInstance()).isSameAs(logTracingHelper);
    }

    @Test
    void testWithParameters() {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("arg0", "value1");
        paramsMap.put("arg1", 42);

        when(Utils.paramsToMap(joinPoint)).thenReturn(paramsMap);

        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class)) {
            logTracingHelper.withParameters(joinPoint);
            mdcMock.verify(() -> MDC.put("parameters", "{arg0=value1, arg1=42}"));
        }
    }

    @Test
    void testWithSignature() {
        MethodSignature signature = mock(MethodSignature.class);
        when(signature.toShortString()).thenReturn("myMethod(int, String)");
        when(staticPart.getSignature()).thenReturn(signature);

        try (MockedStatic<NDC> ndcMock = mockStatic(NDC.class)) {
            logTracingHelper.withSignature(staticPart);
            ndcMock.verify(() -> NDC.push("myMethod(int, String)"));
        }
    }

    @Test
    void testWithEnclosingSignature() {
        MethodSignature signature = mock(MethodSignature.class);
        when(signature.toShortString()).thenReturn("enclosingMethod()");
        when(enclosingStaticPart.getSignature()).thenReturn(signature);

        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class)) {
            logTracingHelper.withEnclosingSignature(enclosingStaticPart);
            mdcMock.verify(() -> MDC.put("enclosingSignature", "enclosingMethod()"));
        }
    }

    @Test
    void testWithExecutionTime() {
        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class)) {
            logTracingHelper.withExecutionTime(123L);
            mdcMock.verify(() -> MDC.put("executionTimeMs", "123"));
        }
    }

    @Test
    void testWithRequestId() {
        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class)) {
            logTracingHelper.withRequestId();
            mdcMock.verify(() -> MDC.put("requestId", any(String.class))); // Verify a UUID is generated
        }
    }

    @Test
    void testWithReturnValue() {
        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class)) {
            logTracingHelper.withReturnValue("result");
            mdcMock.verify(() -> MDC.put("returnValue", "result"));
        }
    }

    @Test
    void testWithKind() {
        when(staticPart.getKind()).thenReturn("method-execution");

        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class)) {
            logTracingHelper.withKind(staticPart);
            mdcMock.verify(() -> MDC.put("kind", "method-execution"));
        }
    }

    @Test
    void testWithThis() {
        Object thisObject = new Object();
        when(joinPoint.getThis()).thenReturn(thisObject);

        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class)) {
            logTracingHelper.withThis(joinPoint);
            mdcMock.verify(() -> MDC.put("this", thisObject.toString()));
        }
    }

    @Test
    void testWithTarget() {
        Object targetObject = new Object();
        when(joinPoint.getTarget()).thenReturn(targetObject);

        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class)) {
            logTracingHelper.withTarget(joinPoint);
            mdcMock.verify(() -> MDC.put("target", targetObject.toString()));
        }
    }

    // ... Add tests for other 'with' and 'remove' methods ...

    @Test
    void testBasicContext() {
        when(staticPart.getKind()).thenReturn("method-execution");
        MethodSignature signature = mock(MethodSignature.class);
        when(signature.toShortString()).thenReturn("myMethod()");
        when(staticPart.getSignature()).thenReturn(signature);

        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class);
             MockedStatic<NDC> ndcMock = mockStatic(NDC.class)) {

            logTracingHelper.basicContext(staticPart, enclosingStaticPart);

            mdcMock.verify(() -> MDC.put("kind", "method-execution"));
            mdcMock.verify(() -> MDC.remove("enclosingSignature"));
            ndcMock.verify(() -> NDC.push("myMethod()"));
        }
    }

    @Test
    void testFullContext() {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("arg0", "value1");

        when(Utils.paramsToMap(joinPoint)).thenReturn(paramsMap);
        when(staticPart.getKind()).thenReturn("method-execution");
        when(joinPoint.getThis()).thenReturn(new Object());
        when(joinPoint.getTarget()).thenReturn(new Object());

        MethodSignature signature = mock(MethodSignature.class);
        when(signature.toShortString()).thenReturn("myMethod()");
        when(staticPart.getSignature()).thenReturn(signature);

        MethodSignature enclosingSignature = mock(MethodSignature.class);
        when(enclosingSignature.toShortString()).thenReturn("enclosingMethod()");
        when(enclosingStaticPart.getSignature()).thenReturn(enclosingSignature);

        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class);
             MockedStatic<NDC> ndcMock = mockStatic(NDC.class)) {

            logTracingHelper.fullContext(joinPoint, staticPart, enclosingStaticPart);

            mdcMock.verify(() -> MDC.put("enclosingSignature", "enclosingMethod()"));
            mdcMock.verify(() -> MDC.put("kind", "method-execution"));
            mdcMock.verify(() -> MDC.put("parameters", "{arg0=value1}"));
            mdcMock.verify(() -> MDC.put("target", any(String.class)));
            mdcMock.verify(() -> MDC.put("this", any(String.class)));
            ndcMock.verify(() -> NDC.push("myMethod()"));
        }
    }

    // ... Add tests for other methods ...

    @Test
    void testGetStringAsNumberOrDefault_validInput() {
        int result = LogTracingHelper.getStringAsNumberOrDefault("123");
        assertThat(result).isEqualTo(123);
    }

    @Test
    void testGetStringAsNumberOrDefault_invalidInput() {
        int result = LogTracingHelper.getStringAsNumberOrDefault("abc");
        assertThat(result).isEqualTo(0);
    }

    @Test
    void testGetStringAsNumberOrDefault_nullInput() {
        int result = LogTracingHelper.getStringAsNumberOrDefault(null);
        assertThat(result).isEqualTo(0);
    }
}