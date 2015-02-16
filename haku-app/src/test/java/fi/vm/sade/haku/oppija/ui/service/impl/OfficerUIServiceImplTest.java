package fi.vm.sade.haku.oppija.ui.service.impl;

import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.hakemus.aspect.LoggerAspect;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.hakemus.service.BaseEducationService;
import fi.vm.sade.haku.oppija.hakemus.service.HakuPermissionService;
import fi.vm.sade.haku.oppija.lomake.domain.*;
import fi.vm.sade.haku.oppija.lomake.domain.builder.PhaseBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import fi.vm.sade.haku.oppija.lomake.service.UserSession;
import fi.vm.sade.haku.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidatorFactory;
import fi.vm.sade.haku.oppija.ui.service.OfficerUIService;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.I18nBundle;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n.I18nBundleService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.haku.virkailija.valinta.ValintaService;
import fi.vm.sade.haku.virkailija.valinta.impl.ValintaServiceMockImpl;
import org.apache.commons.collections.map.SingletonMap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class OfficerUIServiceImplTest {

    private static final String OID = "1.2.3.4.5";
    public static final String ID = "id";

    private OfficerUIServiceImpl officerUIService;
    private ApplicationService applicationService;
    private ApplicationSystemService applicationSystemService;
    private BaseEducationService baseEducationService;
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
    private ApplicationSystem as;
    private Element phase = new PhaseBuilder(ID).setEditAllowedByRoles("TESTING")
            .i18nText(ElementUtil.createI18NAsIs("title")).build();

    private Form form;

    @Rule public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        application = new Application();
        application.setApplicationSystemId("asid");
        application.setOid(OID);
        application.setPhaseId(ID);
        as = new ApplicationSystemBuilder()
                .setId("asid")
                .setName(ElementUtil.createI18NAsIs("asname"))
                .setKohdejoukkoUri(OppijaConstants.KOHDEJOUKKO_AMMATILLINEN_JA_LUKIO)
                .get();
        form = new Form("form", ElementUtil.createI18NAsIs(ID));
        applicationService = mock(ApplicationService.class);
        applicationSystemService = mock(ApplicationSystemService.class);
        formService = mock(FormService.class);
        koodistoService = mock(KoodistoService.class);
        hakuPermissionService = mock(HakuPermissionService.class);
        loggerAspect = mock(LoggerAspect.class);
        ValidatorFactory validatorFactory = mock(ValidatorFactory.class);
        elementTreeValidator = new ElementTreeValidator(validatorFactory);
        authenticationService = mock(AuthenticationService.class);
        organizationService = mock(OrganizationService.class);
        valintaService = new ValintaServiceMockImpl(); //mock(ValintaService.class);
        userSession = mock(UserSession.class);
        baseEducationService = mock(BaseEducationService.class);        I18nBundleService i18nBundleService = mock(I18nBundleService.class);
        I18nBundle i18nBundle = mock(I18nBundle.class);
        when(i18nBundle.get(any(String.class))).thenReturn(new I18nText(new HashMap<String, String>(3) {{
            put("fi", "osoite");put("en", "osoite");put("sv", "osoite");
        }}));
        when(i18nBundleService.getBundle(any(ApplicationSystem.class))).thenReturn(i18nBundle);

        officerUIService = new OfficerUIServiceImpl(
                applicationService,
                baseEducationService,
                formService,
                koodistoService,
                hakuPermissionService,
                loggerAspect, "", "",
                elementTreeValidator,
                applicationSystemService,
                authenticationService,
                organizationService,
                valintaService,
                userSession,
                null,
                null,
                "01.02 - 01.09");
        form.addChild(phase);
        when(applicationSystemService.getApplicationSystem(any(String.class))).thenReturn(as);
        when(applicationService.getApplication(OID)).thenReturn(application);
        when(applicationService.getApplicationByOid(OID)).thenReturn(application);
        when(applicationService.removeOrphanedAnswers(any(Application.class))).then(returnsFirstArg());
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
        assertTrue(11 == modelResponse.getModel().size());
    }

    private OfficerUIService createUiServiceForGrades(String oid, String asId, boolean transferred) {
        ApplicationService applicationService = mock(ApplicationService.class);
        when(applicationService.removeOrphanedAnswers(any(Application.class))).then(returnsFirstArg());
        when(applicationService.ensureApplicationOptionGroupData(any(Map.class), any(String.class))).then(returnsFirstArg());

        BaseEducationService baseEducationService = mock(BaseEducationService.class);
        FormService formService = mock(FormService.class);
        HakuPermissionService hakuPermissionService = mock(HakuPermissionService.class);

        Application application = new Application();
        application.setOid(oid);
        application.setState(Application.State.ACTIVE);
        application.setApplicationSystemId(asId);
        Map<String, String> answers = new HashMap<String, String>();
        answers.put("PK_GRADE", "foo");
        answers.put("notgrade", "foo");
        answers.put("stillnotgrade", "foo");
        application.addVaiheenVastaukset("osaaminen", answers);
        if (transferred) {
            application.addMeta("grades_transferred_pk", "true");
        }
        Form form = new Form(asId, new I18nText(new HashMap<String, String>()));
        form.addChild(PhaseBuilder.Phase("osaaminen")
                .setEditAllowedByRoles("all")
                .addChild(TextQuestionBuilder.TextQuestion("PK_GRADE"))
                .addChild(TextQuestionBuilder.TextQuestion("notgrade")).build());

        when(applicationService.getApplicationByOid(eq(oid))).thenReturn(application);
        when(formService.getForm(asId)).thenReturn(form);
        when(hakuPermissionService.userHasEditRoleToPhases(any(Application.class), any(Form.class)))
                .thenReturn(new SingletonMap("osaaminen", Boolean.TRUE));

        return new OfficerUIServiceImpl(applicationService, baseEducationService, formService,
                null, hakuPermissionService,loggerAspect, "", "", elementTreeValidator,
                null, null, null, null, userSession, null, null, null);
    }

    @Test
    public void testUpdateGrades() {
        String oid = "1.2.3";
        String asId = "4.5.6";
        OfficerUIService uiService = createUiServiceForGrades(oid, asId, false);

        Map<String, String> answers = new HashMap<String, String>();
        answers.put("PK_GRADE", "bar");
        answers.put("notgrade", "baz");
        ApplicationPhase phase = new ApplicationPhase(asId, "osaaminen", answers);
        ModelResponse response = null;
        try {
            response = uiService.updateApplication(oid, phase, null);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        Map<String, String> newAnswers = response.getApplication().getVastauksetMerged();
        assertEquals(2, answers.size());
        assertEquals("bar", newAnswers.get("PK_GRADE"));
        assertEquals("baz", newAnswers.get("notgrade"));
    }

    @Test
    public void testUpdateGradesTransferred() {
        String oid = "1.2.3";
        String asId = "4.5.6";
        OfficerUIService uiService = createUiServiceForGrades(oid, asId, true);

        Map<String, String> answers = new HashMap<String, String>();
        answers.put("notgrade", "baz");
        answers.put("stillnotgrade", "foo");
        ApplicationPhase phase = new ApplicationPhase(asId, "osaaminen", answers);
        ModelResponse response = null;
        try {
            response = uiService.updateApplication(oid, phase, null);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        Map<String, String> newAnswers = response.getApplication().getVastauksetMerged();
        assertEquals(4, newAnswers.size());
        assertEquals("foo", newAnswers.get("PK_GRADE"));
        assertEquals("baz", newAnswers.get("notgrade"));
        assertEquals("foo", newAnswers.get("stillnotgrade"));
        assertEquals("true", newAnswers.get("_meta_grades_transferred_pk"));
    }

    @Test
    public void testUpdateGradesFail() {
        String oid = "1.2.3";
        String asId = "4.5.6";
        OfficerUIService uiService = createUiServiceForGrades(oid, asId, true);

        Map<String, String> answers = new HashMap<String, String>();
        answers.put("PK_GRADE", "bar");
        answers.put("notgrade", "baz");
        ApplicationPhase phase = new ApplicationPhase(asId, "osaaminen", answers);

        thrown.expect(fi.vm.sade.haku.oppija.lomake.exception.IllegalStateException.class);
        thrown.expectMessage("Trying to change transferred grades");
        ModelResponse response = null;
        try {
            response = uiService.updateApplication(oid, phase, null);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        response.getApplication().getVastauksetMerged();
        fail("Should have thrown");
    }

    @Test
    public void testGetApplicationWithLastPhase() throws Exception {
        Application expectedApplication = officerUIService.getApplicationWithLastPhase(OID);
        assertEquals("esikatselu", expectedApplication.getPhaseId());
    }

    @Test
    public void testGetOrganizationAndLearningInstitutions() throws Exception {
        ModelResponse modelResponse = officerUIService.getOrganizationAndLearningInstitutions();
        assertEquals("Model size does not match", 10, modelResponse.getModel().size());
    }

    @Test
    public void testSaveApplicationAdditionalInfo() throws Exception {
        HashMap<String, String> additionalInfo = new HashMap<String, String>();
        officerUIService.saveApplicationAdditionalInfo(OID, additionalInfo);
        verify(applicationService, times(1)).saveApplicationAdditionalInfo(OID, additionalInfo);
    }
}
