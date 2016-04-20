/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.haku.virkailija.authentication.impl;

import fi.vm.sade.haku.virkailija.authentication.Person;
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
        Person p = new Person("Onni Pekka", "Onni", "Oppija", "111166-987F", null,
                false, "mm@example.com", "MALE", "Helsinki", false, "fi", "FINLAND", "fi", "1.2.3.4", "1.2.3.4",
                "040123456", "Onnentie 2", "00100", "HELSINKI", "FIN");
        assertNotNull(authenticationServiceMock.addPerson(p));
    }

}
