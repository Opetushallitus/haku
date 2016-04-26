package fi.vm.sade.haku.properties;

import fi.vm.sade.properties.OphProperties;
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
    OphProperties urlConfiguration;

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public String frontProperties() {
        return "window.urls.override=" + urlConfiguration.frontPropertiesToJson();
    }
}
