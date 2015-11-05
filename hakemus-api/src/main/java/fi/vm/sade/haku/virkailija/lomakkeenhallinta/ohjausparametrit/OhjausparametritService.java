package fi.vm.sade.haku.virkailija.lomakkeenhallinta.ohjausparametrit;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import fi.vm.sade.haku.oppija.common.jackson.UnknownPropertiesAllowingJacksonJsonClientFactory;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.ohjausparametrit.domain.Ohjausparametrit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MediaType;

@Service
public class OhjausparametritService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OhjausparametritService.class);
    public static final String MEDIA_TYPE = MediaType.APPLICATION_JSON + ";charset=UTF-8";
    private final WebResource webResource;

    @Autowired
    public OhjausparametritService(@Value("${ohjausparametrit.resource.url:''}") String ohjausparametritResourceUrl) {
        Client clientWithJacksonSerializer = UnknownPropertiesAllowingJacksonJsonClientFactory.create();
        webResource = clientWithJacksonSerializer.resource(ohjausparametritResourceUrl);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Ohjausparametrit uri: " + webResource.getURI().toString());
        }
    }

    public Ohjausparametrit fetchOhjausparametritForHaku(String oid) {
        return webResource.path(oid).accept(MEDIA_TYPE).get(new GenericType<Ohjausparametrit>() {});
    }
}
