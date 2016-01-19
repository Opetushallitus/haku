package fi.vm.sade.haku.oppija.hakemus.service;

import fi.vm.sade.haku.http.MockedRestClient;
import fi.vm.sade.haku.http.RestClient;
import fi.vm.sade.haku.oppija.hakemus.service.impl.SendMailService;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.virkailija.viestintapalvelu.EmailService;
import fi.vm.sade.haku.virkailija.viestintapalvelu.impl.EmailServiceMockImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static fi.vm.sade.haku.testfixtures.HakumaksuMockData.testMappings;

@Configuration
@Profile({"it", "dev"})
public class TestMockConfiguration {

    @Value("${cas.service.koodisto-service}")
    String koodistoServiceUrl;

    @Value("${koulutusinformaatio.ao.resource.url}")
    String koulutusinformaatioUrl;

    @Value("${oppijantunnistus.create.url}")
    String oppijanTunnistusUrl;

    @Value("${hakuperusteet.url.fi}")
    String hakuperusteetUrlFi;

    @Value("${hakuperusteet.url.sv}")
    String hakuperusteetUrlSv;

    @Value("${hakuperusteet.url.en}")
    String hakuperusteetUrlEn;

    @Value("${email.application.modify.link.fi}")
    String emailApplicationModifyLinkFi;

    @Value("${email.application.modify.link.sv}")
    String emailApplicationModifyLinkSv;

    @Value("${email.application.modify.link.en}")
    String emailApplicationModifyLinkEn;

    @Autowired
    ApplicationSystemService applicationSystemService;

    MockedRestClient restClient = new MockedRestClient(testMappings());

    EmailService emailService = new EmailServiceMockImpl();

    @Bean(name = "hakumaksuService")
    public HakumaksuService hakumaksuService() {
        return new HakumaksuService(
                koodistoServiceUrl,
                koulutusinformaatioUrl,
                oppijanTunnistusUrl,
                hakuperusteetUrlFi,
                hakuperusteetUrlSv,
                hakuperusteetUrlEn,
                restClient
        );
    }

    @Bean(name = "sendMailService")
    public SendMailService sendMailService() {
        return new SendMailService(applicationSystemService, restClient, emailService, emailApplicationModifyLinkFi, emailApplicationModifyLinkSv, emailApplicationModifyLinkEn);
    }

}
