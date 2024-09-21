package org.automon.aspects.monitoring;

import org.automon.implementations.NullImp;
import org.automon.implementations.OpenMon;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class BaseMonitoringAspectTest {

    private final OpenMon openMon = mock(OpenMon.class);
    private final BaseMonitoringAspect baseMonitoringAspect = new BaseMonitoringAspect();

    @BeforeEach
    public void setUp() throws Exception {
    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    @Test
    public void testSetOpenMon() {
        OpenMon nullMon = new NullImp();
        baseMonitoringAspect.setOpenMon(nullMon);
        assertThat(baseMonitoringAspect.getOpenMon()).isEqualTo(nullMon);

        baseMonitoringAspect.setOpenMon(openMon);
        assertThat(baseMonitoringAspect.getOpenMon()).isEqualTo(openMon);
    }


}