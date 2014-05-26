package fi.vm.sade.haku.oppija.configuration;

import fi.vm.sade.haku.oppija.lomake.SeleniumContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("it")
public class ASInitializer {

    @Autowired
    public ASInitializer(final SeleniumContainer seleniumContainer, @Value("${webdriver.base.url:http://localhost:9090/haku-app/}") final String baseUrl) {
        seleniumContainer.getDriver().get((baseUrl + "lomakkeenhallinta"));
    }
}
