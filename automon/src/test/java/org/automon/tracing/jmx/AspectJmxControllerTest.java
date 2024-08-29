package org.automon.tracing.jmx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AspectJmxControllerTest {

    private AspectJmxController aspectJmxController;

    @BeforeEach
    void setUp() {
        aspectJmxController = new AspectJmxController();
    }

    @Test
    void testDefaultEnabled() {
        assertThat(aspectJmxController.isEnabled()).isTrue();
    }

    @Test
    void testEnable() {
        aspectJmxController.enable(false);
        assertThat(aspectJmxController.isEnabled()).isFalse();

        aspectJmxController.enable(true);
        assertThat(aspectJmxController.isEnabled()).isTrue();
    }
}