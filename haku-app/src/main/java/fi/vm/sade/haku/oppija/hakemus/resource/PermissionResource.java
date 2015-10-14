package fi.vm.sade.haku.oppija.hakemus.resource;

import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import fi.vm.sade.authentication.permissionchecker.PermissionCheckInterface;
import fi.vm.sade.authentication.permissionchecker.PermissionCheckRequestDTO;
import fi.vm.sade.authentication.permissionchecker.PermissionCheckResponseDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParameters;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParametersBuilder;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Date;

@Component
@Path("/permission")
@Api(value = "/permission", description = "Oikeuksien tarkistuksen REST-rajapinta")
public class PermissionResource implements PermissionCheckInterface {

    public static final String CHARSET_UTF_8 = ";charset=UTF-8";

    @Autowired
    private ApplicationService applicationService;

    @POST
    @Path("/checkpermission")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @ApiOperation(
            value = "",
            response = PermissionCheckResponseDTO.class
    )
    @Override
    public PermissionCheckResponseDTO checkPermission(PermissionCheckRequestDTO request) {
        ApplicationQueryParameters applicationQueryParameters  = new ApplicationQueryParametersBuilder()
                //.setStart(0)
                //.setRows(Integer.MAX_VALUE)
                //.setOrderBy("fullName")
                //.setOrderDir(1)
                .setAsIds(new ArrayList<String>())
                .addPersonOid(request.getPersonOid())
                .build();

        ApplicationSearchResultDTO result = applicationService.findApplications(applicationQueryParameters);

        System.out.println(result);

        return null;
    }

}
