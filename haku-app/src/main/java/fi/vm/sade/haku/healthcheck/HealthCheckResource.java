package fi.vm.sade.haku.healthcheck;


import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/healthcheck")
@Component
public class HealthCheckResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Map<String, Object> checkHealthAndReturnStats(@Context HttpServletRequest httpServletRequest) {

        HashMap<String, Object> responseBody = new HashMap<String, Object>();
        try {
            responseBody.put("status", "OK");
            responseBody.put("contextPath", httpServletRequest.getContextPath());
            responseBody.put("checks", ImmutableMap.of());
            responseBody.put("info", ImmutableMap.of());
        } catch (Throwable t) {
            responseBody.put("status", "ERROR");
        }
        return responseBody;

    }


}
