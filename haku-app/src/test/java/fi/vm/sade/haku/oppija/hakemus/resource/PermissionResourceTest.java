package fi.vm.sade.haku.oppija.hakemus.resource;

import fi.vm.sade.authentication.permissionchecker.PermissionCheckRequestDTO;
import fi.vm.sade.authentication.permissionchecker.PermissionCheckResponseDTO;
import fi.vm.sade.haku.oppija.hakemus.it.IntegrationTestSupport;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@ActiveProfiles("it")
public class PermissionResourceTest extends IntegrationTestSupport {

    public static final String NULL_REQUEST = "Null request.";
    public static final String BLANK_OID = "Blank person oid.";
    public static final String BLANK_ORGANISATION_OID = "Blank organisation oid in oid list.";
    public static final String EMPTY_LIST = "Organisation oid list empty.";
    public static final String NULL_LIST = "Null organisation oid list.";
    public static final String NO_RESULTS = "No organisation found.";

    PermissionResource permissionResource = appContext.getBean(PermissionResource.class);;


    /*
     * Testataan että virkailija, joka kuuluu organisaatioon, voi nähdä vain niiden henkilöiden tietoja, jotka ovat
     * hakeneet hänen organisaatioonsa tai johonkin sen lapsiorganisaatioon.
     */

    @Test
    public void personMatchesOrganisation() {
        String studentOid = "1.2.246.562.24.14229104472";
        String organisationOid = "1.2.246.562.10.21989237215";
        validate(permissionResource.checkPermission(getRequest(studentOid, organisationOid)), true, null);
    }

    @Test
    public void personDoesNotMatchChildOrganisation() {
        String studentOid = "1.2.246.562.24.14229104472";
        String[] organisationOids = {"childOrganisationOid", "secondChildOrganisationOid"};
        validate(permissionResource.checkPermission(getRequest(studentOid, organisationOids)), false, NO_RESULTS);
    }

    @Test
    public void personMatchesParentOrganisation() {
        String studentOid = "1.2.246.562.24.14229104472";
        String organisationOid = "1.2.246.562.10.85149969462";
        String[] organisationOids = {organisationOid, "1.2.246.562.10.27756776996", "1.2.246.562.10.21989237215"};
        validate(permissionResource.checkPermission(getRequest(studentOid, organisationOids)), true, null);
    }

    @Test
    public void handlesInvalidInput() {
        validate(permissionResource.checkPermission(null), false, NULL_REQUEST);
        validate(permissionResource.checkPermission(getRequest(null, "organisationoid")), false, BLANK_OID);
        validate(permissionResource.checkPermission(getRequest("", "organisationoid")), false, BLANK_OID);
        validate(permissionResource.checkPermission(getRequest("personoid", "")), false, BLANK_ORGANISATION_OID);
        validate(permissionResource.checkPermission(getRequest("personoid")), false, EMPTY_LIST);
        PermissionCheckRequestDTO dto = getRequest("personoid");
        dto.setOrganisationOids(null);
        validate(permissionResource.checkPermission(dto), false, NULL_LIST);
        dto.setOrganisationOids(new ArrayList<String>());
        dto.getOrganisationOids().add(null);
        validate(permissionResource.checkPermission(dto), false, BLANK_ORGANISATION_OID);
    }

    private void validate(PermissionCheckResponseDTO r, boolean b, String s) {
        assertEquals("AccessAllowed was no correct.", b, r.isAccessAllowed());
        assertEquals("Wrong error message.", s, r.getErrorMessage());
    }

    private PermissionCheckRequestDTO getRequest(String personoid, String... oids) {
        PermissionCheckRequestDTO d = new PermissionCheckRequestDTO();
        d.setPersonOid(personoid);
        d.setOrganisationOids(Arrays.asList(oids));
        return d;
    }
}
