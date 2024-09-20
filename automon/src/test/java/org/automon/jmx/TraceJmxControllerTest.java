package org.automon.jmx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TraceJmxControllerTest {

        private TraceJmxController traceJmxController;

        @BeforeEach
        public void setUp() {
            traceJmxController = new TraceJmxController();
        }

        // Tests for TraceJmxController methods
        @Test
        public void testDefaultLoggingEnabled() {
            assertThat(traceJmxController.isLoggingEnabled()).isTrue();
        }

        @Test
        public void testEnableLogging() {
            traceJmxController.enableLogging(false);
            assertThat(traceJmxController.isLoggingEnabled()).isFalse();

            traceJmxController.enableLogging(true);
            assertThat(traceJmxController.isLoggingEnabled()).isTrue();
        }

        // Tests for AspectJmxController methods (inherited)
        @Test
        public void testDefaultEnabled() {
            assertThat(traceJmxController.isEnabled()).isTrue(); // Inherited from AspectJmxController
        }

        @Test
        public void testEnable() {
            traceJmxController.enable(false);
            assertThat(traceJmxController.isEnabled()).isFalse();

            traceJmxController.enable(true);
            assertThat(traceJmxController.isEnabled()).isTrue();
        }
}