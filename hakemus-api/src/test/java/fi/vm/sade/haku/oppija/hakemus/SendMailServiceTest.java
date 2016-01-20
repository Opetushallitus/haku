package fi.vm.sade.haku.oppija.hakemus;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import fi.vm.sade.haku.http.MockedRestClient;
import fi.vm.sade.haku.http.MockedRestClient.Captured;
import fi.vm.sade.haku.oppija.common.oppijantunnistus.OppijanTunnistusDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.service.impl.SendMailService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.haku.virkailija.viestintapalvelu.impl.EmailServiceMockImpl;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailData;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailRecipient;
import org.apache.commons.mail.EmailException;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.Iterator;

import static fi.vm.sade.haku.oppija.common.oppijantunnistus.OppijanTunnistusDTO.LanguageCodeISO6391.fi;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.MailTemplateUtil.dateTimeFormatter;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class SendMailServiceTest {

    String oppijantunnistusUrl = "http://localhost/oppijan-tunnistus";
    String linkFi = "http://localhost/fi/omatsivut";
    String linkSv = "http://localhost/sv/omatsivut";
    String linkEn = "http://localhost/en/omatsivut";
    String applicationSystemId = "1.2.246.562.29.75203638285";
    String applicationSystemIdSecondary = "1.2.246.562.29.75203638285";
    String applicationSystemIdErkka = "oid.erkka";
    String applicationSystemIdJatkuva = "oid.jatkuva";
    String applicationSystemIdSiirtohaku = "oid.siirtohaku";
    String applicationOid = "1.2.246.562.11.1";
    String emailAddress = "testi@example.com";
    String emailAddressGuardian = "guardian@example.com";
    String hakuNimiFi = "haku 1";
    Date receivedDate = new Date(new Date().getTime() - 10000);
    Date modifiedDate = new Date();

    @Test
    public void testSendReceivedEmail() throws EmailException {
        service.sendReceivedEmail(testApplication());

        Captured firstCaptured = restClient.getCaptured().iterator().next();
        OppijanTunnistusDTO capturedBody = (OppijanTunnistusDTO) firstCaptured.body;

        assertEquals(firstCaptured.url, oppijantunnistusUrl);
        assertEquals(firstCaptured.method, "POST");
        assertEquals(capturedBody.url, linkFi);
        assertEquals(capturedBody.lang, fi);
        assertEquals(capturedBody.email, emailAddress);
        assertEquals(capturedBody.metadata.hakemusOid, applicationOid);
        assertTrue(capturedBody.template.contains(hakuNimiFi));
    }

    @Test
    public void testSendModifiedEmailSecondary() throws EmailException {
        service.sendModifiedEmail(testApplicationSecondary());

        Iterator<Captured> iterator = restClient.getCaptured().iterator();
        OppijanTunnistusDTO oppijaEmailBody = (OppijanTunnistusDTO) iterator.next().body;
        OppijanTunnistusDTO guardianEmailBody = (OppijanTunnistusDTO) iterator.next().body;

        assertEquals(oppijaEmailBody.email, emailAddress);
        assertEquals(guardianEmailBody.email, emailAddressGuardian);
        assertEquals(oppijaEmailBody.metadata.hakemusOid, applicationOid);
        assertEquals(guardianEmailBody.metadata.hakemusOid, applicationOid);
        assertEquals(guardianEmailBody.metadata.hakemusOid, applicationOid);
        assertTrue(guardianEmailBody.subject.contains("huoltaja"));
        assertTrue(guardianEmailBody.template.contains(dateTimeFormatter(SendMailService.FI).format(receivedDate)));
    }

    @Test
    public void testInDemoModeItShouldNotSendEmailRequest() throws EmailException {
        setField(service, "demoMode", true);

        service.sendReceivedEmail(testApplication());

        assertTrue(restClient.getCaptured().isEmpty());
    }

    @Test
    public void testThatErkkaHakuSendsNonSecurelinkVersionOfEmail() throws EmailException {
        service.sendReceivedEmail(testApplicationErkka());
        assertNonSecurelinkMail();
    }

    @Test
    public void testThatJatkuvaHakuSendsNonSecurelinkVersionOfEmail() throws EmailException {
        service.sendReceivedEmail(testApplicationJatkuva());
        assertNonSecurelinkMail();
    }

    @Test
    public void testThatSiirtohakuSendsNonSecurelinkVersionOfEmail() throws EmailException {
        service.sendReceivedEmail(testApplicationSiirtohaku());
        assertNonSecurelinkMail();
    }

    private void assertNonSecurelinkMail() {
        EmailData sentMail = emailServiceMockImpl.getLastSentMail();
        assertNotNull(Iterables.find(sentMail.getRecipient(), new Predicate<EmailRecipient>() {
            public boolean apply(EmailRecipient recipient) {
                return recipient.getEmail().equals(emailAddress);
            }
        }));

        String body = sentMail.getEmail().getBody();
        assertTrue(body.contains("Oma Opintopolku -palvelussa"));
        assertFalse(body.contains("seuraavan linkin kautta"));
    }

    MockedRestClient restClient = new MockedRestClient();
    EmailServiceMockImpl emailServiceMockImpl = new EmailServiceMockImpl();
    ApplicationSystemService applicationSystemService;
    SendMailService service;

    @Before
    public void setupServices() {
        applicationSystemService = mock(ApplicationSystemService.class);

        mockApplicationSystem(applicationSystemId, OppijaConstants.KOHDEJOUKKO_KORKEAKOULU);
        mockApplicationSystem(applicationSystemIdSecondary, OppijaConstants.KOHDEJOUKKO_AMMATILLINEN_JA_LUKIO);
        mockApplicationSystem(applicationSystemIdErkka, OppijaConstants.KOHDEJOUKKO_ERITYISOPETUKSENA_JARJESTETTAVA_AMMATILLINEN);

        ApplicationSystem jatkuvaHaku = mockApplicationSystem(applicationSystemIdJatkuva, OppijaConstants.KOHDEJOUKKO_KORKEAKOULU);
        when(jatkuvaHaku.getHakutapa()).thenReturn(OppijaConstants.HAKUTAPA_JATKUVA_HAKU);

        ApplicationSystem siirtohaku = mockApplicationSystem(applicationSystemIdSiirtohaku, OppijaConstants.KOHDEJOUKKO_KORKEAKOULU);
        when(siirtohaku.getKohdejoukonTarkenne()).thenReturn(OppijaConstants.KOHDEJOUKON_TARKENNE_SIIRTOHAKU);

        service = new SendMailService(applicationSystemService, restClient, emailServiceMockImpl, linkFi, linkSv, linkEn);
        setField(service, "oppijanTunnistusUrl", oppijantunnistusUrl);
    }

    private ApplicationSystem mockApplicationSystem(String oid, String kohdejoukkoUri) {
        final ApplicationSystem mockedAs = mock(ApplicationSystem.class);
        when(mockedAs.getKohdejoukkoUri()).thenReturn(kohdejoukkoUri);
        when(mockedAs.getName()).thenReturn(new I18nText(ImmutableMap.of(
                "fi", hakuNimiFi,
                "sv", "ansökan 1",
                "en", "application 1"
        )));
        when(mockedAs.getApplicationPeriods()).thenReturn(ImmutableList.of(
                new ApplicationPeriod(new Date(0), new Date(Long.MAX_VALUE))
        ));
        when(mockedAs.isHigherEducation()).thenReturn(OppijaConstants.KOHDEJOUKKO_KORKEAKOULU.equals(kohdejoukkoUri));
        when(applicationSystemService.getApplicationSystem(oid)).thenReturn(mockedAs);
        return mockedAs;
    }

    private Application baseApplication(final String applicationSystemId) {
        return new Application() {{
            setOid(applicationOid);
            setApplicationSystemId(applicationSystemId);
            setReceived(receivedDate);
            setUpdated(modifiedDate);
            getAnswers().put("henkilotiedot", ImmutableMap.of(
                OppijaConstants.ELEMENT_ID_EMAIL, emailAddress,
                OppijaConstants.ELEMENT_ID_HUOLTAJANSAHKOPOSTI, emailAddressGuardian
            ));
            getAnswers().put("lisatiedot", ImmutableMap.of(
                    OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE, "suomi"
            ));
        }};
    }

    private Application testApplication() {
        return baseApplication(applicationSystemId);
    }

    private Application testApplicationSecondary() {
        return baseApplication(applicationSystemIdSecondary);
    }

    private Application testApplicationErkka() {
        return baseApplication(applicationSystemIdErkka);
    }

    private Application testApplicationJatkuva() {
        return baseApplication(applicationSystemIdJatkuva);
    }

    private Application testApplicationSiirtohaku() {
        return baseApplication(applicationSystemIdSiirtohaku);
    }

}
