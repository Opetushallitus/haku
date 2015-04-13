package fi.vm.sade.haku;

import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.FormConfigurationDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.FormConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;


@Controller
@Path("/testResource")
@Profile(value = {"dev", "it"})
public class TestResource {
    public static final String CHARSET_UTF_8 = ";charset=UTF-8";

    @Autowired
    private FormConfigurationDAO formConfigurationDAO;

    @POST
    @Path("{asId}/formConfiguration")
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD')")
    public void saveFormConfiguration(@PathParam("asId") String applicationSystemId,
                                      FormConfiguration configuration) throws IOException {
        formConfigurationDAO.update(configuration);
    }
}
