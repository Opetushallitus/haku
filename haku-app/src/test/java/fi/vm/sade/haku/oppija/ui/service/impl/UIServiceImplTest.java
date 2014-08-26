package fi.vm.sade.haku.oppija.ui.service.impl;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystemBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.service.UserSession;
import fi.vm.sade.haku.oppija.ui.service.ModelResponse;
import fi.vm.sade.haku.virkailija.koulutusinformaatio.KoulutusinformaatioService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.haku.virkailija.viestintapalvelu.PDFService;
import fi.vm.sade.koulutusinformaatio.domain.dto.AddressDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOfficeDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunityProviderDTO;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UIServiceImplTest {

    ApplicationService applicationService = mock(ApplicationService.class);
    ApplicationSystemService applicationSystemService = mock(ApplicationSystemService.class);
    UserSession userSession = mock(UserSession.class);
    KoulutusinformaatioService koulutusinformaatioService = mock(KoulutusinformaatioService.class);
    PDFService pdfService = mock(PDFService.class);
    
    String koulutusinformaatioBaseurl = "";

    Map<String, ApplicationOptionDTO> aos;
    @Before
    public void setUp() {
        aos = new HashMap<String, ApplicationOptionDTO>();
        aos.put("1.2.3", createApplicationOption("1.2.3", "office123", "00123", "POSTAL 123", "Street 123"));
        aos.put("4.5.6", createApplicationOption("4.5.6", "office789", "00789", "POSTAL 789", "Street 789"));
        aos.put("7.8.9", createApplicationOption("7.8.9", "office789", "00789", "POSTAL 789", "Street 789"));
    }

    private ApplicationOptionDTO createApplicationOption(String oid , String officeName, String postalCode, String postalOffice,
                                                         String streetAddress) {
        ApplicationOptionDTO ao = new ApplicationOptionDTO();
        ao.setId(oid);
        LearningOpportunityProviderDTO provider = new LearningOpportunityProviderDTO();
        ApplicationOfficeDTO office = new ApplicationOfficeDTO();
        office.setName(officeName);
        AddressDTO address = new AddressDTO();
        address.setPostalCode(postalCode);
        address.setPostOffice(postalOffice);
        address.setStreetAddress(streetAddress);
        office.setPostalAddress(address);
        provider.setApplicationOffice(office);
        ao.setProvider(provider);
        return ao;
    }

    @Test
    public void testCompleteApplicationAttachments() {

        UIServiceImpl service = new UIServiceImpl(applicationService, applicationSystemService, userSession,
                koulutusinformaatioService, koulutusinformaatioBaseurl, pdfService);

        String asId = "1.2.3";
        String oid = "4.5.6";
        ApplicationSystem as = buildApplicationSystem(asId);
        Application application = buildApplication(asId, oid);
        when(applicationSystemService.getActiveApplicationSystem(eq(asId))).thenReturn(as);
        when(applicationService.getSubmittedApplication(eq(asId), eq(oid))).thenReturn(application);
        when(koulutusinformaatioService.getApplicationOption(eq("1.2.3"))).thenReturn(aos.get("1.2.3"));
        when(koulutusinformaatioService.getApplicationOption(eq("4.5.6"))).thenReturn(aos.get("4.5.6"));
        when(koulutusinformaatioService.getApplicationOption(eq("7.8.9"))).thenReturn(aos.get("7.8.9"));

        ModelResponse response = service.getCompleteApplication(asId, oid);
        assertNotNull(response);
        Map<String, List<ApplicationOptionDTO>> attachments = (Map<String, List<ApplicationOptionDTO>>)
                response.getModel().get("higherEducationAttachments");
        assertNotNull(attachments);
        assertEquals(2, attachments.size());
        assertTrue(attachments.containsKey("yo"));
        assertTrue(attachments.containsKey("muu"));
        assertEquals(1, attachments.get("yo").size());
        assertEquals("00789",attachments.get("yo").get(0).getProvider().getApplicationOffice().getPostalAddress().getPostalCode());
        assertEquals(2, attachments.get("muu").size());
        boolean found123 = false;
        boolean found789 = false;
        for (ApplicationOptionDTO ao : attachments.get("muu")) {
            if (ao.getProvider().getApplicationOffice().getPostalAddress().getPostalCode().equals("00123")) {
                assertFalse(found123);
                found123 = true;
            }
            if (ao.getProvider().getApplicationOffice().getPostalAddress().getPostalCode().equals("00789")) {
                assertFalse(found789);
                found789 = true;
            }
        }
        assertTrue(found123);
        assertTrue(found789);
    }

    private Application buildApplication(String asId, String oid) {
        Application application = new Application();
        application.setApplicationSystemId(asId);
        application.setOid(oid);


        Map<String, String> baseEd = new HashMap<String, String>();
        Map<String, String> prefs = new HashMap<String, String>();

        baseEd.put("pohjakoulutus_yo", "true");
        baseEd.put("pohjakoulutus_yo_vuosi", "2012");
        baseEd.put("pohjakoulutus_yo_tutkinto", "eb");
        baseEd.put("pohjakoulutus_muu", "true");

        prefs.put("preference1-amkLiite", "true");
        prefs.put("preference1-Koulutus-id", "1.2.3");

        prefs.put("preference2-yoLiite", "true");
        prefs.put("preference2-Koulutus-id", "4.5.6");

        prefs.put("preference3-yoLiite", "true");
        prefs.put("preference3-Koulutus-id", "7.8.9");

        application.addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, baseEd);
        application.addVaiheenVastaukset(OppijaConstants.PHASE_APPLICATION_OPTIONS, prefs);

        return application;
    }

    private ApplicationSystem buildApplicationSystem(String asId) {

        ApplicationSystemBuilder builder = new ApplicationSystemBuilder()
                .addId(asId)
                .addName(new I18nText(new HashMap<String, String>()));

        return builder.get();
    }
}
