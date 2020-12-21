package fi.vm.sade.haku.oppija.hakemus.service;

import fi.vm.sade.haku.http.RestClient;
import fi.vm.sade.properties.OphProperties;
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
    OphProperties urlConfiguration;

    @Bean(name = "hakumaksuService")
    public HakumaksuService hakumaksuService(@Value("${haku.app.username.to.valintarekisteri}") String clientAppUser,
                                             @Value("${haku.app.password.to.valintarekisteri}") String clientAppPass) {
        return new HakumaksuService(
                urlConfiguration,
                restClient,
                clientAppUser,
                clientAppPass
        );
    }

}
