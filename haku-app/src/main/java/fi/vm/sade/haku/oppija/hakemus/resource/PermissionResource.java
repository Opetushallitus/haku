package fi.vm.sade.haku.oppija.hakemus.resource;

import com.google.common.base.Preconditions;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import fi.vm.sade.authentication.permissionchecker.PermissionCheckInterface;
import fi.vm.sade.authentication.permissionchecker.PermissionCheckRequestDTO;
import fi.vm.sade.authentication.permissionchecker.PermissionCheckResponseDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.oppija.hakemus.service.HakuPermissionService;
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
    public static final String NULL_REQUEST = "Null request.";
    public static final String NULL_PERSON_OID_LIST = "Null person oid list.";
    public static final String NULL_ORGANISATION_OID_LIST = "Null organisation oid list.";
    public static final String PERSON_LIST_EMPTY = "Person oid list empty.";
    public static final String ORGANISATION_LIST_EMPTY = "Organisation oid list empty.";
    public static final String BLANK_PERSON_OID = "Blank person oid in oid list.";
    public static final String BLANK_ORGANISATION_OID = "Blank organisation oid in organisation oid list.";
    public static final String NO_ORGANISATION_FOUND = "No organisation found.";

    @Autowired
    private ApplicationDAO applicationDao;

    @Autowired
    private HakuPermissionService hakuPermissionService;

    @POST
    @Path("/checkpermission")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @ApiOperation(
            value = "Tarkistaa onko hakija hakenut johonkin listatuista organisaatioista.",
            notes = "Vain virkailijat hakemuksen hakukohteiden organisaatioista saavat katsella hakijan tietoja.\n" +
                    "personOids: Hakijan henkilöoidit\n" +
                    "organisationOids: Virkailijan organisaatiot ja niiden lapsiorganisaatiot",
            response = PermissionCheckResponseDTO.class
    )
    @Override
    public PermissionCheckResponseDTO checkPermission(PermissionCheckRequestDTO request) {
        try {
            Preconditions.checkNotNull(request, NULL_REQUEST);
            Preconditions.checkNotNull(request.getPersonOidsForSamePerson(), NULL_PERSON_OID_LIST);
            Preconditions.checkNotNull(request.getOrganisationOids(), NULL_ORGANISATION_OID_LIST);
            Preconditions.checkArgument(!request.getPersonOidsForSamePerson().isEmpty(), PERSON_LIST_EMPTY);
            Preconditions.checkArgument(!request.getOrganisationOids().isEmpty(), ORGANISATION_LIST_EMPTY);
            for (String oid : request.getPersonOidsForSamePerson()) {
                Preconditions.checkArgument(!StringUtils.isBlank(oid), BLANK_PERSON_OID);
            }
            for (String org : request.getOrganisationOids()) {
                Preconditions.checkArgument(!StringUtils.isBlank(org), BLANK_ORGANISATION_OID);
            }

            List<Application> result = applicationDao.getApplicationsByPersonOid(request.getPersonOidsForSamePerson());
            for (Application hakemus : result) {
                if(hakuPermissionService.userCanDeleteApplication(hakemus))
                    return permissionAllowed();
                Map<String, String> answers = hakemus.getAnswers().get("hakutoiveet");
                for (String hakutoive : answers.keySet()) {
                    if (hakutoive.contains("-Opetuspiste-id")
                            && request.getOrganisationOids().contains(answers.get(hakutoive)))
                        return permissionAllowed();
                }
            }

            return permissionDenied(NO_ORGANISATION_FOUND);
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
