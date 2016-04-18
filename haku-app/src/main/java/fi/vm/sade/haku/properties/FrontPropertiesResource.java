package fi.vm.sade.haku.properties;

import fi.vm.sade.haku.oppija.configuration.UrlConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/rest/frontProperties")
@Component
public class FrontPropertiesResource {

    @Autowired
    UrlConfiguration urlConfiguration;

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public String frontProperties() {
        return "window.urls.override=" + urlConfiguration.frontPropertiesToJson();
    }
}
