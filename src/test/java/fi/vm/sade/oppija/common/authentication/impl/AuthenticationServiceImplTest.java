package fi.vm.sade.oppija.common.authentication.impl;

import fi.vm.sade.authentication.service.UserManagementService;
import fi.vm.sade.authentication.service.types.AddHenkiloDataType;
import fi.vm.sade.authentication.service.types.dto.HenkiloType;
import fi.vm.sade.oppija.common.authentication.Person;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Hannu Lyytikainen
 */
public class AuthenticationServiceImplTest {

    AuthenticationServiceImpl authenticationService;

    public static final String OID = "1.2.246.562.24.50700775342";

    @Before
    public void setup() {
        HenkiloType ht = new HenkiloType();
        ht.setOidHenkilo(OID);
        UserManagementService userManagementService = mock(UserManagementService.class);
        when(userManagementService.addHenkilo(any(AddHenkiloDataType.class))).thenReturn(ht);
        this.authenticationService = new AuthenticationServiceImpl(userManagementService);
    }

    @Test
    public void testAddUser() {
        Person p = new Person("Onni Pekka", "Onni", "Oppija", "111166-987F",
                false, "mm@mail.com", "MALE", "Helsinki", false, "fi", "FINLAND", "fi");

        String oid = authenticationService.addPerson(p);

        assertEquals(oid, OID);

    }


}
