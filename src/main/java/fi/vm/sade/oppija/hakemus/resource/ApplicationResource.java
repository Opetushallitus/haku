package fi.vm.sade.oppija.hakemus.resource;

import fi.vm.sade.oppija.hakemus.domain.Application;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@Component
@Path("/applications")
public class ApplicationResource {

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Application> getAllApplications() {
        // TODO: actually retrieve applications

        List<Application> apps = new ArrayList<Application>();
        return apps;


    }

    @GET
    @Path("/{applicationId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Application getApplication(@PathParam("applicationId") String applicationId) {

        Application application = new Application();
        application.setOid("1.2.3.4.5");


        return application;

    }

}
