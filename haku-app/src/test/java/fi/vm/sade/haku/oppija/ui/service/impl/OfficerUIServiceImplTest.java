package fi.vm.sade.haku.oppija.ui.service.impl;

import fi.vm.sade.haku.oppija.common.organisaatio.Organization;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.configuration.UrlConfiguration;
import fi.vm.sade.haku.oppija.hakemus.aspect.LoggerAspect;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.hakemus.service.BaseEducationService;
import fi.vm.sade.haku.oppija.hakemus.service.HakuPermissionService;
import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService;
import fi.vm.sade.haku.oppija.lomake.domain.*;
import fi.vm.sade.haku.oppija.lomake.domain.builder.PhaseBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import fi.vm.sade.haku.oppija.lomake.service.Session;
import fi.vm.sade.haku.oppija.lomake.service.impl.UserSession;
import fi.vm.sade.haku.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidatorFactory;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.FormConfigurationDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl.ThemeQuestionDAOMockImpl;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.I18nBundle;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n.I18nBundleService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.impl.KoodistoServiceMockImpl;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.ohjausparametrit.OhjausparametritService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.service.FormConfigurationService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakukohdeService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.impl.HakuServiceMockImpl;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.haku.virkailija.valinta.ValintaService;
import fi.vm.sade.haku.virkailija.valinta.impl.ValintaServiceMockImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class OfficerUIServiceImplTest {

    private static final String OID = "1.2.3.4.5";
    private static final String SENDING_SCHOOL_OID = "1.2.246.562.10.1";

    private OfficerUIServiceImpl officerUIService;
    private ApplicationService applicationService;
    private ApplicationSystemService applicationSystemService;
    private BaseEducationService baseEducationService;
    private FormService formService;
    private FormConfigurationService formConfigurationService;
    private KoodistoService koodistoService;
    private HakuPermissionService hakuPermissionService;
    private LoggerAspect loggerAspect;
    private ElementTreeValidator elementTreeValidator;
    private AuthenticationService authenticationService;
    private OrganizationService organizationService;
    private ValintaService valintaService;
    private Session userSession;

    private Application application;
    private Application applicationValinnoissa;
    private ApplicationSystem as;
    private Element phase = new PhaseBuilder(OppijaConstants.PHASE_EDUCATION).setEditAllowedByRoles("TESTING")
            .i18nText(ElementUtil.createI18NAsIs("title")).build();

    private Form form;
    private Organization sendingSchool;
    private I18nText sendingSchoolName;

    @Rule public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        application = new Application();
        applicationValinnoissa = new Application();
        application.setApplicationSystemId("asid");
        applicationValinnoissa.setApplicationSystemId("asid");
        application.setOid(OID);
        applicationValinnoissa.setOid(OID);
        application.setPhaseId(OppijaConstants.PHASE_EDUCATION);
        HashMap<String, String> valintaData = new HashMap<>();
        valintaData.put(OppijaConstants.ELEMENT_ID_BASE_EDUCATION, OppijaConstants.PERUSKOULU);
        valintaData.put(OppijaConstants.ELEMENT_ID_SENDING_SCHOOL, SENDING_SCHOOL_OID);
        applicationValinnoissa.setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_EDUCATION, valintaData);
        as = new ApplicationSystemBuilder()
                .setId("asid")
                .setName(ElementUtil.createI18NAsIs("asname"))
                .setKohdejoukkoUri(OppijaConstants.KOHDEJOUKKO_AMMATILLINEN_JA_LUKIO)
                .get();
        form = new Form("form", ElementUtil.createI18NAsIs(OppijaConstants.PHASE_EDUCATION));
        form.addChild(new TextQuestion(OppijaConstants.ELEMENT_ID_BASE_EDUCATION, ElementUtil.createI18NAsIs("pohjakoulutus")));
        applicationService = mock(ApplicationService.class);
        applicationSystemService = mock(ApplicationSystemService.class);
        formService = mock(FormService.class);
        formConfigurationService = new FormConfigurationService(
                        new KoodistoServiceMockImpl(),
                        new HakuServiceMockImpl(),
                        new ThemeQuestionDAOMockImpl(),
                        mock(HakukohdeService.class),
                        mock(OrganizationService.class),
                        mock(FormConfigurationDAO.class),
                        mock(OhjausparametritService.class),
                        mock(I18nBundleService.class));
        koodistoService = mock(KoodistoService.class);
        hakuPermissionService = mock(HakuPermissionService.class);
        loggerAspect = mock(LoggerAspect.class);
        ValidatorFactory validatorFactory = mock(ValidatorFactory.class);
        elementTreeValidator = new ElementTreeValidator(validatorFactory);
        authenticationService = mock(AuthenticationService.class);

        organizationService = mock(OrganizationService.class);
        HashMap<String ,String> lahtokoulunNimi = new HashMap<>();
        lahtokoulunNimi.put("fi", "Lähtökoulu");
        sendingSchoolName = new I18nText(lahtokoulunNimi);
        sendingSchool = new Organization(sendingSchoolName, SENDING_SCHOOL_OID, null, Collections.<String>emptyList(), null, null, null);
        when(organizationService.findByOid(SENDING_SCHOOL_OID)).thenReturn(sendingSchool);

        valintaService = new ValintaServiceMockImpl(); //mock(ValintaService.class);
        userSession = mock(UserSession.class);
        I18nBundleService i18nBundleService = mock(I18nBundleService.class);
        I18nBundle i18nBundle = mock(I18nBundle.class);
        when(i18nBundle.get(any(String.class))).thenReturn(new I18nText(new HashMap<String, String>(3) {{
            put("fi", "osoite");put("en", "osoite");put("sv", "osoite");
        }}));
        when(i18nBundleService.getBundle(any(ApplicationSystem.class))).thenReturn(i18nBundle);

        UrlConfiguration urlConfiguration = new UrlConfiguration(UrlConfiguration.SPRING_IT_PROFILE);
        urlConfiguration.addDefault("host.virkailija","localhost:9090");
        officerUIService = new OfficerUIServiceImpl(
                applicationService,
                formService,
                koodistoService,
                hakuPermissionService,
                loggerAspect,
                urlConfiguration,
                elementTreeValidator,
                applicationSystemService,
                authenticationService,
                organizationService,
                valintaService,
                userSession,
                null,
                mock(HakumaksuService.class),
                "01.02 - 01.09");
        form.addChild(phase);
        when(applicationSystemService.getApplicationSystem(any(String.class))).thenReturn(as);
        when(applicationService.getApplication(OID)).thenReturn(application);
        when(applicationService.getApplicationByOid(OID)).thenReturn(application);
        when(applicationService.getApplicationWithValintadata(application)).thenReturn(applicationValinnoissa);
        when(applicationService.removeOrphanedAnswers(any(Application.class))).then(returnsFirstArg());
        when(formService.getForm(any(String.class))).thenReturn(form);
        when(formService.getActiveForm(any(String.class))).thenReturn(form);
        Map<String, Boolean> phasesToEdit = new HashMap<String, Boolean>();
        phasesToEdit.put(OppijaConstants.PHASE_EDUCATION, true);
        when(hakuPermissionService.userHasEditRoleToPhases(any(ApplicationSystem.class), any(Application.class), any(Form.class))).thenReturn(phasesToEdit);
        User officerUser = new User("1.2.246.562.24.00000000001");
        when(userSession.getUser()).thenReturn(officerUser);
    }

    @Test
    public void testGetValidatedApplication() throws Exception {
        ModelResponse modelResponse = officerUIService.getValidatedApplication(OID, OppijaConstants.PHASE_EDUCATION, true);
        assertTrue(modelResponse.getModel().size() > 0);
        assertEquals(OppijaConstants.PERUSKOULU, ((Map<String, String>) modelResponse.getModel().get(ModelResponse.ANSWERS)).get(OppijaConstants.ELEMENT_ID_BASE_EDUCATION));
        assertEquals(sendingSchoolName, modelResponse.getModel().get("sendingSchool"));
    }

    @Test
    public void testUpdateApplication() throws Exception {
        ModelResponse modelResponse = officerUIService.updateApplication(
                OID, new ApplicationPhase(application.getApplicationSystemId(), OppijaConstants.PHASE_EDUCATION, new HashMap<String, String>()), new User(User.ANONYMOUS_USER));
        assertEquals(11, modelResponse.getModel().size());
    }

    @Test
    public void testGetApplicationWithLastPhase() throws Exception {
        Application expectedApplication = officerUIService.getApplicationWithLastPhase(OID);
        assertEquals("esikatselu", expectedApplication.getPhaseId());
    }

    @Test
    public void testGetOrganizationAndLearningInstitutions() throws Exception {
        ModelResponse modelResponse = officerUIService.getOrganizationAndLearningInstitutions(

        );
        assertEquals("Model size does not match", 10, modelResponse.getModel().size());
    }

    @Test
    public void testSaveApplicationAdditionalInfo() throws Exception {
        HashMap<String, String> additionalInfo = new HashMap<String, String>();
        officerUIService.saveApplicationAdditionalInfo(OID, additionalInfo);
        verify(applicationService, times(1)).saveApplicationAdditionalInfo(OID, additionalInfo);
    }
}
