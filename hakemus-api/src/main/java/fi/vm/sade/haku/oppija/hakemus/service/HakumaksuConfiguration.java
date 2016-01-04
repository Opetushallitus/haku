package fi.vm.sade.haku.oppija.hakemus.service;

import fi.vm.sade.haku.http.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"default", "devluokka"})
public class HakumaksuConfiguration {

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
    RestClient restClient;

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

}
