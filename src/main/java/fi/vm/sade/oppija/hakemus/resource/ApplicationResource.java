package fi.vm.sade.oppija.hakemus.resource;

import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
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

    private ApplicationService applicationService;

    @Autowired
    public ApplicationResource(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Application> getAllApplications(@QueryParam("asid") String asId) {

        List<Application> applications;
        if (asId != null) {
            // retrieve applications related to a single application system
            applications = applicationService.getApplicationsByApplicationSystem(asId);
        }
        else {
            applications = new ArrayList<Application>();
        }

        return applications;
    }

    @GET
    @Path("/{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Application getApplication(@PathParam("oid") String oid) {
        return applicationService.getApplication(oid);
    }

}
