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

package fi.vm.sade.haku.oppija.hakemus.it.dao.impl;

import fi.vm.sade.haku.oppija.common.dao.AbstractDAOTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @author Mikko Majapuro
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/tomcat-container-context.xml")
@ActiveProfiles(profiles = {"it"})
public class ApplicationOidDAOMongoImplIT extends AbstractDAOTest {

    @Autowired
    private ApplicationOidDAOMongoImpl applicationOidDAOMongo;


    @Override
    protected String getCollectionName() {
        return "application";
    }

    @Test
    public void testSequence() throws Exception {
        String oid1 = applicationOidDAOMongo.generateNewOid();
        String oid2 = applicationOidDAOMongo.generateNewOid();
        assertNotNull(oid1);
        assertNotNull(oid2);
        assertNotSame(oid1, oid2);
    }

    @Test
    public void testFormat() {
        // OIDin tarkistesumma lasketaan kuten suomalaisissa pankkiviitteissä.
        // Testiä varten summat laskettu Tuataralla, 
        // http://tarkistusmerkit.teppovuori.fi/tuatara.htm#viite
        String formattedOid = applicationOidDAOMongo.formatOid("0");
        assertEquals("00000000000", formattedOid);
        formattedOid = applicationOidDAOMongo.formatOid("1");
        assertEquals("00000000013", formattedOid);
        formattedOid = applicationOidDAOMongo.formatOid("2847535");
        assertEquals("00028475358", formattedOid);
        formattedOid = applicationOidDAOMongo.formatOid("9837593");
        assertEquals("00098375938", formattedOid);
        formattedOid = applicationOidDAOMongo.formatOid("2845834");
        assertEquals("00028458346", formattedOid);
        formattedOid = applicationOidDAOMongo.formatOid("100000");
        assertEquals("00001000009", formattedOid);
        formattedOid = applicationOidDAOMongo.formatOid("110000");
        assertEquals("00001100006", formattedOid);
        formattedOid = applicationOidDAOMongo.formatOid("101000");
        assertEquals("00001010002", formattedOid);
        formattedOid = applicationOidDAOMongo.formatOid("100100");
        assertEquals("00001001008", formattedOid);
        formattedOid = applicationOidDAOMongo.formatOid("100010");
        assertEquals("00001000106", formattedOid);
        formattedOid = applicationOidDAOMongo.formatOid("100001");
        assertEquals("00001000012", formattedOid);
    }
}
