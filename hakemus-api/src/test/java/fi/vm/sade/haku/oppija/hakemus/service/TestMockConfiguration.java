package fi.vm.sade.haku.oppija.hakemus.service;

import fi.vm.sade.haku.http.MockedRestClient;
import fi.vm.sade.haku.oppija.hakemus.service.impl.SendMailService;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.virkailija.viestintapalvelu.EmailService;
import fi.vm.sade.haku.virkailija.viestintapalvelu.impl.EmailServiceMockImpl;
import fi.vm.sade.properties.OphProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;

import static fi.vm.sade.haku.testfixtures.HakumaksuMockData.testMappings;

@Configuration
@Profile({"it", "dev"})
public class TestMockConfiguration {

    @Autowired
    ApplicationSystemService applicationSystemService;

    @Autowired
    OphProperties urlConfiguration;

    MockedRestClient restClient = new MockedRestClient(testMappings());

    EmailService emailService = new EmailServiceMockImpl();

    @PostConstruct
    private void setUrls() {
        urlConfiguration.addDefault("host.virkailija", "localhost:9090").addDefault("host.haku","localhost:9090");
    }

    @Bean(name = "hakumaksuService")
    public HakumaksuService hakumaksuService() {
        return new HakumaksuService(
                urlConfiguration,
                restClient
        );
    }

    @Bean(name = "sendMailService")
    public SendMailService sendMailService() {
        return new SendMailService(applicationSystemService, restClient, emailService, urlConfiguration);
    }

}
