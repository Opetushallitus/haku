package fi.vm.sade.oppija.hakemus.resource;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Used to return different HTTP codes with JSON payload.
 *
 * @author Hannu Lyytikainen
 */
public class JSONException extends WebApplicationException {
    public JSONException(Response.Status status, String message) {
      super(Response.status(status).entity(new ErrorMessage(message)).type(MediaType.APPLICATION_JSON).build());
    }
}
