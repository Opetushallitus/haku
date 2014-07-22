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

package fi.vm.sade.haku.oppija.lomake.domain;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {

    public static final String USERNAME = "test";
    private User user;

    @Before
    public void setUp() throws Exception {
        this.user = new User(USERNAME);
    }

    @Test
    public void testGetUserName() throws Exception {
        assertEquals(USERNAME, user.getUserName());
    }

    @Test
    public void testIsKnown() throws Exception {
        assertTrue(this.user.isKnown());
    }

    @Test
    public void testEqualsSame() throws Exception {
        this.user.equals(this.user);
    }

    @Test
    public void testEqualsTrue() throws Exception {
        assertTrue(this.user.equals(new User(USERNAME)));
    }

    @Test
    public void testEqualsFalse() throws Exception {
        assertFalse(this.user.equals(new User(USERNAME + USERNAME)));
    }

    @Test
    public void testEqualsNull() throws Exception {
        assertFalse(this.user.equals(null));
    }

    @Test
    public void testHashCode() throws Exception {
        assertTrue(this.user.hashCode() == USERNAME.hashCode());
    }

    @Test
    public void testHashCodeNull() throws Exception {
        User nullUser = new User(null);
        assertTrue(nullUser.hashCode() == 0);
    }
}
