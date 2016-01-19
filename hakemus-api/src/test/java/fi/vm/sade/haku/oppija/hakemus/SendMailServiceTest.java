package fi.vm.sade.haku.oppija.hakemus;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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
import fi.vm.sade.haku.virkailija.viestintapalvelu.EmailService;
import fi.vm.sade.haku.virkailija.viestintapalvelu.impl.EmailServiceMockImpl;
import org.apache.commons.mail.EmailException;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.Iterator;

import static fi.vm.sade.haku.oppija.common.oppijantunnistus.OppijanTunnistusDTO.LanguageCodeISO6391.fi;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.MailTemplateUtil.dateTimeFormatter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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


    MockedRestClient restClient = new MockedRestClient();
    EmailService emailService = new EmailServiceMockImpl();
    ApplicationSystemService applicationSystemService;
    SendMailService service;

    @Before
    public void setupServices() {
        final ApplicationSystem applicationSystem = mock(ApplicationSystem.class);
        when(applicationSystem.getKohdejoukkoUri()).thenReturn(OppijaConstants.KOHDEJOUKKO_KORKEAKOULU);
        when(applicationSystem.getName()).thenReturn(new I18nText(ImmutableMap.of(
                "fi", hakuNimiFi,
                "sv", "ansökan 1",
                "en", "application 1"
        )));
        when(applicationSystem.getApplicationPeriods()).thenReturn(ImmutableList.of(
                new ApplicationPeriod(new Date(0), new Date(Long.MAX_VALUE))
        ));

        final ApplicationSystem applicationSystemSecondary = mock(ApplicationSystem.class);
        when(applicationSystemSecondary.getKohdejoukkoUri()).thenReturn(OppijaConstants.KOHDEJOUKKO_AMMATILLINEN_JA_LUKIO);
        when(applicationSystemSecondary.getName()).thenReturn(new I18nText(ImmutableMap.of(
                "fi", hakuNimiFi,
                "sv", "ansökan 1",
                "en", "application 1"
        )));
        when(applicationSystemSecondary.getApplicationPeriods()).thenReturn(ImmutableList.of(
                new ApplicationPeriod(new Date(0), new Date(Long.MAX_VALUE))
        ));

        applicationSystemService = mock(ApplicationSystemService.class);
        when(applicationSystemService.getApplicationSystem(applicationSystemId)).thenReturn(applicationSystem);
        when(applicationSystemService.getApplicationSystem(applicationSystemIdSecondary)).thenReturn(applicationSystemSecondary);

        service = new SendMailService(applicationSystemService, restClient, emailService, linkFi, linkSv, linkEn);
        setField(service, "oppijanTunnistusUrl", oppijantunnistusUrl);
    }

    private Application testApplication() {
        return new Application() {{
            setOid(applicationOid);
            setApplicationSystemId(applicationSystemId);
            getAnswers().put("henkilotiedot", ImmutableMap.of(
                    OppijaConstants.ELEMENT_ID_EMAIL, emailAddress
            ));
            getAnswers().put("lisatiedot", ImmutableMap.of(
                    OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE, "suomi"
            ));
            setReceived(receivedDate);
            setUpdated(modifiedDate);
        }};
    }

    private Application testApplicationSecondary() {
        return new Application() {{
            setOid(applicationOid);
            setApplicationSystemId(applicationSystemIdSecondary);
            getAnswers().put("henkilotiedot", ImmutableMap.of(
                    OppijaConstants.ELEMENT_ID_EMAIL, emailAddress,
                    OppijaConstants.ELEMENT_ID_HUOLTAJANSAHKOPOSTI, emailAddressGuardian
            ));
            getAnswers().put("lisatiedot", ImmutableMap.of(
                    OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE, "suomi"
            ));
            setReceived(receivedDate);
            setUpdated(modifiedDate);
        }};
    }

}
