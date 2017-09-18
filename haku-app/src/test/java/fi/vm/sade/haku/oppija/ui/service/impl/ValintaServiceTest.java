package fi.vm.sade.haku.oppija.ui.service.impl;

import fi.vm.sade.authentication.cas.CasClient;
import fi.vm.sade.generic.rest.CachingRestClient;
import fi.vm.sade.haku.VirkailijaAuditLogger;
import fi.vm.sade.haku.oppija.configuration.UrlConfiguration;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.Pistetieto;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.hakemus.service.HakuPermissionService;
import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.ModelResponse;
import fi.vm.sade.haku.oppija.lomake.domain.User;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import fi.vm.sade.haku.oppija.lomake.service.impl.UserSession;
import fi.vm.sade.haku.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidatorFactory;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.haku.virkailija.valinta.dto.HakemusDTO;
import fi.vm.sade.haku.virkailija.valinta.dto.Osallistuminen;
import fi.vm.sade.haku.virkailija.valinta.impl.ValintaServiceImpl;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValintaServiceTest {

    private UrlConfiguration urlConfiguration = new UrlConfiguration(UrlConfiguration.SPRING_IT_PROFILE);

    public ValintaServiceTest() {
        urlConfiguration.addDefault("host.cas","localhost").addDefault("host.virkailija","localhost:9090");
    }

    @Test
    public void testHappy() throws IOException {
        CachingRestClient sijoitteluClient = mock(CachingRestClient.class);
        CachingRestClient valintaClient = mock(CachingRestClient.class);
        when(sijoitteluClient.getAsString(startsWith("https://localhost:9090/sijoittelu-service/resources/sijoittelu"))).thenReturn(fileAsString("sijoittelu1.json"));
        when(valintaClient.getAsString(startsWith("https://localhost:9090/valintalaskenta-laskenta-service/resources/hakemus"))).thenReturn(fileAsString("laskenta1.json"));

        ValintaServiceImpl valintaService = new ValintaServiceImpl(urlConfiguration);
        valintaService.setCachingRestClientValinta(valintaClient);
        HakemusDTO hakemus = valintaService.getHakemus("", "");
        assertNotNull(hakemus);
    }

    @Test
    public void testOfficerUi() throws Exception {
        CachingRestClient sijoitteluClient = mock(CachingRestClient.class);
        CachingRestClient valintaClient = mock(CachingRestClient.class);

        // mocking valintarekisteri
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse httpresponse = mock(HttpResponse.class);
        HttpEntity httpEntity = mock(HttpEntity.class);
        StatusLine statusline = mock(StatusLine.class);
        IOUtils ioUtils = mock(IOUtils.class);
        InputStream inputStream = IOUtils.toInputStream(fileAsString("valintarekisteritulos.json"));

        when(statusline.getStatusCode()).thenReturn(200);
        when(httpresponse.getStatusLine()).thenReturn(statusline);
        when(httpEntity.getContent()).thenReturn(inputStream);
        when(httpresponse.getEntity()).thenReturn(httpEntity);
        when(httpClient.execute(any())).thenReturn(httpresponse);

        when(sijoitteluClient.getAsString(startsWith("https://localhost:9090/sijoittelu-service/resources/sijoittelu"))).thenReturn(fileAsString("sijoittelu1.json"));
        when(valintaClient.getAsString(startsWith("https://localhost:9090/valintalaskenta-laskenta-service/resources/hakemus"))).thenReturn(fileAsString("laskenta1.json"));

        ValintaServiceImpl valintaService = new ValintaServiceImpl(urlConfiguration);
        valintaService.setCachingRestClientValinta(valintaClient);

        valintaService.setValintarekisteriHeaders(new Header[]{});
        valintaService.setHttpClient(httpClient);

        ApplicationService applicationService = mock(ApplicationService.class);
        Application application = new Application();
        application.setApplicationSystemId("oid");
        application.setOid("1.2.246.562.11.00002361873");
        application.setState(Application.State.ACTIVE);
        Map<String, String> aoMap = createHakutoiveet();
        Map<String, String> eduMap = new HashMap<String, String>() {{
            put(OppijaConstants.ELEMENT_ID_LISAKOULUTUS_KANSANOPISTO, "true");
            put("PK_PAATTOTODISTUSVUOSI", "2012");
            put("ammatillinenTutkintoSuoritettu", "false");
            put("KOULUTUSPAIKKA_AMMATILLISEEN_TUTKINTOON", "false");
            put("POHJAKOULUTUS", "1");
            put("perusopetuksen_kieli", "SV");
        }};
        application.setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_APPLICATION_OPTIONS, aoMap);
        application.setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_EDUCATION, eduMap);
        when(applicationService.getApplicationByOid(eq("oid"))).thenReturn(application);
        when(applicationService.getApplicationWithValintadata(application)).thenReturn(application);

        FormService formService = mock(FormService.class);
        when(formService.getForm(any(String.class))).thenReturn(new Form(null, null));

        ValidatorFactory validatorFactory = mock(ValidatorFactory.class);
        ElementTreeValidator elementTreeValidator = new ElementTreeValidator(validatorFactory);

        HakuPermissionService hakupermissionService = mock(HakuPermissionService.class);
        when(hakupermissionService.userCanPostProcess(any(Application.class))).thenReturn(true);

        ApplicationSystemService applicationSystemService = mock(ApplicationSystemService.class);
        ApplicationSystem applicationSystem = mock(ApplicationSystem.class);

        when(applicationSystemService.getApplicationSystem(any(String.class))).thenReturn(applicationSystem);

        UserSession session = mock(UserSession.class);
        User user = mock(User.class);
        when(user.getUserName()).thenReturn(null);
        when(session.getUser()).thenReturn(user);

        OfficerUIServiceImpl officerUIService = new OfficerUIServiceImpl(applicationService, formService, null,
                hakupermissionService, null, new UrlConfiguration(), elementTreeValidator, applicationSystemService,
                null, null, valintaService, session, null, mock(VirkailijaAuditLogger.class),
                mock(HakumaksuService.class), null, "true");
        ModelResponse response = officerUIService.getValidatedApplication("oid", "esikatselu", true);

        List<ApplicationOptionDTO> hakukohteet = (List<ApplicationOptionDTO>) response.getModel().get("hakukohteet");
        assertEquals(1, hakukohteet.size());
        ApplicationOptionDTO hakukohde = hakukohteet.get(0);
        assertEquals(3, hakukohde.getPistetiedot().size());
        boolean kielikoeFound = false;
        boolean urheilijaFound = false;
        boolean paasykoeFound = false;
        for (Pistetieto p : hakukohde.getPistetiedot()) {
            String nimi = p.getNimi().getTranslations().get("fi");
            switch (nimi) {
                case "Kielikoe":
                    assertEquals(Osallistuminen.EI_OSALLISTU, p.getOsallistuminen());
                    kielikoeFound = true;
                    break;
                case "Urheilijalisäpiste":
                    assertEquals(Osallistuminen.EI_OSALLISTU, p.getOsallistuminen());
                    urheilijaFound = true;
                    break;
                case "Pääsy- ja soveltuvuuskoe":
                    assertEquals(Osallistuminen.OSALLISTUU, p.getOsallistuminen());
                    paasykoeFound = true;
                    break;
                default:
                    fail("Found unknown exam: "+nimi);
                    break;
            }
        }
        assertTrue(kielikoeFound);
        assertTrue(urheilijaFound);
        assertTrue(paasykoeFound);

    }

    private String fileAsString(String filename) throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(filename);
        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer, "UTF-8");
        String json = writer.toString();
        return json.trim();
    }

    private HashMap<String, String> createHakutoiveet() {
        return new HashMap<String, String>() {{
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
