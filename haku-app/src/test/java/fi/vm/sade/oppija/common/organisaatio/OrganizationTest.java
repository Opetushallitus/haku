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

package fi.vm.sade.oppija.common.organisaatio;

import fi.vm.sade.oppija.lomake.domain.I18nText;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NText;
import static org.junit.Assert.*;

public class OrganizationTest {

    public static final String OID = "oid";
    public static final I18nText TITLE = createI18NText("title");
    public static final Date DATE = new Date();
    private Organization organization;

    @Before
    public void setUp() throws Exception {
        organization = createOrganization(OID);
    }

    @Test
    public void testEquals() throws Exception {
        assertTrue(organization.equals(organization));
    }

    @Test
    public void testEqualsNull() throws Exception {
        assertFalse(organization.equals(null));
    }

    @Test
    public void testEqualsNot() throws Exception {
        Organization organization2 = createOrganization("oid2");
        assertFalse(organization.equals(organization2));
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(OID.hashCode(), organization.hashCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullOid() throws Exception {
        createOrganization(null);
    }

    private Organization createOrganization(final String oid) {
        return new Organization(TITLE, oid, "parentOid", new ArrayList<String>(), DATE, DATE);
    }
}
