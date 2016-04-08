package fi.vm.sade.haku.oppija.configuration;

import fi.vm.sade.properties.OphProperties;
import fi.vm.sade.properties.PropertyResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class UrlConfiguration extends OphProperties {

    @Value("${host.virkailija}")
    private String virkailija;

    @Value("${host.cas}")
    private String cas;

    @Value("${host.haku}")
    private String haku;

    @Value("${host.haku.sv}")
    private String hakuSv;

    @Value("${host.haku.en}")
    private String hakuEn;

    public UrlConfiguration() {
        addFiles("/hakemus-api-oph.properties");
    }

    @PostConstruct
    public void postConstruct() {
        if(virkailija != null) {
            addDefault("host.virkailija", virkailija);
        }

        if(cas != null) {
            addDefault("host.cas", cas);
        }

        if(haku != null) {
            addDefault("host.haku", haku);
        }

        if(hakuSv != null) {
            addDefault("host.haku.sv", hakuSv);
        }

        if(hakuEn != null) {
            addDefault("host.haku.en", hakuEn);
        }
    }
}
