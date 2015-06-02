package fi.vm.sade.haku.oppija.ui.service.impl;

import fi.vm.sade.generic.rest.CachingRestClient;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.Pistetieto;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.hakemus.service.HakuPermissionService;
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

    @Test
    public void testHappy() throws IOException {
        CachingRestClient sijoitteluClient = mock(CachingRestClient.class);
        CachingRestClient valintaClient = mock(CachingRestClient.class);
        when(sijoitteluClient.getAsString(startsWith("/resources/sijoittelu"))).thenReturn(fileAsString("sijoittelu1.json"));
        when(valintaClient.getAsString(startsWith("/resources/hakemus"))).thenReturn(fileAsString("laskenta1.json"));

        ValintaServiceImpl valintaService = new ValintaServiceImpl();
        valintaService.setCachingRestClientSijoittelu(sijoitteluClient);
        valintaService.setCachingRestClientValinta(valintaClient);
        HakemusDTO hakemus = valintaService.getHakemus("", "");
        assertNotNull(hakemus);
    }

    @Test
    public void testOfficerUi() throws IOException {
        CachingRestClient sijoitteluClient = mock(CachingRestClient.class);
        CachingRestClient valintaClient = mock(CachingRestClient.class);
        when(sijoitteluClient.getAsString(startsWith("/resources/sijoittelu"))).thenReturn(fileAsString("sijoittelu1.json"));
        when(valintaClient.getAsString(startsWith("/resources/hakemus"))).thenReturn(fileAsString("laskenta1.json"));

        ValintaServiceImpl valintaService = new ValintaServiceImpl();
        valintaService.setCachingRestClientSijoittelu(sijoitteluClient);
        valintaService.setCachingRestClientValinta(valintaClient);

        ApplicationService applicationService = mock(ApplicationService.class);
        Application application = new Application();
        application.setState(Application.State.ACTIVE);
        Map<String, String> aoMap = new HashMap<String, String>(){{
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
        Map<String, String> eduMap = new HashMap<String, String>() {{
            put("LISAKOULUTUS_KANSANOPISTO", "true");
            put("PK_PAATTOTODISTUSVUOSI", "2012");
            put("ammatillinenTutkintoSuoritettu", "false");
            put("KOULUTUSPAIKKA_AMMATILLISEEN_TUTKINTOON", "false");
            put("POHJAKOULUTUS", "1");
            put("perusopetuksen_kieli", "SV");
        }};
        application.addVaiheenVastaukset(OppijaConstants.PHASE_APPLICATION_OPTIONS, aoMap);
        application.addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, eduMap);
        when(applicationService.getApplicationByOid(eq("oid"))).thenReturn(application);

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
                hakupermissionService, null, null, null, elementTreeValidator, applicationSystemService,
                null, null, valintaService, session, null, null);
        ModelResponse response = officerUIService.getValidatedApplication("oid", "esikatselu");

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
}
