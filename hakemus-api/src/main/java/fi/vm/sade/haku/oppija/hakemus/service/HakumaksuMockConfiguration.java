package fi.vm.sade.haku.oppija.hakemus.service;

import fi.vm.sade.haku.http.MockedRestClient;
import fi.vm.sade.haku.http.RestClient;
import fi.vm.sade.haku.oppija.hakemus.service.impl.SendMailService;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static fi.vm.sade.haku.testfixtures.HakumaksuMockData.testMappings;

@Configuration
@Profile({"it", "dev"})
public class HakumaksuMockConfiguration {

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

    @Autowired
    ApplicationSystemService applicationSystemService;

    MockedRestClient restClient = new MockedRestClient(testMappings());

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
        return new SendMailService(applicationSystemService, restClient);
    }

}
