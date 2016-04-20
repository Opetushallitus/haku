package fi.vm.sade.haku.oppija.hakemus.service;

import fi.vm.sade.haku.http.RestClient;
import fi.vm.sade.haku.oppija.configuration.UrlConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"default", "devluokka"})
public class HakumaksuConfiguration {

    @Autowired
    RestClient restClient;

    @Autowired
    UrlConfiguration urlConfiguration;

    @Bean(name = "hakumaksuService")
    public HakumaksuService hakumaksuService() {
        return new HakumaksuService(
                urlConfiguration,
                restClient
        );
    }

}
