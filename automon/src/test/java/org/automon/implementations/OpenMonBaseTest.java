package org.automon.implementations;

import org.aspectj.lang.JoinPoint;
import org.automon.utils.AutomonExpirable;
import org.automon.utils.Expirable;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class OpenMonBaseTest {
    OpenMonBase openMon = new OpenMonBase() {
        @Override
        public Object start(JoinPoint.StaticPart jp) {
            return null;
        }

        @Override
        public void stop(Object context) {

        }
    };

    @Test
    public void testException() throws Exception {
        JoinPoint jp = mock(JoinPoint.class);
        RuntimeException runtimeException = new RuntimeException("my exception");
        openMon.exception(jp, runtimeException);
        assertThat(openMon.getExceptionsMap()).containsOnlyKeys(runtimeException);
        Map<Throwable, Expirable> exceptionsMap = openMon.getExceptionsMap();
        Expirable expirable = exceptionsMap.get(runtimeException);

        openMon.exception(jp, runtimeException);
        openMon.exception(jp, runtimeException);

        assertThat(openMon.getExceptionsMap()).containsOnlyKeys(runtimeException);
        assertThat(openMon.getExceptionsMap().get(runtimeException)).
                describedAs("The value object should only have been created once").
                isEqualTo(expirable);

        Map<Throwable, AutomonExpirable> map = openMon.getExceptionsMap();
        assertThat(map.get(runtimeException).getThrowable()).describedAs("Throwable should have been set").isEqualTo(runtimeException);
    }

    @Test
    public void testGetLabels() throws Exception {
        RuntimeException runtimeException = new RuntimeException("my exception");
        List<String> labels = openMon.getLabels(runtimeException);
        assertThat(labels).containsOnly("java.lang.RuntimeException", OpenMon.EXCEPTION_LABEL);
    }
}