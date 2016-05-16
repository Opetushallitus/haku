package fi.vm.sade.haku.oppija.ui.service.impl;

import fi.vm.sade.generic.rest.CachingRestClient;
import fi.vm.sade.haku.oppija.configuration.UrlConfiguration;
import fi.vm.sade.haku.oppija.hakemus.aspect.LoggerAspect;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.hakemus.service.HakuPermissionService;
import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.ModelResponse;
import fi.vm.sade.haku.oppija.lomake.domain.User;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import fi.vm.sade.haku.oppija.lomake.service.impl.UserSession;
import fi.vm.sade.haku.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidatorFactory;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.haku.virkailija.valinta.impl.ValintaServiceImpl;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OfficerUIServiceImplUpdateDiscretionaryTest {
    private UrlConfiguration urlConfiguration = new UrlConfiguration(UrlConfiguration.SPRING_IT_PROFILE);
    private OfficerUIServiceImpl officerUIService;
    private User user;
    private Application application = new Application();

    public OfficerUIServiceImplUpdateDiscretionaryTest() {
        urlConfiguration.addDefault("host.cas","localhost").addDefault("host.virkailija","localhost:9090");
    }

    @Before
    public void setUp() throws Exception {
        CachingRestClient sijoitteluClient = mock(CachingRestClient.class);
        CachingRestClient valintaClient = mock(CachingRestClient.class);
        when(sijoitteluClient.getAsString(startsWith("https://localhost:9090/sijoittelu-service/resources/sijoittelu"))).thenReturn(fileAsString("sijoittelu1.json"));
        when(valintaClient.getAsString(startsWith("https://localhost:9090/valintalaskenta-laskenta-service/resources/hakemus"))).thenReturn(fileAsString("laskenta1.json"));

        ValintaServiceImpl valintaService = new ValintaServiceImpl(urlConfiguration);
        valintaService.setCachingRestClientValinta(valintaClient);

        ApplicationService applicationService = mock(ApplicationService.class);

        application.setState(Application.State.ACTIVE);
        when(applicationService.getApplicationByOid(eq("oid"))).thenReturn(application);
        when(applicationService.getApplicationWithValintadata(application)).thenReturn(application);

        FormService formService = mock(FormService.class);
        Form form = mock(Form.class);
        when(formService.getForm(any(String.class))).thenReturn(form);
        when(form.getChildById(any(String.class))).thenReturn(mock(Element.class));

        ValidatorFactory validatorFactory = mock(ValidatorFactory.class);
        ElementTreeValidator elementTreeValidator = new ElementTreeValidator(validatorFactory);


        Map<String, Boolean> perms = mock(HashMap.class);
        when(perms.get(any(String.class))).thenReturn(true);
        HakuPermissionService hakupermissionService = mock(HakuPermissionService.class);
        when(hakupermissionService.userHasEditRoleToPhases(any(ApplicationSystem.class), any(Application.class), any(Form.class))).thenReturn(perms);

        ApplicationSystemService applicationSystemService = mock(ApplicationSystemService.class);
        ApplicationSystem applicationSystem = mock(ApplicationSystem.class);

        when(applicationSystemService.getApplicationSystem(any(String.class))).thenReturn(applicationSystem);
        when(applicationSystem.getId()).thenReturn("asId");
        when(applicationSystem.getKohdejoukkoUri()).thenReturn(KOHDEJOUKKO_AMMATILLINEN_JA_LUKIO);

        UserSession session = mock(UserSession.class);
        user = mock(User.class);
        when(user.getUserName()).thenReturn(null);
        when(session.getUser()).thenReturn(user);

        officerUIService = new OfficerUIServiceImpl(applicationService, formService, null,
                hakupermissionService, mock(LoggerAspect.class), new UrlConfiguration(), elementTreeValidator, applicationSystemService,
                null, null, valintaService, session, null, mock(HakumaksuService.class), null);
    }

    @Test
    public void testUpdateKoulutusNotUlkomainenOrKesken() throws Exception {
        application.setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_APPLICATION_OPTIONS, createHakutoiveet());
        ApplicationPhase phase = new ApplicationPhase("asId", PHASE_EDUCATION, createNewAnswers(PERUSKOULU));
        updateAndAssertNoChanges(phase);
    }


    @Test
    public void testUpdateKoulutusToDiscretionaryWhenKoulutusIsNotDiscretionary() throws Exception {
        application.setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_APPLICATION_OPTIONS, createHakutoiveet());
        ApplicationPhase phase = new ApplicationPhase("asId", PHASE_EDUCATION, createNewAnswers(ULKOMAINEN_TUTKINTO));
        updateAndAssertNoChanges(phase);
    }

    @Test
    public void testUpdateKoulutusToDiscretionaryWhenKoulutusIsDiscretionaryAndUlkomainen() throws Exception {
        initDiscretionaryHakutoiveet();
        ApplicationPhase phase = new ApplicationPhase("asId", PHASE_EDUCATION, createNewAnswers(ULKOMAINEN_TUTKINTO));
        updateAndAssertChangedToDiscretionary(phase);
    }

    @Test
    public void testUpdateKoulutusToDiscretionaryWhenKoulutusIsDiscretionaryAndKeskenjaanyt() throws Exception {
        initDiscretionaryHakutoiveet();
        ApplicationPhase phase = new ApplicationPhase("asId", PHASE_EDUCATION, createNewAnswers(KESKEYTYNYT));
        updateAndAssertChangedToDiscretionary(phase);
    }

    @Test
    public void testUpdateKoulutusToNotDiscretionary() throws Exception {
        final HashMap<String, String> hakutoiveet = createHakutoiveet();
        hakutoiveet.put("preference1-Koulutus-id-discretionary", "true");
        hakutoiveet.put("preference1-discretionary", "true");
        application.setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_APPLICATION_OPTIONS, hakutoiveet);
        ApplicationPhase phase = new ApplicationPhase("asId", PHASE_EDUCATION, createNewAnswers(YLIOPPILAS));
        ModelResponse response = officerUIService.updateApplication("oid", phase, user);
        final Map<String, String> answers = (Map<String, String>) response.getModel().get("answers");
        assertEquals(14, answers.size());
        assertFalse(answers.containsKey("preference1--discretionary"));
    }

    private void updateAndAssertNoChanges(ApplicationPhase phase) throws IOException {
        ModelResponse response = officerUIService.updateApplication("oid", phase, user);
        final Map<String, String> answers = (Map<String, String>) response.getModel().get("answers");
        assertEquals(13, answers.size());
        assertFalse(answers.containsKey("preference1-discretionary"));
    }

    private void updateAndAssertChangedToDiscretionary(ApplicationPhase phase) throws IOException {
        ModelResponse response = officerUIService.updateApplication("oid", phase, user);
        final Map<String, String> answers = (Map<String, String>) response.getModel().get("answers");
        assertEquals(15, answers.size());
        assertEquals("true", answers.get("preference1-discretionary"));
    }

    private void initDiscretionaryHakutoiveet() {
        final HashMap<String, String> hakutoiveet = createHakutoiveet();
        hakutoiveet.put("preference1-Koulutus-id-discretionary", "true");
        application.setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_APPLICATION_OPTIONS, hakutoiveet);
    }

    private Map<String, String> createNewAnswers(final String pohjakoulutus) {
        return new HashMap<String, String>() {{
                put("ammatillinenTutkintoSuoritettu", "false");
                put("POHJAKOULUTUS", pohjakoulutus);
                put("phaseId", "koulutustausta");
            }};
    }

    private String fileAsString(String filename) throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(filename);
        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer, "UTF-8");
        String json = writer.toString();
        return json.trim();
    }

    public static HashMap<String, String> createHakutoiveet() {
        return new HashMap<String, String>(){{
            put("preference1-Opetuspiste", "Yrkesakademin i Österbotten, Jakobstad, Köpmansgatan");
            put("preference1-Koulutus-educationDegree", "32");
            put("preference1_kaksoistutkinnon_lisakysymys", "false");
            put("preference1-Opetuspiste-id", "1.2.246.562.10.56173627367");
            put("preference1-Koulutus-id-sora", "false");
            put("preference1-Koulutus-id-aoIdentifier", "460");
            put("preference1-Koulutus-id-educationcode", "koulutus_321204");
            put("preference1-Koulutus-id-vocational", "true");
            put("preference1-Koulutus-id-attachments", "true");
            put("preference1-Koulutus-id", "1.2.246.562.20.65821322891");
            put("preference1-Koulutus", "Utbildningsprogrammet för musik, gr (Grundexamen i musik)");
            put("preference1-Koulutus-id-kaksoistutkinto", "true");
            put("preference1-Koulutus-id-lang", "SV");
        }};
    }
}
