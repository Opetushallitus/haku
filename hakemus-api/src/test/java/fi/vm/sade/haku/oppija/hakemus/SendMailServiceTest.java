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
import org.apache.commons.mail.EmailException;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static fi.vm.sade.haku.oppija.common.oppijantunnistus.OppijanTunnistusDTO.LanguageCodeISO6391.fi;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class SendMailServiceTest {

    static final MockedRestClient REST_CLIENT = new MockedRestClient();

    static final String OPPIJANTUNNISTUS_URL = "http://localhost/oppijan-tunnistus";
    static final String LINK_FI = "http://localhost/fi/omatsivut";
    static final String LINK_SV = "http://localhost/sv/omatsivut";
    static final String LINK_EN = "http://localhost/en/omatsivut";
    static final String APPLICATION_SYSTEM_ID = "1.2.246.562.29.75203638285";
    static final String APPLICATION_OID = "1.2.246.562.11.1";
    static final String EMAIL_ADDRESS = "testi@example.com";
    static final String HAKU_NIMI_FI = "haku 1";

    ApplicationSystemService applicationSystemService;

    SendMailService service;

    @Before
    public void setupServices() {
        final ApplicationSystem applicationSystem = mock(ApplicationSystem.class);
        when(applicationSystem.getKohdejoukkoUri()).thenReturn(OppijaConstants.KOHDEJOUKKO_KORKEAKOULU);
        when(applicationSystem.getName()).thenReturn(new I18nText(ImmutableMap.of(
                "fi", HAKU_NIMI_FI,
                "sv", "ansökan 1",
                "en", "application 1"
        )));
        when(applicationSystem.getApplicationPeriods()).thenReturn(ImmutableList.of(
                new ApplicationPeriod(new Date(0), new Date(Long.MAX_VALUE))
        ));

        applicationSystemService = mock(ApplicationSystemService.class);
        when(applicationSystemService.getApplicationSystem(APPLICATION_SYSTEM_ID)).thenReturn(applicationSystem);

        service = new SendMailService(applicationSystemService, REST_CLIENT, LINK_FI, LINK_SV, LINK_EN);
        setField(service, "oppijanTunnistusUrl", OPPIJANTUNNISTUS_URL);
    }

    @Test
    public void testSendReceivedEmail() throws EmailException {
        Application application = testApplication();

        service.sendReceivedEmail(application);

        Captured firstCaptured = REST_CLIENT.getCaptured().iterator().next();
        OppijanTunnistusDTO capturedBody = (OppijanTunnistusDTO) firstCaptured.body;

        assertEquals(firstCaptured.url, OPPIJANTUNNISTUS_URL);
        assertEquals(firstCaptured.method, "POST");
        assertEquals(capturedBody.url, LINK_FI);
        assertEquals(capturedBody.lang, fi);
        assertEquals(capturedBody.email, EMAIL_ADDRESS);
        assertEquals(capturedBody.metadata.hakemusOid, APPLICATION_OID);
        assertTrue(capturedBody.template.contains(HAKU_NIMI_FI));
    }

    private static Application testApplication() {
        return new Application() {{
            setOid(APPLICATION_OID);
            setApplicationSystemId(APPLICATION_SYSTEM_ID);
            getAnswers().put("henkilotiedot", ImmutableMap.of(
                    OppijaConstants.ELEMENT_ID_EMAIL, EMAIL_ADDRESS
            ));
            getAnswers().put("lisatiedot", ImmutableMap.of(
                    OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE, "suomi"
            ));
            setReceived(new Date());
        }};
    }

}
