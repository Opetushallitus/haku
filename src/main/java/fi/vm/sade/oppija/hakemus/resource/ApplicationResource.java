package fi.vm.sade.oppija.hakemus.resource;

import com.sun.jersey.api.NotFoundException;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * API for accessing applications.
 *
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
    public List<Application> getApplications(@QueryParam("asid") String asId) {

        List<Application> applications;
        if (asId != null) {
            // retrieve applications related to a single application system
            return applications = applicationService.getApplicationsByApplicationSystem(asId);
        }
        else {
            throw new JSONException(Response.Status.BAD_REQUEST, "Invalid application system id argument");
        }
    }

    @GET
    @Path("/{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Application getApplication(@PathParam("oid") String oid) {

        try {
            return applicationService.getApplication(oid);
        } catch (ResourceNotFoundException e) {
            throw new JSONException(Response.Status.NOT_FOUND, "Could not find requested application");

        }

    }

}
