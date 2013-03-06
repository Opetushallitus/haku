package fi.vm.sade.oppija.ui.service;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.lomake.domain.FormId;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UIServiceResponseTest {

    public static final String EXPECTED_VALUE = "value";
    public static final String KEY = "key";
    private UIServiceResponse uiServiceResponse;
    private Application application;

    @Before
    public void setUp() throws Exception {
        this.uiServiceResponse = new UIServiceResponse();
        application = new Application();
        application.setPhaseId("Phase");
        application.setOid("oid");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetModel() throws Exception {
        uiServiceResponse.getModel().put("", "");
    }

    @Test
    public void testAddObjectToModel() throws Exception {
        uiServiceResponse.addObjectToModel(KEY, EXPECTED_VALUE);
        assertEquals(EXPECTED_VALUE, uiServiceResponse.getModel().get(KEY));
    }

    @Test
    public void testSetApplication() throws Exception {
        application.setFormId(new FormId("", ""));
        uiServiceResponse.setApplication(application);
        assertEquals(7, uiServiceResponse.getModel().size());
    }

    @Test
    public void testHasErrorsTrue() throws Exception {
        uiServiceResponse.setErrorMessages(ImmutableMap.of("error", "message"));
        assertTrue(uiServiceResponse.hasErrors());
    }

    @Test
    public void testHasErrorsFalse() throws Exception {
        assertFalse(uiServiceResponse.hasErrors());
    }
}
