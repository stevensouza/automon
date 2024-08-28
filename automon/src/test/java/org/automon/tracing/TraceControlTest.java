package org.automon.tracing;

import org.automon.tracing.jmx.TraceControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TraceControlTest {

        private TraceControl traceControl;

        @BeforeEach
        public void setUp() {
            traceControl = new TraceControl();
        }

        // Tests for TraceControl methods
        @Test
        public void testDefaultLoggingEnabled() {
            assertThat(traceControl.isLoggingEnabled()).isTrue();
        }

        @Test
        public void testEnableLogging() {
            traceControl.enableLogging(false);
            assertThat(traceControl.isLoggingEnabled()).isFalse();

            traceControl.enableLogging(true);
            assertThat(traceControl.isLoggingEnabled()).isTrue();
        }

        // Tests for AspectControl methods (inherited)
        @Test
        public void testDefaultEnabled() {
            assertThat(traceControl.isEnabled()).isTrue(); // Inherited from AspectControl
        }

        @Test
        public void testEnable() {
            traceControl.enable(false);
            assertThat(traceControl.isEnabled()).isFalse();

            traceControl.enable(true);
            assertThat(traceControl.isEnabled()).isTrue();
        }
}