package fi.vm.sade.haku.oppija.hakemus.resource;

import com.google.common.base.Preconditions;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import fi.vm.sade.authentication.permissionchecker.PermissionCheckInterface;
import fi.vm.sade.authentication.permissionchecker.PermissionCheckRequestDTO;
import fi.vm.sade.authentication.permissionchecker.PermissionCheckResponseDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

@Component
@Path("/permission")
@Api(value = "/permission", description = "Oikeuksien tarkistuksen REST-rajapinta")
public class PermissionResource implements PermissionCheckInterface {

    public static final String CHARSET_UTF_8 = ";charset=UTF-8";

    @Autowired
    private ApplicationDAO applicationDao;

    @POST
    @Path("/checkpermission")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @ApiOperation(
            value = "Tarkistaa onko hakija hakenut johonkin listatuista organisaatioista.",
            notes = "Vain virkailijat hakemuksen hakukohteiden organisaatioista saavat katsella hakijan tietoja.\n" +
                    "personOid: Hakijan numero\n" +
                    "organisationOids: Virkailijan organisaatiot ja niiden lapsiorganisaatiot",
            response = PermissionCheckResponseDTO.class
    )
    @Override
    public PermissionCheckResponseDTO checkPermission(PermissionCheckRequestDTO request) {
        try {
            Preconditions.checkNotNull(request, "Null request.");
            Preconditions.checkArgument(!StringUtils.isBlank(request.getPersonOid()), "Blank person oid.");
            Preconditions.checkNotNull(request.getOrganisationOids(), "Null organisation oid list.");
            Preconditions.checkArgument(!request.getOrganisationOids().isEmpty(), "Organisation oid list empty.");
            for (String org : request.getOrganisationOids()) {
                Preconditions.checkArgument(!StringUtils.isBlank(org), "Blank organisation oid in oid list.");
            }

            List<Application> result = applicationDao.getApplicationsByPersonOid(request.getPersonOid());
            for (Application hakemus : result) {
                Map<String, String> answers = hakemus.getAnswers().get("hakutoiveet");
                for (String hakutoive : answers.keySet()) {
                    if (hakutoive.contains("-Opetuspiste-id") && request.getOrganisationOids().contains(answers.get(hakutoive)))
                        return permissionAllowed();
                }
            }

            return permissionDenied("No organisation found.");
        } catch(Exception e){
            return permissionDenied(e.getMessage());
        }
    }

    private PermissionCheckResponseDTO permissionAllowed() {
        PermissionCheckResponseDTO result = new PermissionCheckResponseDTO();
        result.setAccessAllowed(true);
        return result;
    }

    private PermissionCheckResponseDTO permissionDenied(String reason) {
        PermissionCheckResponseDTO result = new PermissionCheckResponseDTO();
        result.setErrorMessage(reason);
        return result;
    }

}
