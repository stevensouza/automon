package org.automon.tracing;

import org.automon.tracing.jmx.AspectControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AspectControlTest {

    private AspectControl aspectControl;

    @BeforeEach
    void setUp() {
        aspectControl = new AspectControl();
    }

    @Test
    void testDefaultEnabled() {
        assertThat(aspectControl.isEnabled()).isTrue();
    }

    @Test
    void testEnable() {
        aspectControl.enable(false);
        assertThat(aspectControl.isEnabled()).isFalse();

        aspectControl.enable(true);
        assertThat(aspectControl.isEnabled()).isTrue();
    }
}