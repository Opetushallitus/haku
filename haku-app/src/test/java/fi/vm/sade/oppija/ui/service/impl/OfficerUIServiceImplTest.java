package fi.vm.sade.oppija.ui.service.impl;

import com.google.common.collect.ImmutableList;
import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.common.valintaperusteet.AdditionalQuestions;
import fi.vm.sade.oppija.common.valintaperusteet.ValintaperusteetService;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.util.ElementUtil;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.ui.service.UIServiceResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class OfficerUIServiceImplTest {

    private static final String OID = "1.2.3.4.5";
    private static final List<String> OIDS = ImmutableList.of("1", "2");
    public static final String ID = "id";
    private OfficerUIServiceImpl officerUIService;
    private ApplicationService applicationService;
    private FormService formService;
    private ValintaperusteetService valintaperusteetService;
    private KoodistoService koodistoService;
    private Application application;
    private AdditionalQuestions additionalQuestions = new AdditionalQuestions();
    private Phase phase = new Phase(ID, ElementUtil.createI18NForm("title"), false);
    private Form form;

    @Before
    public void setUp() throws Exception {
        application = new Application();
        application.setFormId(new FormId(ID, ID));
        application.setOid(OID);
        application.setPhaseId(ID);
        form = new Form("form", ElementUtil.createI18NForm(ID));
        applicationService = mock(ApplicationService.class);
        formService = mock(FormService.class);
        valintaperusteetService = mock(ValintaperusteetService.class);
        koodistoService = mock(KoodistoService.class);
        officerUIService = new OfficerUIServiceImpl(
                applicationService, formService, valintaperusteetService, koodistoService);
        form.addChild(phase);
        when(applicationService.getApplicationPreferenceOids(application)).thenReturn(OIDS);
        when(applicationService.getApplication(OID)).thenReturn(application);
        when(valintaperusteetService.retrieveAdditionalQuestions(OIDS)).thenReturn(additionalQuestions);
        when(formService.getForm(any(FormId.class))).thenReturn(form);
        when(formService.getActiveForm(any(FormId.class))).thenReturn(form);
        when(formService.getLastPhase(any(String.class), any(String.class))).thenReturn(phase);
    }

    @Test
    public void testGetValidatedApplication() throws Exception {
        UIServiceResponse uiServiceResponse = officerUIService.getValidatedApplication(OID, ID);
        assertTrue(uiServiceResponse.getModel().size() > 0);
    }

    @Test
    public void testGetAdditionalInfo() throws Exception {
        UIServiceResponse uiServiceResponse = officerUIService.getAdditionalInfo(OID);
        assertEquals(application, uiServiceResponse.getModel().get(UIServiceResponse.APPLICATION));
        assertEquals(additionalQuestions, uiServiceResponse.getModel().get(OfficerAdditionalInfoResponse.ADDITIONAL_QUESTIONS));
    }

    @Test
    public void testUpdateApplication() throws Exception {
        UIServiceResponse uiServiceResponse = officerUIService.updateApplication(
                OID, new ApplicationPhase(application.getFormId(), ID, new HashMap<String, String>()));
        assertTrue(10 == uiServiceResponse.getModel().size());
    }

    @Test
    public void testGetApplicationWithLastPhase() throws Exception {
        Application expectedApplication = officerUIService.getApplicationWithLastPhase(OID);
        assertEquals(ID, expectedApplication.getPhaseId());

    }

    @Test
    public void testGetOrganizationAndLearningInstitutions() throws Exception {
        UIServiceResponse uiServiceResponse = officerUIService.getOrganizationAndLearningInstitutions();
        assertTrue(3 == uiServiceResponse.getModel().size());
    }

    @Test
    public void testSaveApplicationAdditionalInfo() throws Exception {
        HashMap<String, String> additionalInfo = new HashMap<String, String>();
        officerUIService.saveApplicationAdditionalInfo(OID, additionalInfo);
        verify(applicationService, times(1)).saveApplicationAdditionalInfo(OID, additionalInfo);
    }
}
