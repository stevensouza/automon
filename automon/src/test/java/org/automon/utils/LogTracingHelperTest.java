package org.automon.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.slf4j.MDC;
import org.slf4j.NDC;

import static org.assertj.core.api.Assertions.assertThat;
import static org.automon.utils.LogTracingHelper.*;
import static org.mockito.Mockito.*;

class LogTracingHelperTest {
    private final LogTracingHelper logTracingHelper = LogTracingHelper.getInstance();;
    private JoinPoint joinPoint;
    private JoinPoint.StaticPart staticPart;
    private JoinPoint.StaticPart enclosingStaticPart;

    @BeforeEach
    void setUp() {
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
        Object[] args = {"value1", 42}; // Sample arguments
        when(joinPoint.getArgs()).thenReturn(args);

        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class)) {
            logTracingHelper.withParameters(joinPoint);
            mdcMock.verify(() -> MDC.put(eq(PARAMETERS), eq("{param0=value1, param1=42}")));
        }
    }

    @Test
    void testWithSignature() {
        MethodSignature signature = mock(MethodSignature.class);
        when(signature.toShortString()).thenReturn("myMethod(int, String)");
        when(staticPart.getSignature()).thenReturn(signature);

        try (MockedStatic<NDC> ndcMock = mockStatic(NDC.class)) {
            logTracingHelper.withSignature(staticPart);
            ndcMock.verify(() -> NDC.push(eq("myMethod(int, String)")));
        }
    }

    @Test
    void testWithEnclosingSignature() {
        MethodSignature signature = mock(MethodSignature.class);
        when(signature.toShortString()).thenReturn("enclosingMethod()");
        when(enclosingStaticPart.getSignature()).thenReturn(signature);

        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class)) {
            logTracingHelper.withEnclosingSignature(enclosingStaticPart);
            mdcMock.verify(() -> MDC.put(eq(ENCLOSING_SIGNATURE), eq("enclosingMethod()")));
        }
    }

    @Test
    void testWithExecutionTime() {
        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class)) {
            logTracingHelper.withExecutionTime(123L);
            mdcMock.verify(() -> MDC.put(eq(EXECUTION_TIME_MS), eq("123")));
        }
    }

    @Test
    void testWithRequestId() {
        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class)) {
            logTracingHelper.withRequestId();
            mdcMock.verify(() -> MDC.put(eq(REQUEST_ID), any(String.class))); // Verify a UUID is generated
        }
    }

    @Test
    void testWithReturnValue() {
        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class)) {
            logTracingHelper.withReturnValue("result");
            mdcMock.verify(() -> MDC.put(eq(RETURN_VALUE), eq("result")));
        }
    }

    @Test
    void testWithException() {
        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class)) {
            logTracingHelper.withException("java.lang.Exception");
            mdcMock.verify(() -> MDC.put(eq(EXCEPTION), eq("java.lang.Exception")));
        }
    }

    @Test
    void testWithKind() {
        when(staticPart.getKind()).thenReturn("method-execution");

        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class)) {
            logTracingHelper.withKind(staticPart);
            mdcMock.verify(() -> MDC.put(eq(KIND), eq("method-execution")));
        }
    }

    @Test
    void testWithThis() {
        Object thisObject = new Object();
        when(joinPoint.getThis()).thenReturn(thisObject);

        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class)) {
            logTracingHelper.withThis(joinPoint);
            mdcMock.verify(() -> MDC.put(eq(THIS), eq(thisObject.toString())));
        }
    }

    @Test
    void testWithTarget() {
        Object targetObject = new Object();
        when(joinPoint.getTarget()).thenReturn(targetObject);

        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class)) {
            logTracingHelper.withTarget(joinPoint);
            mdcMock.verify(() -> MDC.put(eq(TARGET), eq(targetObject.toString())));
        }
    }


    @Test
    void testWithBasicContext() {
        when(staticPart.getKind()).thenReturn("method-execution");
        MethodSignature signature = mock(MethodSignature.class);
        when(signature.toShortString()).thenReturn("myMethod()");
        when(staticPart.getSignature()).thenReturn(signature);

        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class);
             MockedStatic<NDC> ndcMock = mockStatic(NDC.class)) {

            logTracingHelper.withBasicContext(staticPart, enclosingStaticPart);

            mdcMock.verify(() -> MDC.put(eq(KIND), eq("method-execution")));
            mdcMock.verify(() -> MDC.remove(eq("enclosingSignature")));
            ndcMock.verify(() -> NDC.push(eq("myMethod()")));
        }
    }

    @Test
    void testWithFullContext() {
        Object[] args = {"value1"}; // Sample arguments
        when(joinPoint.getArgs()).thenReturn(args);
        when(staticPart.getKind()).thenReturn("method-execution");
        when(joinPoint.getThis()).thenReturn("myThisString");
        when(joinPoint.getTarget()).thenReturn("myTargetString");

        MethodSignature signature = mock(MethodSignature.class);
        when(signature.toShortString()).thenReturn("myMethod()");
        when(staticPart.getSignature()).thenReturn(signature);

        MethodSignature enclosingSignature = mock(MethodSignature.class);
        when(enclosingSignature.toShortString()).thenReturn("enclosingMethod()");
        when(enclosingStaticPart.getSignature()).thenReturn(enclosingSignature);

        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class);
             MockedStatic<NDC> ndcMock = mockStatic(NDC.class)) {

            logTracingHelper.withFullContext(joinPoint, staticPart, enclosingStaticPart);

            mdcMock.verify(() -> MDC.put(eq(ENCLOSING_SIGNATURE), eq("enclosingMethod()")));
            mdcMock.verify(() -> MDC.put(eq(KIND), eq("method-execution")));
            mdcMock.verify(() -> MDC.put(eq(PARAMETERS), eq("{param0=value1}")));
            mdcMock.verify(() -> MDC.put(eq(TARGET), eq("myTargetString")));
            mdcMock.verify(() -> MDC.put(eq(THIS),  eq("myThisString")));
            ndcMock.verify(() -> NDC.push(eq("myMethod()")));
        }
    }


    @Test
    void testRemoveTarget() {
        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class)) {
            logTracingHelper.removeTarget();
            mdcMock.verify(() -> MDC.remove(eq(TARGET)));
        }
    }

    @Test
    void testRemoveParameters() {
        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class)) {
            logTracingHelper.removeParameters();
            mdcMock.verify(() -> MDC.remove(eq(PARAMETERS)));
        }
    }

    @Test
    void testRemoveSignature() {
        try (MockedStatic<NDC> ndcMock = mockStatic(NDC.class)) {
            logTracingHelper.removeSignature();
            ndcMock.verify(NDC::pop);
        }
    }

    @Test
    void testRemoveEnclosingSignature() {
        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class)) {
            logTracingHelper.removeEnclosingSignature();
            mdcMock.verify(() -> MDC.remove(eq(ENCLOSING_SIGNATURE)));
        }
    }

    @Test
    void testRemoveExecutionTime() {
        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class)) {
            logTracingHelper.removeExecutionTime();
            mdcMock.verify(() -> MDC.remove(eq(EXECUTION_TIME_MS)));
        }
    }

    @Test
    void testRemoveRequestId() {
        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class)) {
            logTracingHelper.removeRequestId();
            mdcMock.verify(() -> MDC.remove(eq(REQUEST_ID)));
        }
    }

    @Test
    void testRemoveReturnValue() {
        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class)) {
            logTracingHelper.removeReturnValue();
            mdcMock.verify(() -> MDC.remove(eq(RETURN_VALUE)));
        }
    }

    @Test
    void testRemoveKind() {
        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class)) {
            logTracingHelper.removeKind();
            mdcMock.verify(() -> MDC.remove(eq(KIND)));
        }
    }

    @Test
    void testRemoveException() {
        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class)) {
            logTracingHelper.removeException();
            mdcMock.verify(() -> MDC.remove(eq(EXCEPTION)));
        }
    }

    @Test
    void testRemoveThis() {
        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class)) {
            logTracingHelper.removeThis();
            mdcMock.verify(() -> MDC.remove(eq(THIS)));
        }
    }

    @Test
    void testRemoveWithBasicContext() {
        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class);
             MockedStatic<NDC> ndcMock = mockStatic(NDC.class)) {

            logTracingHelper.removeBasicContext();

            mdcMock.verify(() -> MDC.remove(eq(ENCLOSING_SIGNATURE)));
            mdcMock.verify(() -> MDC.remove(eq(EXECUTION_TIME_MS)));
            mdcMock.verify(() -> MDC.remove(eq(KIND)));
            ndcMock.verify(NDC::pop);
        }
    }

    @Test
    void testRemoveFullContext() {
        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class);
             MockedStatic<NDC> ndcMock = mockStatic(NDC.class)) {

            logTracingHelper.removeFullContext();

            mdcMock.verify(() -> MDC.remove(eq(ENCLOSING_SIGNATURE)));
            mdcMock.verify(() -> MDC.remove(eq(EXECUTION_TIME_MS)));
            mdcMock.verify(() -> MDC.remove(eq(EXCEPTION)));
            mdcMock.verify(() -> MDC.remove(eq(KIND)));
            mdcMock.verify(() -> MDC.remove(eq(PARAMETERS)));
            mdcMock.verify(() -> MDC.remove(eq(RETURN_VALUE)));
            mdcMock.verify(() -> MDC.remove(eq(TARGET)));
            mdcMock.verify(() -> MDC.remove(eq(THIS)));
            ndcMock.verify(NDC::pop);
        }
    }

    @Test
    void testAutoCloseable() {
        try (MockedStatic<MDC> mdcMock = mockStatic(MDC.class);
             MockedStatic<NDC> ndcMock = mockStatic(NDC.class)) {
            try (logTracingHelper) {
                System.out.println("LogTracingHelper.close() is automatically called at end of block (AutoCloseable)");
            }

            mdcMock.verify(() -> MDC.remove(eq(ENCLOSING_SIGNATURE)));
            mdcMock.verify(() -> MDC.remove(eq(EXECUTION_TIME_MS)));
            mdcMock.verify(() -> MDC.remove(eq(EXCEPTION)));
            mdcMock.verify(() -> MDC.remove(eq(KIND)));
            mdcMock.verify(() -> MDC.remove(eq(PARAMETERS)));
            mdcMock.verify(() -> MDC.remove(eq(RETURN_VALUE)));
            mdcMock.verify(() -> MDC.remove(eq(TARGET)));
            mdcMock.verify(() -> MDC.remove(eq(THIS)));
            ndcMock.verify(NDC::pop);
        }
    }

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