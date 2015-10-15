package fi.vm.sade.haku.oppija.hakemus.resource;

import fi.vm.sade.authentication.permissionchecker.PermissionCheckRequestDTO;
import fi.vm.sade.authentication.permissionchecker.PermissionCheckResponseDTO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class PermissionResourceTest {

    public static final String NULL_REQUEST = "Null request.";
    public static final String BLANK_OID = "Blank person oid.";
    public static final String BLANK_ORGANISATION_OID = "Blank organisation oid in oid list.";
    public static final String EMPTY_LIST = "Organisation oid list empty.";
    public static final String NULL_LIST = "Null organisation oid list.";

    @Mock
    private ApplicationDAO applicationDao;

    @InjectMocks
    private PermissionResource permissionResource = new PermissionResource();

    public PermissionResourceTest() {
    }

    @Before()
    public void init() {

    }

    @Test
    public void personMatchesOrganisation() {
    }

    @Test
    public void personMatchesParentOrganisation() {
    }

    @Test
    public void personDoesNotMatchChildOrganisation() {
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
