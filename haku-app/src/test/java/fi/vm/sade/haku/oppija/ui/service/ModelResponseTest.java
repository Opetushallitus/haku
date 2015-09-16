package fi.vm.sade.haku.oppija.ui.service;

import com.google.common.collect.ImmutableMap;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.lomake.domain.ModelResponse;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ModelResponseTest {

    public static final String EXPECTED_VALUE = "value";
    public static final String KEY = "key";
    private ModelResponse modelResponse;
    private Application application;

    @Before
    public void setUp() throws Exception {
        this.modelResponse = new ModelResponse();
        application = new Application();
        application.setPhaseId("Phase");
        application.setOid("oid");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetModel() throws Exception {
        modelResponse.getModel().put("", "");
    }

    @Test
    public void testAddObjectToModel() throws Exception {
        modelResponse.addObjectToModel(KEY, EXPECTED_VALUE);
        assertEquals(EXPECTED_VALUE, modelResponse.getModel().get(KEY));
    }

    @Test
    public void testSetApplication() throws Exception {
        application.setApplicationSystemId("");
        modelResponse.setApplication(application);
        assertEquals(7, modelResponse.getModel().size());
    }

    @Test
    public void testHasErrorsTrue() throws Exception {
        modelResponse.setErrorMessages(ImmutableMap.of("error", ElementUtil.createI18NAsIs("")));
        assertTrue(modelResponse.hasErrors());
    }

    @Test
    public void testHasErrorsFalse() throws Exception {
        assertFalse(modelResponse.hasErrors());
    }
}
