package fi.vm.sade.haku.virkailija.lomakkeenhallinta.ohjausparametrit;

import fi.vm.sade.haku.oppija.common.jackson.UnknownPropertiesAllowingJacksonJsonClientFactory;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.ohjausparametrit.domain.Ohjausparametrit;
import fi.vm.sade.properties.OphProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

@Service
public class OhjausparametritService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OhjausparametritService.class);
    public static final String MEDIA_TYPE = MediaType.APPLICATION_JSON + ";charset=UTF-8";
    private final Client clientWithJacksonSerializer;
    private final OphProperties urlConfiguration;

    @Autowired
    public OhjausparametritService(OphProperties urlConfiguration) {
        this.urlConfiguration = urlConfiguration;
        clientWithJacksonSerializer = UnknownPropertiesAllowingJacksonJsonClientFactory.create();
    }

    public Ohjausparametrit fetchOhjausparametritForHaku(String oid) {
        return clientWithJacksonSerializer.target(urlConfiguration.url("ohjausparametrit-service.resource.url", oid))
            .request()
            .accept(MEDIA_TYPE).get(new GenericType<Ohjausparametrit>() {});
    }
}
