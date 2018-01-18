package fi.vm.sade.haku.oppija.hakemus.resource;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchDTO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParameters;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParametersBuilder;
import fi.vm.sade.haku.oppija.hakemus.it.dao.impl.CloseableIterator;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.haku.oppija.hakemus.resource.ApplicationResource.CHARSET_UTF_8;

@Component
@Path("/streaming/applications")
public class StreamingApplicationResource {

    private final ApplicationService applicationService;

    @Autowired
    public StreamingApplicationResource(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @POST
    @Path("/listfull")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_CRUD', 'ROLE_APP_HAKEMUS_OPO')")
    public Response findFullApplicationsPostStreaming(final ApplicationSearchDTO applicationSearchDTO) {
        List<String> state = applicationSearchDTO.states;
        List<String> asIds = applicationSearchDTO.asIds;
        List<String> aoOid = applicationSearchDTO.aoOids;

        ApplicationQueryParameters queryParams = new ApplicationQueryParametersBuilder()
                .setSearchTerms("")
                .setStates(state)
                .setAsIds(asIds)
                .addAoOid(aoOid.toArray(new String[0]))
                .build();

        StreamingOutput stream = os -> {
            JsonGenerator json = new JsonFactory().setCodec(new ObjectMapper()).createJsonGenerator(os);
            json.writeStartArray();
            try(CloseableIterator<Map<String, Object>> apps = applicationService.findFullApplicationsStreaming(queryParams)) {
                while(apps.hasNext()){
                    Map<String, Object> next = apps.next();
                    json.writeObject(next);
                    json.flush();
                }
            } catch (Exception e) {
                json.writeObject(ImmutableMap.of("error", e.getMessage()));
            } finally {
                json.writeEndArray();
                json.flush();
            }
        };
        return Response.ok(stream).build();
    }
}
