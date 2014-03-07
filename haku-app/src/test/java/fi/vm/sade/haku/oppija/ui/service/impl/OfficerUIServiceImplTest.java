package fi.vm.sade.haku.oppija.ui.service.impl;

import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.hakemus.aspect.LoggerAspect;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.hakemus.service.HakuPermissionService;
import fi.vm.sade.haku.oppija.lomake.domain.User;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import fi.vm.sade.haku.oppija.lomake.service.UserSession;
import fi.vm.sade.haku.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidatorFactory;
import fi.vm.sade.haku.oppija.ui.service.ModelResponse;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.valinta.ValintaService;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class OfficerUIServiceImplTest {

    private static final String OID = "1.2.3.4.5";
    public static final String ID = "id";

    private OfficerUIServiceImpl officerUIService;
    private ApplicationService applicationService;
    private FormService formService;
    private KoodistoService koodistoService;
    private HakuPermissionService hakuPermissionService;
    private LoggerAspect loggerAspect;
    private ElementTreeValidator elementTreeValidator;
    private AuthenticationService authenticationService;
    private OrganizationService organizationService;
    private ValintaService valintaService;
    private UserSession userSession;

    private Application application;
    private Phase phase = new Phase(ID, ElementUtil.createI18NAsIs("title"), false,
            Lists.newArrayList("APP_HAKEMUS_READ_UPDATE", "APP_HAKEMUS_CRUD", "APP_HAKEMUS_OPO"));

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
        authenticationService = mock(AuthenticationService.class);
        organizationService = mock(OrganizationService.class);
        valintaService = mock(ValintaService.class);
        userSession = mock(UserSession.class);

        officerUIService = new OfficerUIServiceImpl(
                applicationService,
                formService,
                koodistoService,
                hakuPermissionService,
                loggerAspect, "",
                elementTreeValidator,
                mock(ApplicationSystemService.class),
                authenticationService,
                organizationService,
                valintaService,
                userSession);
        form.addChild(phase);
        when(applicationService.getApplication(OID)).thenReturn(application);
        when(applicationService.getApplicationByOid(OID)).thenReturn(application);
        when(formService.getForm(any(String.class))).thenReturn(form);
        when(formService.getActiveForm(any(String.class))).thenReturn(form);
        Map<String, Boolean> phasesToEdit = new HashMap<String, Boolean>();
        phasesToEdit.put(ID, true);
        when(hakuPermissionService.userHasEditRoleToPhases(any(Application.class), any(Form.class))).thenReturn(phasesToEdit);
        User officerUser = new User("1.2.246.562.24.00000000001");
        when(userSession.getUser()).thenReturn(officerUser);
    }

    @Test
    public void testGetValidatedApplication() throws Exception {
        ModelResponse modelResponse = officerUIService.getValidatedApplication(OID, ID);
        assertTrue(modelResponse.getModel().size() > 0);
    }

    @Test
    public void testUpdateApplication() throws Exception {
        ModelResponse modelResponse = officerUIService.updateApplication(
                OID, new ApplicationPhase(application.getApplicationSystemId(), ID, new HashMap<String, String>()), new User(User.ANONYMOUS_USER));
        assertTrue(10 == modelResponse.getModel().size());
    }

    @Test
    public void testGetApplicationWithLastPhase() throws Exception {
        Application expectedApplication = officerUIService.getApplicationWithLastPhase(OID);
        assertEquals("esikatselu", expectedApplication.getPhaseId());
    }

    @Test
    public void testGetOrganizationAndLearningInstitutions() throws Exception {
        ModelResponse modelResponse = officerUIService.getOrganizationAndLearningInstitutions();
        assertEquals("Model size does not match", 9, modelResponse.getModel().size());
    }

    @Test
    public void testSaveApplicationAdditionalInfo() throws Exception {
        HashMap<String, String> additionalInfo = new HashMap<String, String>();
        officerUIService.saveApplicationAdditionalInfo(OID, additionalInfo);
        verify(applicationService, times(1)).saveApplicationAdditionalInfo(OID, additionalInfo);
    }
}
