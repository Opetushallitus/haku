package fi.vm.sade.haku.oppija.hakemus.resource;

import com.google.common.collect.Lists;
import fi.vm.sade.authentication.permissionchecker.PermissionCheckRequestDTO;
import fi.vm.sade.authentication.permissionchecker.PermissionCheckResponseDTO;
import fi.vm.sade.haku.oppija.hakemus.it.IntegrationTestSupport;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@ActiveProfiles("it")
public class PermissionResourceTest extends IntegrationTestSupport {

    public static final String NULL_REQUEST = "Null request.";
    public static final String NULL_PERSON_OID_LIST = "Null person oid list.";
    public static final String NULL_ORGANISATION_OID_LIST = "Null organisation oid list.";
    public static final String PERSON_LIST_EMPTY = "Person oid list empty.";
    public static final String ORGANISATION_LIST_EMPTY = "Organisation oid list empty.";
    public static final String BLANK_PERSON_OID = "Blank person oid in oid list.";
    public static final String BLANK_ORGANISATION_OID = "Blank organisation oid in organisation oid list.";
    public static final String NO_ORGANISATION_FOUND = "No organisation found.";

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
    public void personWithInvalidAndValidOidMatchesOrganisation() {
        String studentOid = "InvalidOid";
        String organisationOid = "1.2.246.562.10.21989237215";
        PermissionCheckRequestDTO request = getRequest(studentOid, organisationOid);
        request.getPersonOidsForSamePerson().add("1.2.246.562.24.14229104472");
        validate(permissionResource.checkPermission(request), true, null);
    }

    @Test
    public void personDoesNotMatchChildOrganisation() {
        String studentOid = "1.2.246.562.24.14229104472";
        String[] organisationOids = {"childOrganisationOid", "secondChildOrganisationOid"};
        validate(permissionResource.checkPermission(getRequest(studentOid, organisationOids)), false, NO_ORGANISATION_FOUND);
    }

    @Test
    public void personMatchesParentOrganisation() {
        String studentOid = "1.2.246.562.24.14229104472";
        String organisationOid = "1.2.246.562.10.85149969462";
        String[] organisationOids = {organisationOid, "1.2.246.562.10.27756776996", "1.2.246.562.10.21989237215"};
        validate(permissionResource.checkPermission(getRequest(studentOid, organisationOids)), true, null);
    }

    @Test
    public void personDoesMatchesToPassiveApplication() {
        String studentOid = "1.2.246.562.24.40135708059";
        String[] organisationOids = {"1.2.246.562.10.15884705888"};
        validate(permissionResource.checkPermission(getRequest(studentOid, organisationOids)), true, null);
    }


    @Test
    public void handlesInvalidInput() {
        validate(permissionResource.checkPermission(null), false, NULL_REQUEST);
        validate(permissionResource.checkPermission(getRequest("", "organisationoid")), false, BLANK_PERSON_OID);
        validate(permissionResource.checkPermission(getRequest("personoid", "")), false, BLANK_ORGANISATION_OID);
        validate(permissionResource.checkPermission(getRequest("personoid")), false, ORGANISATION_LIST_EMPTY);

        PermissionCheckRequestDTO dto = getRequest("personoid", "organisationoid");
        dto.setOrganisationOids(null);
        validate(permissionResource.checkPermission(dto), false, NULL_ORGANISATION_OID_LIST);

        dto = getRequest("personoid", "organisationoid");
        dto.setPersonOidsForSamePerson(Lists.<String>newArrayList());
        validate(permissionResource.checkPermission(dto), false, PERSON_LIST_EMPTY);

        dto = getRequest("personoid", "organisationoid");
        dto.setPersonOidsForSamePerson(null);
        validate(permissionResource.checkPermission(dto), false, NULL_PERSON_OID_LIST);

        dto = getRequest("personoid", "organisationoid");
        dto.setOrganisationOids(new ArrayList<String>());
        dto.getOrganisationOids().add(null);
        validate(permissionResource.checkPermission(dto), false, BLANK_ORGANISATION_OID);

        dto = getRequest("personoid", "organisationoid");
        dto.setPersonOidsForSamePerson(new ArrayList<String>());
        dto.getPersonOidsForSamePerson().add(null);
        validate(permissionResource.checkPermission(dto), false, BLANK_PERSON_OID);
    }

    private void validate(PermissionCheckResponseDTO r, boolean b, String s) {
        assertEquals("AccessAllowed was no correct.", b, r.isAccessAllowed());
        assertEquals("Wrong error message.", s, r.getErrorMessage());
    }

    private PermissionCheckRequestDTO getRequest(String personoid, String... oids) {
        PermissionCheckRequestDTO d = new PermissionCheckRequestDTO();
        d.setPersonOidsForSamePerson(Lists.newArrayList(personoid));
        d.setOrganisationOids(Lists.newArrayList(oids));
        return d;
    }
}
