package fi.vm.sade.haku.oppija.configuration;

import fi.vm.sade.properties.OphProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

@Configuration
public class UrlConfiguration extends OphProperties {
    public UrlConfiguration() {
        addFiles("/haku-app-oph.properties");
        if(!"it".equals(System.getProperty("spring.profiles.active"))) {
            addOptionalFiles(Paths.get(System.getProperties().getProperty("user.home"), "/oph-configuration/common.properties").toString());
        }
    }

    @Override
    synchronized public String frontPropertiesToJson() {
        if(!frontProperties.containsKey("frontProperties.baseUrl")) {
            frontProperties.put("koulutusinformaatio.baseUrl", "//" + require("host.haku"));
        }
        return super.frontPropertiesToJson();
    }
}
