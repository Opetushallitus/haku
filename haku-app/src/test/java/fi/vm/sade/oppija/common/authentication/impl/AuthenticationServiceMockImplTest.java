package fi.vm.sade.oppija.common.authentication.impl;

import fi.vm.sade.oppija.common.authentication.Person;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author Hannu Lyytikainen
 */
public class AuthenticationServiceMockImplTest {

    AuthenticationServiceMockImpl authenticationServiceMock;

    @Before
    public void setup() {
        this.authenticationServiceMock = new AuthenticationServiceMockImpl();
    }

    @Test
    public void testAddUser() {
        Person p = new Person("Onni Pekka", "Onni", "Oppija", "111166-987F",
                false, "mm@mail.com", "MALE", "Helsinki", false, "fi", "FINLAND", "fi");
        assertNotNull(authenticationServiceMock.addPerson(p));
    }

}
