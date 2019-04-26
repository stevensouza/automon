package org.automon.aspects;

import org.automon.implementations.NullImp;
import org.automon.implementations.OpenMon;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class AutomonAspectBaseTest {

    private OpenMon openMon = mock(OpenMon.class);
    private AutomonAspectBase automonAspectBase = new AutomonAspectBase();

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSetOpenMon() throws Exception {
        OpenMon nullMon = new NullImp();
        automonAspectBase.setOpenMon(nullMon);
        assertThat(automonAspectBase.isEnabled()).isFalse();
        assertThat(automonAspectBase.getOpenMon()).isEqualTo(nullMon);

        automonAspectBase.setOpenMon(openMon);
        assertThat(automonAspectBase.isEnabled()).isTrue();
        assertThat(automonAspectBase.getOpenMon()).isEqualTo(openMon);
    }


}