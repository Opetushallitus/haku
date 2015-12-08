package fi.vm.sade.haku.oppija.koodisto;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.nio.charset.Charset;
import java.util.NoSuchElementException;

import static org.slf4j.LoggerFactory.*;

@Component
@Path("/koodisto")
@Profile(value = {"dev", "it"})
public class KoodistoResourceMock {
    private final Logger logger = getLogger(KoodistoResourceMock.class);

    @GET
    @Path("/rest/codeelement/{koodiUri}/1")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response getKoodi(@PathParam("koodiUri") final String koodiUri) throws IOException {
        try {
            return Response.ok(koodi(koodiUri)).build();
        } catch (NoSuchElementException e) {
            logger.warn(e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    private String koodi(String koodiUri) throws IOException {
        String path = "/mockdata/koodisto/" + koodiUri + ".json";
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            throw new NoSuchElementException("path " + path + " not found");
        }
        return IOUtils.toString(is, Charset.forName("UTF-8"));
    }

}
