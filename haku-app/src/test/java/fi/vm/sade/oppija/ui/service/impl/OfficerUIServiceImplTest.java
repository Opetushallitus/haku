package fi.vm.sade.oppija.ui.service.impl;

import com.google.common.collect.ImmutableList;
import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.hakemus.aspect.LoggerAspect;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.AnonymousUser;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.oppija.lomake.validation.ValidatorFactory;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.oppija.ui.HakuPermissionService;
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
    private KoodistoService koodistoService;
    private HakuPermissionService hakuPermissionService;
    private LoggerAspect loggerAspect;
    private ElementTreeValidator elementTreeValidator;

    private Application application;
    private Phase phase = new Phase(ID, ElementUtil.createI18NAsIs("title"), false);

    private Form form;

    @Before
    public void setUp() throws Exception {
        application = new Application();
        application.setApplicationSystemId("asid");
        application.setOid(OID);
        application.setPhaseId(ID);
        form = new Form("form", ElementUtil.createI18NAsIs(ID));
        applicationService = mock(ApplicationService.class);
        formService = mock(FormService.class);
        koodistoService = mock(KoodistoService.class);
        hakuPermissionService = mock(HakuPermissionService.class);
        loggerAspect = mock(LoggerAspect.class);
        ValidatorFactory validatorFactory = mock(ValidatorFactory.class);
        elementTreeValidator = new ElementTreeValidator(validatorFactory);

        officerUIService = new OfficerUIServiceImpl(
                applicationService,
                formService,
                koodistoService,
                hakuPermissionService,
                loggerAspect, "",
                elementTreeValidator,
                mock(ApplicationSystemService.class));
        form.addChild(phase);
        when(applicationService.getApplicationPreferenceOids(application)).thenReturn(OIDS);
        when(applicationService.getApplication(OID)).thenReturn(application);
        when(applicationService.getApplicationByOid(OID)).thenReturn(application);
        when(formService.getForm(any(String.class))).thenReturn(form);
        when(formService.getActiveForm(any(String.class))).thenReturn(form);
        when(formService.getLastPhase(any(String.class))).thenReturn(phase);
        when(hakuPermissionService.userCanUpdateApplication(any(Application.class))).thenReturn(true);
    }

    @Test
    public void testGetValidatedApplication() throws Exception {
        UIServiceResponse uiServiceResponse = officerUIService.getValidatedApplication(OID, ID);
        assertTrue(uiServiceResponse.getModel().size() > 0);
    }

    @Test
    public void testUpdateApplication() throws Exception {
        UIServiceResponse uiServiceResponse = officerUIService.updateApplication(
                OID, new ApplicationPhase(application.getApplicationSystemId(), ID, new HashMap<String, String>()), new AnonymousUser());
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
        assertTrue(4 == uiServiceResponse.getModel().size());
    }

    @Test
    public void testSaveApplicationAdditionalInfo() throws Exception {
        HashMap<String, String> additionalInfo = new HashMap<String, String>();
        officerUIService.saveApplicationAdditionalInfo(OID, additionalInfo);
        verify(applicationService, times(1)).saveApplicationAdditionalInfo(OID, additionalInfo);
    }
}
