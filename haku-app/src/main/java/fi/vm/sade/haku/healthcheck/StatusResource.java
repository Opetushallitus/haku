package fi.vm.sade.haku.healthcheck;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

@Component
@Path("/status")
@PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_CRUD')")
public class StatusResource {

    public static final String CHARSET_UTF_8 = ";charset=UTF-8";

    private StatusRepository statusRepository;

    public StatusResource() {
        // NOP
    }

    @Autowired
    public StatusResource(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public List<Map<String, String>> getStatus() {
        return statusRepository.read();
    }
}
