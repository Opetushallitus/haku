package fi.vm.sade.haku.oppija.ui.service.impl;

import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.hakemus.aspect.LoggerAspect;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.Pistetieto;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.hakemus.service.HakuPermissionService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
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
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.haku.virkailija.valinta.ValintaService;
import fi.vm.sade.sijoittelu.tulos.dto.HakemuksenTila;
import fi.vm.sade.sijoittelu.tulos.dto.PistetietoDTO;
import fi.vm.sade.sijoittelu.tulos.dto.raportointi.HakijaDTO;
import fi.vm.sade.sijoittelu.tulos.dto.raportointi.HakutoiveDTO;
import fi.vm.sade.sijoittelu.tulos.dto.raportointi.HakutoiveenValintatapajonoDTO;
import fi.vm.sade.valintalaskenta.domain.dto.*;
import fi.vm.sade.valintalaskenta.domain.dto.valintakoe.OsallistuminenTulosDTO;
import fi.vm.sade.valintalaskenta.domain.dto.valintakoe.ValintakoeDTO;
import fi.vm.sade.valintalaskenta.domain.valintakoe.Osallistuminen;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValintaServiceTest {

    private static final String APPLICATION_OID = "1.2.3.4.5";
    private static final String AO_OID = "6.7.8.9";
    private static final String LOP_OID = "0.1.2.3";
    private static final String LOP_NAME = "Turun ammatti-instituutti - Åbo yrkesinstitut, Aninkaisten toimipiste";
    private static final String EXAM_ID = "valintakoe";
    private static final String AS_OID = "4.5.6";
    private static final String AO_NAME = "Hotelli-, ravintola- ja catering-alan perustutkinto, pk";
    private static final String ATHLETE_ID = "urheilija_lisapiste";
    private static final String LANG_TEST_ID = "kielikoe_fi";
    private static final String JONO_OID = "31415";
    Application application;

    OfficerUIServiceImpl officerUIService;

    @Before
    public void setUp() {
        application = new Application();
        Map<String, String> hakutoiveet = new HashMap<String, String>();

        hakutoiveet.put("preference1-Opetuspiste", LOP_NAME);
        hakutoiveet.put("preference1-Koulutus-educationDegree", "32");
        hakutoiveet.put("preference1_kaksoistutkinnon_lisakysymys", "false");
        hakutoiveet.put("preference1-Opetuspiste-id", LOP_OID);
        hakutoiveet.put("preference1-Koulutus-id-sora", "false");
        hakutoiveet.put("preference1-Koulutus-id-aoIdentifier", "473");
        hakutoiveet.put("preference1-Koulutus-id-educationcode", "koulutus_381112");
        hakutoiveet.put("preference1-Koulutus-id-vocational", "true");
        hakutoiveet.put("preference1-Koulutus-id-athlete", "true");
        hakutoiveet.put("preference1-discretionary", "false");
        hakutoiveet.put("preference1_urheilijan_ammatillisen_koulutuksen_lisakysymys", "false");
        hakutoiveet.put("preference1-Opetuspiste-id-parents", "1.2.246.562.10.00000000001,"+LOP_OID);
        hakutoiveet.put("preference1-Koulutus-id", AO_OID);
        hakutoiveet.put("preference1-Koulutus", AO_NAME);
        hakutoiveet.put("preference1-Koulutus-id-kaksoistutkinto", "true");
        hakutoiveet.put("preference1-Koulutus-id-lang", "FI");

        application.addVaiheenVastaukset(OppijaConstants.PHASE_APPLICATION_OPTIONS, hakutoiveet);
        application.setOid(APPLICATION_OID);
        application.setApplicationSystemId(AS_OID);

        ApplicationService applicationService = mock(ApplicationService.class);
        when(applicationService.getApplicationByOid(APPLICATION_OID)).thenReturn(application);

        FormService formService = mock(FormService.class);
        Form form = new Form("formId", ElementUtil.createI18NAsIs("formName"));
        Phase phase = new Phase("esikatselu", ElementUtil.createI18NAsIs("title"), false,
                Lists.newArrayList("APP_HAKEMUS_READ_UPDATE", "APP_HAKEMUS_CRUD", "APP_HAKEMUS_OPO"));
        form.addChild(phase);

        when(formService.getForm(anyString()))
                .thenReturn(form);

        ElementTreeValidator elementTreeValidator = new ElementTreeValidator(mock(ValidatorFactory.class));

        ApplicationSystemService applicationSystemService = mock(ApplicationSystemService.class);
        when(applicationSystemService.getApplicationSystem(anyString())).thenReturn(mock(ApplicationSystem.class));

        HakuPermissionService hakuPermissionService = mock(HakuPermissionService.class);
        when(hakuPermissionService.userHasEditRoleToPhases(any(Application.class), any(Form.class)))
                .thenReturn(new HashMap<String, Boolean>());
        when(hakuPermissionService.userCanDeleteApplication(any(Application.class))).thenReturn(true);
        when(hakuPermissionService.userCanPostProcess(any(Application.class))).thenReturn(true);

        UserSession userSession = mock(UserSession.class);
        User user = mock(User.class);
        when(userSession.getUser()).thenReturn(user);
        when(user.getUserName()).thenReturn("");

        KoodistoService koodistoService = null;
        LoggerAspect loggerAspect = null;
        AuthenticationService authenticationService = null;
        OrganizationService organizationService = null;

        officerUIService = new OfficerUIServiceImpl(
                applicationService,
                formService,
                koodistoService,
                hakuPermissionService,
                loggerAspect,
                "koulutusinformaatioBaseUrl",
                elementTreeValidator,
                applicationSystemService,
                authenticationService,
                organizationService,
                null,
                userSession);

    }

    @Test
    public void testGetValinta() throws Exception {
        HakijaDTO hakija = buildHakija();
        HakemusDTO hakemus = buildHakemus();

        ValintaService valintaService = mock(ValintaService.class);
        when(valintaService.getHakija(AS_OID, APPLICATION_OID)).thenReturn(hakija);
        when(valintaService.getHakemus(AS_OID, APPLICATION_OID)).thenReturn(hakemus);
        officerUIService.setValintaService(valintaService);

        ModelResponse response = officerUIService.getValidatedApplication(APPLICATION_OID, "esikatselu");
        List<ApplicationOptionDTO> hakukohdeList = (List<ApplicationOptionDTO>) response.getModel().get("hakukohteet");
        assertTrue(hakukohdeList.size() == 1);
        ApplicationOptionDTO ao = hakukohdeList.get(0);
        assertBasicAoInfo(ao);
        List<Pistetieto> pistetiedot = ao.getPistetiedot();
        assertEquals(5, pistetiedot.size());
    }

    @Test
    public void testGetValintaEmptySijoittelu() throws Exception {
        HakijaDTO hakija = new HakijaDTO();
        HakemusDTO hakemus = buildHakemus();

        ValintaService valintaService = mock(ValintaService.class);
        when(valintaService.getHakija(AS_OID, APPLICATION_OID)).thenReturn(hakija);
        when(valintaService.getHakemus(AS_OID, APPLICATION_OID)).thenReturn(hakemus);

        officerUIService.setValintaService(valintaService);

        ModelResponse response = officerUIService.getValidatedApplication(APPLICATION_OID, "esikatselu");
        List<ApplicationOptionDTO> hakukohdeList = (List<ApplicationOptionDTO>) response.getModel().get("hakukohteet");
        assertTrue(hakukohdeList.size() == 1);
        ApplicationOptionDTO ao = hakukohdeList.get(0);
        assertBasicAoInfo(ao);
        List<Pistetieto> pistetiedot = ao.getPistetiedot();
        assertEquals(5, pistetiedot.size());
    }

    private void assertBasicAoInfo(ApplicationOptionDTO ao) {
        assertEquals(AO_NAME, ao.getName());
        assertEquals(AO_OID, ao.getOid());
        assertEquals(LOP_OID, ao.getOpetuspisteOid());
        assertEquals(LOP_NAME, ao.getOpetuspiste());
    }

    private HakemusDTO buildHakemus() {
        List<HakukohdeDTO> hakukohteet = new ArrayList<HakukohdeDTO>();
        HakukohdeDTO hakukohdeDTO = new HakukohdeDTO();
        hakukohdeDTO.setHakuoid("");
        hakukohdeDTO.setOid(AO_OID);
        hakukohdeDTO.setTarjoajaoid(LOP_OID);
        List<ValinnanvaiheDTO> valinnanvaiheet = new ArrayList<ValinnanvaiheDTO>();
        ValinnanvaiheDTO vaihe = new ValinnanvaiheDTO();
        vaihe.setCreatedAt(new Date());
        vaihe.setJarjestysnumero(1);
        vaihe.setNimi("vaiheenNimi");
        vaihe.setValinnanvaiheoid("1");

        List<ValintakoeDTO> kokeet = new ArrayList<ValintakoeDTO>();
        kokeet.add(buildValintakoe("Soveltuvuuskoe", EXAM_ID, Osallistuminen.OSALLISTUU));
        kokeet.add(buildValintakoe("Yleinen kielikoe", LANG_TEST_ID, Osallistuminen.EI_OSALLISTU));
        kokeet.add(buildValintakoe("Urheilijalisäpiste", ATHLETE_ID, Osallistuminen.EI_OSALLISTU));
        vaihe.setValintakokeet(kokeet);

        List<ValintatapajonoDTO> jonot = new ArrayList<ValintatapajonoDTO>();
        ValintatapajonoDTO jono = new ValintatapajonoDTO();
        jono.setAloituspaikat(2);
        jono.setEiVarasijatayttoa(true);
        jono.setNimi("jono");
        jono.setOid(JONO_OID);
        jono.setPrioriteetti(1);
        jono.setSiirretaanSijoitteluun(true);
        jono.setTasasijasaanto(null);
        List<JonosijaDTO> jonosijat = new ArrayList<JonosijaDTO>();
        JonosijaDTO sija = new JonosijaDTO();

        List<FunktioTulosDTO> funktioTulokset = new ArrayList<FunktioTulosDTO>();
        funktioTulokset.add(buildFunktioTulos("keskiarvo", "8.0", "Kaikkien aineiden keskiarvo"));
        funktioTulokset.add(buildFunktioTulos("sukupuoli", "0", "Sukupuoli"));

        sija.setFunktioTulokset(funktioTulokset);
        
        jonosijat.add(sija);
        jono.setJonosijat(jonosijat);
        jonot.add(jono);
        vaihe.setValintatapajonot(jonot);
        valinnanvaiheet.add(vaihe);
        hakukohdeDTO.setValinnanvaihe(valinnanvaiheet);

        hakukohteet.add(hakukohdeDTO);
        HakemusDTO hakemus = new HakemusDTO("", APPLICATION_OID, hakukohteet);

        return hakemus;
    }

    private ValintakoeDTO buildValintakoe(String nimi, String tunniste, Osallistuminen osallistuminen) {
        ValintakoeDTO koe = new ValintakoeDTO();
        koe.setAktiivinen(true);
        koe.setLahetetaankoKoekutsut(true);
        koe.setNimi(nimi);
        koe.setValintakoeTunniste(tunniste);
        OsallistuminenTulosDTO osallistuminenTulos = new OsallistuminenTulosDTO();
        osallistuminenTulos.setKuvaus(null);
        osallistuminenTulos.setLaskentaTila("HYVAKSYTTAVISSA");
        osallistuminenTulos.setLaskentaTulos(false);
        osallistuminenTulos.setOsallistuminen(osallistuminen);

        koe.setOsallistuminenTulos(osallistuminenTulos);
        return koe;
    }

    private FunktioTulosDTO buildFunktioTulos(String tunniste, String arvo, String nimi) {
        FunktioTulosDTO tulos = new FunktioTulosDTO();
        tulos.setTunniste(tunniste);
        tulos.setArvo(arvo);
        tulos.setNimiFi(nimi + " [fi]");
        tulos.setNimiSv(nimi + " [sv]");
        tulos.setNimiEn(nimi + " [en]");
        return tulos;
    }

    private HakijaDTO buildHakija() {

        HakijaDTO hakija = new HakijaDTO();
        hakija.setEtunimi("etunimi");
        hakija.setSukunimi("sukunimi");
        hakija.setHakemusOid(APPLICATION_OID);
        SortedSet<HakutoiveDTO> hakutoiveet = new TreeSet<HakutoiveDTO>();
        HakutoiveDTO hakutoive = new HakutoiveDTO();
        hakutoive.setHakukohdeOid(AO_OID);
        hakutoive.setHakutoive(1);
        hakutoive.setTarjoajaOid(LOP_OID);

        HakutoiveenValintatapajonoDTO jono = new HakutoiveenValintatapajonoDTO();
        jono.setValintatapajonoOid(JONO_OID);
        jono.setPisteet(BigDecimal.valueOf(12.0));
        jono.setTila(HakemuksenTila.HYVAKSYTTY);
        jono.setValintatapajonoPrioriteetti(1);
        List<HakutoiveenValintatapajonoDTO> jonot = new ArrayList<HakutoiveenValintatapajonoDTO>();
        jonot.add(jono);
        hakutoive.setHakutoiveenValintatapajonot(jonot);
        hakutoiveet.add(hakutoive);
        hakija.setHakutoiveet(hakutoiveet);

        List<PistetietoDTO> pistetiedot = new ArrayList<PistetietoDTO>();
        pistetiedot.add(buildPistetieto("4", "4", "OSALLISTUI", EXAM_ID));
        pistetiedot.add(buildPistetieto(null, "0.0", "MERKITSEMATTA", ATHLETE_ID));
        pistetiedot.add(buildPistetieto(null, "false", "MERKITSEMATTA", LANG_TEST_ID));

        hakutoive.setPistetiedot(pistetiedot);

        return hakija;
    }

    private PistetietoDTO buildPistetieto(String arvo, String laskennallinenArvo, String osallistuminen, String tunniste) {
        PistetietoDTO pistetietoDTO = new PistetietoDTO();
        pistetietoDTO.setArvo(arvo);
        pistetietoDTO.setLaskennallinenArvo(laskennallinenArvo);
        pistetietoDTO.setOsallistuminen(osallistuminen);
        pistetietoDTO.setTunniste(tunniste);
        return pistetietoDTO;
    }
}
